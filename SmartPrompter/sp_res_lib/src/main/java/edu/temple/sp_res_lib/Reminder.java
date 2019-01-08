package edu.temple.sp_res_lib;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import edu.temple.sp_res_lib.utils.BaseScheduleable;
import edu.temple.sp_res_lib.utils.Constants;

public class Reminder extends BaseScheduleable {

    private static final int REMINDER_REQUEST_CODE_OFFSET = 1000;

    private int alarmID, count;
    private Constants.REMINDER_TYPE type;

    public Reminder(int ID, int alarmID, String type, int count,
                    int year, int month, int day, int hour, int minute,
                    String action, String namespace, String className) {
        this.ID = ID;
        this.alarmID = alarmID;
        this.type = Constants.REMINDER_TYPE.valueOf(type);
        this.count = count;

        updateDate(year, month, day);
        updateTime(hour, minute);
        updateIntentSettings(action, namespace, className);
    }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    public int getRequestCode() { return (REMINDER_REQUEST_CODE_OFFSET + ID); }

    public int getAlarmID() { return alarmID; }

    public Constants.REMINDER_TYPE getType() { return type; }

    public String getTypeString() { return type.toString(); }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    public void incrementCount() { count++; }

    public int getCount() { return count; }

    public boolean hasReachedCountLimit() {
        return (count == Constants.getReminderLimit(type));
    }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    public void calculateNewReminder() {
        int interval = Constants.getReminderInterval(type);
        Log.i(Constants.LOG_TAG, "ORIGINAL reminder will go off at time: "
                + getTimeString() + " on date: " + getDateString()
                + " after interval: " + interval);

        cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Log.i(Constants.LOG_TAG, "CURRENT reminder time is: "
                + Constants.DATE_TIME_FORMAT.format(cal.getTime()));

        cal.add(Calendar.MINUTE, interval);
        Log.i(Constants.LOG_TAG, "ADJUSTED reminder time is: "
                + Constants.DATE_TIME_FORMAT.format(cal.getTime()));
    }

    public PendingIntent getPendingIntent(Context context) {
        Intent baseIntent = super.getBaseBroadcastIntent(context);
        baseIntent.putExtra(Constants.INTENT_EXTRA_REMINDER_ID, ID);
        baseIntent.putExtra(Constants.INTENT_EXTRA_ALARM_ID, alarmID);
        return PendingIntent.getBroadcast(context, getRequestCode(),
                baseIntent, super.PENDING_INTENT_FLAGS);
    }

    public String toString() {
        String date = getDateString();
        String time = getTimeString();
        return (type.toString() + " (" + date + " " + time + ")");
    }

}