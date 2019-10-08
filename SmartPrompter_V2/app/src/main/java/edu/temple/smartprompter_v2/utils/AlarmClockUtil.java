package edu.temple.smartprompter_v2.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import java.util.Calendar;
import java.util.List;

import edu.temple.smartprompter_v2.activities.MainActivity;
import edu.temple.smartprompter_v2.receivers.AlarmAlertReceiver;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.utils.AlarmUtil;
import edu.temple.sp_res_lib.utils.DateTimeUtil;
import edu.temple.sp_res_lib.utils.Log;
import edu.temple.sp_res_lib.utils.StorageUtil;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class AlarmClockUtil {

    public static void setAlarm(Context context, Alarm alarm) {
        long alarmTime = alarm.getAlarmTimeMillis();
        String alarmString = alarm.getAlarmDateTimeString();

        Log.e(LOG_TAG, "Setting alarm for task: " + alarm.getDesc()
                + " \t \t and GUID-int: " + alarm.getGuidInt()
                + " \t \t with date/time: " + alarmString);
        setAlarmClock(context, alarm, alarmTime, false);
    }

    public static void setReminder(Context context, Alarm alarm, Alarm.REMINDER type) {
        if (type == Alarm.REMINDER.Explicit) {
            Log.i(LOG_TAG, "User has explicitly snoozed alarm.  Resetting reminder count.");
            alarm.resetReminderCount();
        }

        if (!alarm.isReminderLimitReached()) {
            Log.i(LOG_TAG, "Alarm reminder limit hasn't been exceeded.  Setting "
                    + "reminder with type: " + type.toString());
            alarm.setReminderType(type);

            AlarmUtil.setReminderTime(alarm, type);
            long reminderTime = alarm.getReminderTimeMillis();
            String reminderString = alarm.getReminderDateTimeString();

            Log.e(LOG_TAG, "Setting reminder for task: " + alarm.getDesc()
                    + " \t \t and GUID-int: " + alarm.getGuidInt()
                    + " \t \t with date/time: " + reminderString);

            setAlarmClock(context, alarm, reminderTime, true);
            StorageUtil.writeAlarmToStorage(context, alarm);
        }
    }

    public static void setAllAlarms(Context context, List<Alarm> alarms) {
        Calendar now = Calendar.getInstance();
        Log.i(LOG_TAG, "Current time: " + DateTimeUtil.formatTime(now, DateTimeUtil.FORMAT.DateTime));

        for (Alarm alarm : alarms) {
            Log.i(LOG_TAG, "Setting new alarm clock for task: " + alarm.getDesc()
                    + "\n \t Current time millis: " + now.getTimeInMillis()
                    + "\n \t Alarm time millis: " + alarm.getAlarmTimeMillis());

            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (alarm.getAlarmTimeMillis() > currentTime)
                setAlarm(context, alarm);
            else {
                Log.i(LOG_TAG, "Original alarm time has passed... Checking for active reminders...");
                if (alarm.hasReminder()) {
                    if (alarm.getReminderTimeMillis() < currentTime)
                        Log.e(LOG_TAG, "Alarm has active reminder, but reminder time has passed.");
                    else setReminder(context, alarm, alarm.getReminderType());
                }
            }
        }
    }

    public static void cancelAlarm(Context context, Alarm alarm) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager != null) {
            Log.i(LOG_TAG, "Cancelling existing alarms for GUID-int: " + alarm.getGuidInt());
            manager.cancel(Alarm.getPI(context, alarm, AlarmAlertReceiver.class, false));
        }
    }


    // ====================================================================================


    private static void setAlarmClock(Context context, Alarm alarm, long alarmTime, boolean isReminder) {
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert (mgr != null);

        PendingIntent notificationPI = Alarm.getPI(context, alarm, MainActivity.class, isReminder);
        PendingIntent receiverPI = Alarm.getPI(context, alarm, AlarmAlertReceiver.class, isReminder);
        mgr.setAlarmClock(new AlarmManager.AlarmClockInfo(alarmTime, notificationPI), receiverPI);
    }

}