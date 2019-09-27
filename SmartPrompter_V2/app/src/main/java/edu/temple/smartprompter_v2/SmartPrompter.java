package edu.temple.smartprompter_v2;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.PowerManager;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.temple.smartprompter_v2.utils.AlarmClockUtil;
import edu.temple.smartprompter_v2.services.DownloadService;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.smartprompter_v2.services.FileMonitorService;
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

        MediaUtil.stopAlarmAlerts(getApplicationContext());
    }

    public void initializeFromReboot() {
        // TODO - figure out how to guarantee that SP patient app is always checking for updates
        getAlarmsFromStorage();
        AlarmClockUtil.setAllAlarms(getApplicationContext(), currentAlarms);
        AlarmClockUtil.setAllAlarms(getApplicationContext(), futureAlarms);
    }

    public void cleanupDirtyAlarms() {
        StorageUtil.deleteDirtyFlag(this);
        getAlarmsFromStorage();
        AlarmClockUtil.setAllAlarms(getApplicationContext(), currentAlarms);
        AlarmClockUtil.setAllAlarms(getApplicationContext(), futureAlarms);
    }

    public Alarm getAlarm(String guid) {
        return getAlarm(guid, false);
    }

    public Alarm getAlarm(String guid, boolean forAlert) {
        if (forAlert) {
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
        }

        for (Alarm alarm : currentAlarms) {
            if (alarm.getGuid().equals(guid))
                return alarm;
        }

        return null;
    }

    public ArrayList<Alarm> getCurrentAlarms() {
        getAlarmsFromStorage();
        return currentAlarms;
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

    public void setAlarmReminder(Alarm alarm, Alarm.REMINDER type) {
        alarm.setReminder(type);
        AlarmClockUtil.setAlarm(getApplicationContext(), alarm, true);
    }

    public void cancelAlarm(Alarm alarm) {
        AlarmClockUtil.cancelAlarm(getApplicationContext(), alarm);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void getAlarmsFromStorage() {
        // TODO - come back to this, I don't like how it's laid out ...

        Log.i(LOG_TAG, "Retrieving alarm records from storage!");
        futureAlarms = new ArrayList<>();       // these alarms will be going off in the future
        currentAlarms = new ArrayList<>();      // these alarms have gone off, user must complete

        ArrayList<Alarm> allAlarms = StorageUtil.getAlarmsFromStorage(this);
        for (Alarm alarm : allAlarms) {
            if (FUTURE_STATUSES.contains(alarm.getStatus())) futureAlarms.add(alarm);
            else if (CURRENT_STATUSES.contains(alarm.getStatus())) currentAlarms.add(alarm);
        }
    }

}