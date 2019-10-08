package edu.temple.sp_res_lib.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.temple.sp_res_lib.obj.Alarm;

import static edu.temple.sp_res_lib.utils.Constants.LOG_TAG;

public class AlarmUtil {

    public static ArrayList<Alarm> getAlarmsFromStorage(Context ctx) {
        Log.i(LOG_TAG, "Retrieving alarm records from storage!");
        return StorageUtil.getAlarmsFromStorage(ctx);
    }

    public static ArrayList<Alarm> getAlarmsFromStorage(Context ctx,
                                                        List<Alarm.STATUS> alarmStatuses) {
        Log.i(LOG_TAG, "Retrieving alarm records from storage!");
        ArrayList<Alarm> alarms = new ArrayList<>();
        for (Alarm alarm : StorageUtil.getAlarmsFromStorage(ctx)) {
            if (alarmStatuses.contains(alarm.getStatus())) alarms.add(alarm);
        }
        return alarms;
    }

    public static void updateStatus(Context ctx, Alarm alarm, Alarm.STATUS newStatus) {
        alarm.updateStatus(newStatus);

        switch (newStatus) {
            case Incomplete:
                alarm.setTimeAcknowledged();
                Log.i(LOG_TAG, "Alarm with GUID: " + alarm.getGuid()
                        + "\t was acknowledged at time: "
                        + alarm.getAcknowledgedDateTimeString());
                break;
            case Complete:
                alarm.setArchived(true);
                alarm.setTimeCompleted();
                Log.i(LOG_TAG, "Alarm with GUID: " + alarm.getGuid()
                        + "\t was completed at time: "
                        + alarm.getCompletionDateTimeString());
                break;
        }

        StorageUtil.writeAlarmToStorage(ctx, alarm);
    }

    public static void setReminderTime(Alarm alarm, Alarm.REMINDER reminderType) {
        Calendar reminderCal = Calendar.getInstance();
        reminderCal.set(Calendar.SECOND, 0);

        alarm.setReminderType(reminderType);
        switch (reminderType) {
            case None:
                alarm.setReminderTime(null);
                return;
            case Explicit:
                alarm.resetReminderCount();
                reminderCal.add(Calendar.MILLISECOND,
                        Constants.REMINDER_DURATION_EXP.intValue());
                Log.i(LOG_TAG, "User has explicitly set a reminder.  "
                        + "Resetting reminder count for alarm: " + alarm.getDesc());
                break;
            case Implicit:
                int reminderCount = alarm.incrementReminderCount();
                reminderCal.add(Calendar.MILLISECOND,
                        Constants.REMINDER_DURATION_IMP.intValue());
                Log.i(LOG_TAG, "Setting implicit reminder #"
                        + reminderCount + " for alarm: " + alarm.getDesc());
                break;
        }

        alarm.setReminderTime(reminderCal);
    }
}
