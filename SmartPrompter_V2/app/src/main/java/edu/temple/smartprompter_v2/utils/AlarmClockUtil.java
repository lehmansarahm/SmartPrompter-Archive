package edu.temple.smartprompter_v2.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import java.util.Calendar;
import java.util.List;

import edu.temple.smartprompter_v2.activities.MainActivity;
import edu.temple.smartprompter_v2.receivers.AlarmAlertReceiver;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.utils.DateTimeUtil;
import edu.temple.sp_res_lib.utils.Log;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class AlarmClockUtil {

    public static void setAlarm(Context context, Alarm alarm, boolean isReminder) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        long alarmTime = (isReminder ? alarm.getReminderTimeMillis() : alarm.getAlarmTimeMillis());
        String alarmString = (isReminder ? alarm.getReminderDateTimeString() : alarm.getAlarmDateTimeString());

        if (manager != null) {
            Log.e(LOG_TAG, "Setting " + (isReminder ? "reminder" : "alarm")
                    + " for task: " + alarm.getDesc()
                    + " \t \t and GUID-int: " + alarm.getGuidInt()
                    + " \t \t with date/time: " + alarmString);
            PendingIntent notificationPI = alarm.getPI(context, MainActivity.class, isReminder);
            PendingIntent receiverPI = alarm.getPI(context, AlarmAlertReceiver.class, isReminder);
            manager.setAlarmClock(new AlarmManager.AlarmClockInfo(alarmTime, notificationPI), receiverPI);
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
                setAlarm(context, alarm, false);
            else {
                Log.i(LOG_TAG, "Original alarm time has passed... Checking for active reminders...");
                if (alarm.hasReminder()) {
                    if (alarm.getReminderTimeMillis() < currentTime)
                        Log.e(LOG_TAG, "Alarm has active reminder, but reminder time has passed.");
                    else setAlarm(context, alarm, true);
                }
            }
        }
    }

    public static void cancelAlarm(Context context, Alarm alarm) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager != null) {
            Log.i(LOG_TAG, "Cancelling existing alarms for GUID-int: " + alarm.getGuidInt());
            manager.cancel(alarm.getPI(context, AlarmAlertReceiver.class, false));
        }
    }

}