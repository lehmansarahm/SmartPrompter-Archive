package edu.temple.smartprompter_v2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.PowerManager;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.temple.smartprompter_v2.receivers.AutoArchiveReceiver;
import edu.temple.smartprompter_v2.utils.AlarmClockUtil;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.smartprompter_v2.services.FileMonitorService;
import edu.temple.sp_res_lib.utils.AlarmUtil;
import edu.temple.sp_res_lib.utils.DateTimeUtil;
import edu.temple.sp_res_lib.utils.Log;
import edu.temple.sp_res_lib.utils.MediaUtil;
import edu.temple.sp_res_lib.utils.StorageUtil;

public class SmartPrompter extends Application {

    public static final String LOG_TAG = "SmartPrompterV2";
    public static final String WAKELOCK_TAG = "smartprompter:wakelock";

    public static final int ARCHIVE_REQUEST_CODE = 5396306;
    public static final boolean ARCHIVE_TEST = false;

    private static final long WAKELOCK_TIMEOUT = TimeUnit.SECONDS.toMillis(30);
    private static final int ALERT_FLAGS = (WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

    private static final List<Alarm.STATUS> FUTURE_STATUSES =
            Arrays.asList(Alarm.STATUS.New, Alarm.STATUS.Active);
    private static final List<Alarm.STATUS> CURRENT_STATUSES =
            Arrays.asList(Alarm.STATUS.Unacknowledged, Alarm.STATUS.Incomplete);

    private ArrayList<Alarm> futureAlarms, currentAlarms;
    private PowerManager.WakeLock wakeLock;

    private Context appCtx;


    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------


    @Override
    public void onCreate() {
        super.onCreate();
        appCtx = getApplicationContext();

        // TODO - start up DownloadService again if it ever becomes a thing
        // Intent downloadServiceIntent = new Intent(appCtx, DownloadService.class);
        Intent fileMonitorServiceIntent = new Intent(appCtx, FileMonitorService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // startForegroundService(downloadServiceIntent);
            startForegroundService(fileMonitorServiceIntent);
        } else {
            // startService(downloadServiceIntent);
            startService(fileMonitorServiceIntent);
        }

        // ExportReceiver.scheduleExport(appCtx);
    }

    public void wakeup(Activity context, boolean playAlerts, MediaUtil.AUDIO_TYPE audioType) {
         if (playAlerts) {
             Log.i(LOG_TAG, "Playing alarm alerts...");
             MediaUtil.playAlarmAlerts(context, audioType, null);
         } else {
             Log.i(LOG_TAG, "Stopping alarm alerts...");
             MediaUtil.stopAlarmAlerts(getApplicationContext());
         }

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
        if (wakeLock != null && wakeLock.isHeld()) {
            Log.i(LOG_TAG, "Releasing wake lock!");
            wakeLock.release();
        }

        MediaUtil.stopAlarmAlerts(appCtx);
    }

    public void initializeFromReboot() {
        updateAllAlarmsFromStorage();
        scheduleAutoArchive();
    }

    public void cleanupDirtyAlarms() {
        StorageUtil.deleteDirtyFlag(appCtx);
        updateAllAlarmsFromStorage();
    }


    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------


    public Alarm getAlarm(String guid) {
        return getAlarm(guid, false);
    }

    public Alarm getAlarm(String guid, boolean forAlert) {
        updateAllAlarmsFromStorage();

        if (forAlert) {
            for (Alarm alarm : futureAlarms) {
                if (alarm.getGuid().equals(guid)) {
                    if (alarm.getStatus().equals(Alarm.STATUS.Active)) {
                        futureAlarms.remove(alarm);
                        currentAlarms.add(alarm);
                        updateAlarm(guid, Alarm.STATUS.Unacknowledged);
                        Log.i(LOG_TAG, "First time getting alert for this alarm.  Status "
                                + "set to UNACKNOWLEDGED.");
                    }
                    return alarm;
                }
            }
        }

        for (Alarm alarm : currentAlarms) {
            if (alarm.getGuid().equals(guid))
                return alarm;
        }

        return null;
    }

    public ArrayList<Alarm> getCurrentAlarms() {
        currentAlarms = AlarmUtil.getAlarmsFromStorage(appCtx, CURRENT_STATUSES);
        return currentAlarms;
    }

    public ArrayList<Alarm> getTodaysActiveAlarms() {
        Calendar today = Calendar.getInstance();
        ArrayList<Alarm> todaysAlarms = new ArrayList<>();

        ArrayList<Alarm> allAlarms = AlarmUtil.getAlarmsFromStorage(appCtx);
        for (Alarm alarm : allAlarms) {
            int[] alarmDate = alarm.getAlarmDate();
            Log.e(LOG_TAG, "Examining record: " + alarm.toString());

            if (alarmDate[0] == today.get(Calendar.YEAR) &&
                    alarmDate[1] == today.get(Calendar.MONTH) &&
                    alarmDate[2] == today.get(Calendar.DAY_OF_MONTH)) {
                Log.e(LOG_TAG, "Found matching record for today: " + alarm.toString());
                todaysAlarms.add(alarm);
            }
        }

        return todaysAlarms;
    }

    public ArrayList<Alarm> getTodaysPastAlarms() {
        ArrayList<Alarm> todaysAlarms = new ArrayList<>();
        ArrayList<Alarm> allAlarms = StorageUtil.getInactiveAlarmsFromStorage(appCtx);

        for (Alarm alarm : allAlarms) {
            Log.e(LOG_TAG, "Examining record: " + alarm.toString());
            if (alarm.isScheduledForToday()) {
                Log.e(LOG_TAG, "Found matching record for today: " + alarm.toString());
                todaysAlarms.add(alarm);
            }
        }

        return todaysAlarms;
    }


    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------


    public void updateAlarm(String alarmGUID, Alarm.STATUS newStatus) {
        for (Alarm alarm : currentAlarms) {
            if (alarm.getGuid().equals(alarmGUID)) {
                AlarmUtil.updateStatus(appCtx, alarm, newStatus);
            }
        }
    }

    public void setAlarmReminder(Alarm alarm, Alarm.REMINDER type) {
        AlarmClockUtil.setReminder(appCtx, alarm, type, true);
    }

    public void cancelAlarm(String alarmGUID) {
        cancelAlarm(getAlarm(alarmGUID));
    }

    public void cancelAlarm(Alarm alarm) {
        AlarmClockUtil.cancelAlarm(appCtx, alarm);
    }

    public void saveTaskImage(String filename, byte[] bytes) {
        Log.i(LOG_TAG, "Attempting to save file: " + filename);
        Bitmap media = MediaUtil.convertToBitmap(bytes);
        StorageUtil.writeImageToFile(appCtx, filename, media);
    }

    public static long getNextAutoArchiveTime() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.MILLISECOND, 0);
        now.set(Calendar.SECOND, 0);

        if (ARCHIVE_TEST) {
            now.add(Calendar.MINUTE, 5);
        } else {
            now.add(Calendar.DAY_OF_MONTH, 1);
            now.set(Calendar.HOUR, 0);
            now.set(Calendar.MINUTE, 0);
            now.set(Calendar.AM_PM, 0);
        }

        return now.getTimeInMillis();
    }


    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------


    private void updateAllAlarmsFromStorage() {
        currentAlarms = AlarmUtil.getAlarmsFromStorage(appCtx, CURRENT_STATUSES);
        AlarmClockUtil.setAllAlarms(appCtx, currentAlarms);

        futureAlarms = AlarmUtil.getAlarmsFromStorage(appCtx, FUTURE_STATUSES);
        AlarmClockUtil.setAllAlarms(appCtx, futureAlarms);
    }

    private void scheduleAutoArchive() {
        AlarmManager mgr = (AlarmManager) appCtx.getSystemService(Context.ALARM_SERVICE);
        assert (mgr != null);

        Intent receiverIntent = new Intent(appCtx, AutoArchiveReceiver.class);
        PendingIntent receiverPI = PendingIntent.getBroadcast(appCtx, ARCHIVE_REQUEST_CODE,
                receiverIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long alarmTime = getNextAutoArchiveTime();
        long intervalTime = ARCHIVE_TEST
                ?  TimeUnit.MINUTES.toMillis(5)
                :  TimeUnit.HOURS.toMillis(24);

        Log.e(LOG_TAG, "Setting recurring auto-archive alarm for time: "
                + DateTimeUtil.formatTimeInMillis(alarmTime, DateTimeUtil.FORMAT.DateTime)
                + "\t\t with an interval of: " + intervalTime + " ms");
        mgr.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, intervalTime, receiverPI);
    }

}