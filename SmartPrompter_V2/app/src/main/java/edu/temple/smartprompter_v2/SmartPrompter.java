package edu.temple.smartprompter_v2;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import edu.temple.smartprompter_v2.activities.MainActivity;
import edu.temple.smartprompter_v2.receivers.AlarmAlertReceiver;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.utils.DateTimeUtil;
import edu.temple.sp_res_lib.utils.Log;
import edu.temple.sp_res_lib.utils.MediaUtil;
import edu.temple.sp_res_lib.utils.StorageUtil;

public class SmartPrompter extends Application {

    public static final String LOG_TAG = "SmartPrompterV2";

    private static final List<Alarm.STATUS> FUTURE_STATUSES =
            Arrays.asList(Alarm.STATUS.Active);

    private static final List<Alarm.STATUS> CURRENT_STATUSES =
            Arrays.asList(Alarm.STATUS.Unacknowledged, Alarm.STATUS.Incomplete);

    private ArrayList<Alarm> futureAlarms, currentAlarms;

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    // TODO - figure out how to guarantee that SP patient app is always checking for updates

    public void initializeFromReboot() {
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
                    Log.i(LOG_TAG, "First time getting alert for this alarm.  Setting "
                            + "status to UNACKNOWLEDGED.");
                    updateAlarmStatus(guid, Alarm.STATUS.Unacknowledged);
                    futureAlarms.remove(alarm);
                    currentAlarms.add(alarm);
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
            Log.e(LOG_TAG, "Setting alarm for task: " + alarm.getDesc()
                    + " \t \t and GUID-int: " + alarm.getGuidInt()
                    + " \t \t with date/time: " + alarmString);
            PendingIntent notificationPI = alarm.getPI(context, MainActivity.class);
            PendingIntent receiverPI = alarm.getPI(context, AlarmAlertReceiver.class);
            manager.setAlarmClock(new AlarmManager.AlarmClockInfo(alarmTime, notificationPI), receiverPI);
        }
    }

}