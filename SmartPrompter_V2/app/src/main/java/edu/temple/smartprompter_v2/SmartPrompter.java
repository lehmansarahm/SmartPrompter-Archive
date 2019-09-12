package edu.temple.smartprompter_v2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.temple.smartprompter_v2.activities.MainActivity;
import edu.temple.smartprompter_v2.receivers.AlarmAlertReceiver;
import edu.temple.smartprompter_v2.services.DownloadService;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.smartprompter_v2.services.FileMonitorService;
import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.DateTimeUtil;
import edu.temple.sp_res_lib.utils.Log;
import edu.temple.sp_res_lib.utils.MediaUtil;
import edu.temple.sp_res_lib.utils.StorageUtil;

public class SmartPrompter extends Application {

    public static final String LOG_TAG = "SmartPrompterV2";
    public static final String WAKELOCK_TAG = "smartprompter:wakelock";

    private static final long WAKELOCK_TIMEOUT = TimeUnit.SECONDS.toMillis(30);

    private static final int ALERT_FLAGS = (WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

    private static final List<Alarm.STATUS> FUTURE_STATUSES =
            Arrays.asList(Alarm.STATUS.New, Alarm.STATUS.Active);
    private static final List<Alarm.STATUS> CURRENT_STATUSES =
            Arrays.asList(Alarm.STATUS.Unacknowledged, Alarm.STATUS.Incomplete);

    private final MediaPlayer mediaPlayer = new MediaPlayer();
    private ArrayList<Alarm> futureAlarms, currentAlarms;
    private PowerManager.WakeLock wakeLock;

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onCreate() {
        super.onCreate();
        Intent downloadServiceIntent = new Intent(this, DownloadService.class);
        Intent fileMonitorServiceIntent = new Intent(this, FileMonitorService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(downloadServiceIntent);
            startForegroundService(fileMonitorServiceIntent);
        } else {
            startService(downloadServiceIntent);
            startService(fileMonitorServiceIntent);
        }

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        Log.e(LOG_TAG, "Launching on device with dp height: " + dpHeight
                + " \t\t and dp width: " + dpWidth);
    }

    public void wakeup(Activity context) {
        Log.i(LOG_TAG, "Playing alarm alerts...");
        playAlarmAlerts(context);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            Log.i(LOG_TAG, "Acquiring wake lock...");
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, WAKELOCK_TAG);
            wakeLock.acquire(WAKELOCK_TIMEOUT);
        }

        Log.i(LOG_TAG, "Turning on device screen...");
        context.getWindow().addFlags(ALERT_FLAGS);
    }

    public void stopWakeup() {
        if (wakeLock != null) {
            Log.i(LOG_TAG, "Releasing wake lock!");
            wakeLock.release();
        }

        if (mediaPlayer.isPlaying()) {
            Log.i(LOG_TAG, "Stopping alarm alert tone!");
            mediaPlayer.stop();
        }

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            Log.i(LOG_TAG, "Stopping vibrate!");
            vibrator.cancel();
        }
    }

    public void initializeFromReboot() {
        // TODO - figure out how to guarantee that SP patient app is always checking for updates
        getAlarmsFromStorage();
        setAlarmClocks();
    }

    public void cleanupDirtyAlarms() {
        StorageUtil.deleteDirtyFlag(this);
        getAlarmsFromStorage();
        setAlarmClocks();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public Alarm getAlarm(String guid) {
        for (Alarm alarm : currentAlarms) {
            if (alarm.getGuid().equals(guid))
                return alarm;
        }
        return null;
    }

    public ArrayList<Alarm> getAlarms() {
        getAlarmsFromStorage();
        return this.currentAlarms;
    }

    public void updateAlarmStatus(String alarmGUID, Alarm.STATUS newStatus) {
        for (Alarm alarm : currentAlarms) {
            if (alarm.getGuid().equals(alarmGUID))
                alarm.updateStatus(newStatus);
        }
        StorageUtil.writeAlarmsToStorage(this, currentAlarms);
    }

    public void saveTaskImage(String filename, byte[] bytes) {
        Log.i(LOG_TAG, "Attempting to save file: " + filename);
        Bitmap media = MediaUtil.convertToBitmap(bytes);
        StorageUtil.writeImageToFile(this, filename, media);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public Alarm getAlarmForAlert(String guid) {
        // TODO - figure out a better way to do this because this is gross ...
        getAlarmsFromStorage();

        for (Alarm alarm : futureAlarms) {
            if (alarm.getGuid().equals(guid)) {
                if (alarm.getStatus().equals(Alarm.STATUS.Active)) {
                    futureAlarms.remove(alarm);
                    currentAlarms.add(alarm);
                    updateAlarmStatus(guid, Alarm.STATUS.Unacknowledged);
                    Log.i(LOG_TAG, "First time getting alert for this alarm.  Status "
                            + "set to UNACKNOWLEDGED.");
                }
                return alarm;
            }
        }

        for (Alarm alarm : currentAlarms) {
            if (alarm.getGuid().equals(guid))
                return alarm;
        }

        return null;
    }

    public void setAlarmReminder(Alarm alarm, Alarm.REMINDER type) {
        alarm.setReminder(type);
        setAlarmClock(alarm, true);
    }

    public void cancelAlarm(Alarm alarm) {
        Context context = getApplicationContext();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager != null) {
            Log.i(LOG_TAG, "Cancelling existing alarms for GUID-int: " + alarm.getGuidInt());
            manager.cancel(alarm.getPI(context, AlarmAlertReceiver.class));
        }
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void playAlarmAlerts(Activity context) {
        if (Constants.PLAY_ALARM_TONE) {
            try {
                mediaPlayer.setDataSource(context, Constants.ALARM_ALERT_TONE);

                final AudioManager audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if (audioMgr.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.prepare();

                    Log.i(LOG_TAG, "Playing alarm alert tone!");
                    mediaPlayer.start();
                }

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() { stopWakeup(); }
                }, Constants.ALARM_ALERT_DURATION);
            } catch(Exception e) {
                Log.e(LOG_TAG, "Something went wrong when trying to launch the alarm "
                        + "ALARM_ALERT_TONE ringtone!", e);
            }
        }

        if (Constants.PLAY_ALARM_VIBRATE) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                Log.i(LOG_TAG, "Starting alarm alert vibrate!");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    vibrator.vibrate(VibrationEffect.createOneShot(Constants.ALARM_ALERT_DURATION,
                            VibrationEffect.DEFAULT_AMPLITUDE));
                else vibrator.vibrate(Constants.ALARM_ALERT_DURATION);
            }
        }
    }

    private void getAlarmsFromStorage() {
        // TODO - come back to this, I don't like how it's laid out ...

        Log.i(LOG_TAG, "Retrieving alarm records from storage!");
        futureAlarms = new ArrayList<>();       // these alarms will be going off in the future
        currentAlarms = new ArrayList<>();      // these alarms have gone off, user must complete

        ArrayList<Alarm> allAlarms = StorageUtil.getAlarmsFromStorage(this);
        for (Alarm alarm : allAlarms) {
            if (FUTURE_STATUSES.contains(alarm.getStatus()))
                futureAlarms.add(alarm);
            else if (CURRENT_STATUSES.contains(alarm.getStatus()))
                currentAlarms.add(alarm);
        }
    }

    private void setAlarmClocks() {
        Calendar now = Calendar.getInstance();
        Log.i(LOG_TAG, "Current time: " + DateTimeUtil.formatTime(now, DateTimeUtil.FORMAT.DateTime));

        for (Alarm alarm : futureAlarms) {
            Log.i(LOG_TAG, "Setting new alarm clock for future task: " + alarm.getDesc()
                    + "\n \t Current time millis: " + now.getTimeInMillis()
                    + "\n \t Alarm time millis: " + alarm.getAlarmTimeMillis());
            if (alarm.getAlarmTimeMillis() < Calendar.getInstance().getTimeInMillis())
                Log.e(LOG_TAG, "CAN'T SET ALARM FOR TIME IN THE PAST.");
            else setAlarmClock(alarm, false);
        }

        // TODO - check "current alarms" and set reminders where necessary
        for (Alarm alarm : currentAlarms) {
            Log.i(LOG_TAG, "Setting new alarm clock for current task: " + alarm.getDesc()
                    + "\n \t Current time millis: " + now.getTimeInMillis()
                    + "\n \t Alarm time millis: " + alarm.getAlarmTimeMillis());

            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (alarm.getAlarmTimeMillis() > currentTime)
                Log.e(LOG_TAG, "WHY DOES CURRENT ALARM HAVE FUTURE TIME???  "
                        + "ARE YOU A TIME TRAVELER???");
            if (alarm.hasReminder()) {
                if (alarm.getReminderTimeMillis() < currentTime)
                    Log.e(LOG_TAG, "CAN'T SET REMINDER FOR TIME IN THE PAST.");
                else setAlarmClock(alarm, true);
            }
        }
    }

    private void setAlarmClock(Alarm alarm, boolean isReminder) {
        Context context = getApplicationContext();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        long alarmTime = (isReminder ? alarm.getReminderTimeMillis() : alarm.getAlarmTimeMillis());
        String alarmString = (isReminder ? alarm.getReminderDateTimeString() : alarm.getAlarmDateTimeString());

        if (manager != null) {
            Log.e(LOG_TAG, "Setting " + (isReminder ? "reminder" : "alarm")
                    + " for task: " + alarm.getDesc()
                    + " \t \t and GUID-int: " + alarm.getGuidInt()
                    + " \t \t with date/time: " + alarmString);
            PendingIntent notificationPI = alarm.getPI(context, MainActivity.class);
            PendingIntent receiverPI = alarm.getPI(context, AlarmAlertReceiver.class);
            manager.setAlarmClock(new AlarmManager.AlarmClockInfo(alarmTime, notificationPI), receiverPI);
        }
    }

}