package edu.temple.smartprompter_v3.res_lib;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import edu.temple.smartprompter_v3.res_lib.data.Alarm;
import edu.temple.smartprompter_v3.res_lib.data.FirebaseConnector;
import edu.temple.smartprompter_v3.res_lib.utils.MediaUtil;
import edu.temple.smartprompter_v3.res_lib.utils.StorageUtil;

import static edu.temple.smartprompter_v3.res_lib.utils.Constants.LOG_TAG;

public class SpController {

    // TODO - come up with a better name for this method ...
    public static boolean isAlarmLive(Alarm alarm) {
        Log.i(LOG_TAG, "Testing alarm liveness with alarm time: " + alarm.getAlarmDateTimeString()
                + " \t \t and reminder time: " + alarm.getReminderDateTimeString());
        return !(alarm.hasAlarmTimePassed() && alarm.hasReminderTimePassed());
    }

    public static boolean isAlarmSet(Context context, Alarm alarm,
                                     Class<?> responseClass, boolean isReminder) {
        Intent intent = alarm.getIntent(context, responseClass, isReminder);
        return (PendingIntent.getBroadcast(context, alarm.getRequestCode(),
                intent, PendingIntent.FLAG_NO_CREATE) != null);
    }

    public static void setAlarm(Context context, Alarm alarm,
                                Class<?> notificationClass, Class<?> receiverClass) {
        long alarmTime = alarm.getAlarmTimeMillis();
        String alarmString = alarm.getAlarmDateTimeString();
        setAlarmClock(context, alarm, notificationClass, receiverClass,
                alarmTime, false);

        Log.i(LOG_TAG, "Alarm set for record with:" +
                " \n \t Description: " + alarm.getDesc() +
                " \n \t GUID : " + alarm.getGuid() +
                " \n \t Request code : " + alarm.getRequestCode() +
                " \n \t Date/time: " + alarmString);
    }

    public static void cancelAlarm(Context context, Alarm alarm, Class<?> receiverClass) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager != null) {
            Log.i(LOG_TAG, "Cancelling existing alarms for record with:" +
                    " \n \t Description: " + alarm.getDesc() +
                    " \n \t GUID : " + alarm.getGuid() +
                    " \n \t Request code : " + alarm.getRequestCode());
            manager.cancel(getAlarmPI(context, alarm, receiverClass, false));
        }
    }


    // -----------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------


    public static void setReminder(Context context, Alarm alarm,
                                   Class<?> notificationClass, Class<?> receiverClass,
                                   Alarm.REMINDER type, boolean increaseReminderCount) {
        if (type == Alarm.REMINDER.Explicit) {
            Log.i(LOG_TAG, "User has explicitly snoozed alarm.  Resetting reminder count.");
            alarm.resetReminderCount();
        }

        if (!alarm.isReminderLimitReached()) {
            Log.i(LOG_TAG, "Alarm reminder limit hasn't been exceeded.  Setting "
                    + "reminder with type: " + type.toString());
            alarm.setReminderTime(type, increaseReminderCount);
            String reminderString = alarm.getReminderDateTimeString();
            long reminderTime = alarm.getReminderTimeMillis();

            Log.e(LOG_TAG, "Setting reminder for record with:" +
                    " \n \t Description: " + alarm.getDesc() +
                    " \n \t GUID : " + alarm.getGuid() +
                    " \n \t Request code : " + alarm.getRequestCode() +
                    " \n \t Date/time: " + reminderString);

            setAlarmClock(context, alarm, notificationClass, receiverClass,
                    reminderTime, true);
            FirebaseConnector.saveAlarm(alarm, null,
                    (error) -> Log.e(LOG_TAG, "Something went wrong while attempting to "
                            + "save alarm with new reminder.", error));
        } else {
            Log.e(LOG_TAG, "REMINDER LIMIT REACHED.  CANNOT SET MORE REMINDERS.");
        }
    }

    public static void cancelReminder(Context context, Alarm alarm, Class<?> receiverClass) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager != null) {
            Log.i(LOG_TAG, "Cancelling existing reminders for record with:" +
                    " \n \t Description: " + alarm.getDesc() +
                    " \n \t GUID : " + alarm.getGuid() +
                    " \n \t Request code : " + alarm.getRequestCode());
            manager.cancel(getAlarmPI(context, alarm, receiverClass, true));
        }
    }


    // -----------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------

    public static void markUnacknowledged(Alarm alarm) {
        alarm.updateStatus(Alarm.STATUS.Unacknowledged);
        FirebaseConnector.saveAlarm(alarm,
                result -> Log.i(LOG_TAG, "Alarm with GUID: " + alarm.getGuid()
                        + " has gone off.  Task is now 'unacknowledged'."),
                (error) -> Log.e(LOG_TAG, "Something went wrong while trying to save 'unacknowledged' "
                        + "status update for alarm with GUID: " + alarm.getGuid(), error));
        ;
    }

    public static void markAcknowledged(Context context, Alarm alarm,
                                        Class<?> notificationClass, Class<?> receiverClass) {
        alarm.setTimeAcknowledged();
        alarm.resetReminderCount();
        alarm.updateStatus(Alarm.STATUS.Incomplete);

        // cancel any lingering acknowledgment alarms and kick-off implicit completion reminders
        cancelAlarm(context, alarm, receiverClass);
        setReminder(context, alarm, notificationClass, receiverClass,
                Alarm.REMINDER.Implicit, true);

        FirebaseConnector.saveAlarm(alarm,
                result -> Log.i(LOG_TAG, "Alarm with GUID: " + alarm.getGuid()
                        + " has been acknowledged."),
                (error) -> Log.e(LOG_TAG, "Something went wrong while trying to save 'acknowledged' "
                        + "status update for alarm with GUID: " + alarm.getGuid(), error));
    }

    public static void markCompleted(Context context, Alarm alarm,
                                     Class<?> receiverClass, byte[] bytes) {
        // cancel any lingering completion alarms
        cancelAlarm(context, alarm, receiverClass);
        cancelReminder(context, alarm, receiverClass);

        alarm.setTimeCompleted();
        alarm.resetReminderCount();
        alarm.updateStatus(Alarm.STATUS.Complete);

        Log.e(LOG_TAG, "Attempting to save photo to path: " + alarm.getPhotoPath());
        Bitmap media = MediaUtil.convertToBitmap(bytes);
        StorageUtil.writeImageToFile(alarm.getPhotoPath(), media);

        FirebaseConnector.saveAlarm(alarm,
                result -> Log.i(LOG_TAG, "Alarm with GUID: " + alarm.getGuid()
                        + " has been completed."),
                (error) -> Log.e(LOG_TAG, "Something went wrong while trying to save 'complete' "
                        + "status update for alarm with GUID: " + alarm.getGuid(), error));
    }


    // -----------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------


    private static void setAlarmClock(Context context, Alarm alarm,
                                      Class<?> notificationClass, Class<?> receiverClass,
                                      long alarmTime, boolean isReminder) {
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert (mgr != null);

        PendingIntent notificationPI = getAlarmPI(context, alarm, notificationClass, isReminder);
        PendingIntent receiverPI = getAlarmPI(context, alarm, receiverClass, isReminder);
        mgr.setAlarmClock(new AlarmManager.AlarmClockInfo(alarmTime, notificationPI), receiverPI);
    }

    private static PendingIntent getAlarmPI(Context context, Alarm alarm,
                                           Class<?> responseClass, boolean isReminder) {
        Intent intent = alarm.getIntent(context, responseClass, isReminder);
        return PendingIntent.getBroadcast(context, alarm.getRequestCode(),
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

}