package edu.temple.sp_res_lib;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import edu.temple.sp_res_lib.utils.Constants;

public class Alarm {

    public static final String INTENT_EXTRA_ALARM_ID = "intent_extra_request_code";
    public static final String INTENT_EXTRA_ORIG_TIME = "intent_extra_orig_time";

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");

    public enum STATUS { New, Active, Unacknowledged, Incomplete, Complete }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    private static final int BASE_INTENT_FLAGS = Intent.FLAG_INCLUDE_STOPPED_PACKAGES;
    private static final int PENDING_INTENT_FLAGS = PendingIntent.FLAG_CANCEL_CURRENT;

    private static final int DEFAULT_SECONDS = 0;
    private static final int DEFAULT_MILLISECONDS = 0;

    private int ID;
    private Calendar cal;
    private String label;
    private STATUS status;

    private String action;
    private String receiverNamespace;
    private String receiverClassName;

    private String timeAcknowledged;
    private String timeCompleted;

    public Alarm(int ID, String label, String status, int year, int month, int day,
                 int hour, int minute, String action, String namespace, String className,
                 String timeAcknowledged, String timeCompleted) {

        this.ID = ID;
        this.label = label;
        this.status = STATUS.valueOf(status);

        this.action = action;
        this.receiverNamespace = namespace;
        this.receiverClassName = className;

        this.timeAcknowledged = timeAcknowledged;
        this.timeCompleted = timeCompleted;

        cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, DEFAULT_SECONDS);
        cal.set(Calendar.MILLISECOND, DEFAULT_MILLISECONDS);
    }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    public void updateDate(int y, int m, int d) {
        cal.set(Calendar.YEAR, y);
        cal.set(Calendar.MONTH, m);
        cal.set(Calendar.DAY_OF_MONTH, d);
    }

    public int[] getDate() {
        return new int[] {
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        };
    }

    public String getDateString() { return DATE_FORMAT.format(cal.getTime()); }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    public void updateTime(int h, int m) {
        cal.set(Calendar.HOUR_OF_DAY, h);
        cal.set(Calendar.MINUTE, m);
    }

    public int[] getTime() {
        return new int[] {
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.AM_PM)
        };
    }

    public String getTimeString() { return TIME_FORMAT.format(cal.getTime()); }

    public long getAlarmTimeMillis() { return cal.getTimeInMillis(); }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    public String getLabel() { return label; }

    public void updateLabel(String newLabel) {
        label = newLabel;
    }

    public void updateStatus(STATUS newStatus) { status = newStatus; }

    public String getStatus() { return status.toString(); }

    public boolean isActive() {
        // an alarm is "inactive" if its status is "new" or "complete"
        // therefore, an "active" alarm is one with a status OTHER THAN these
        return !status.equals(STATUS.New) && !status.equals(STATUS.Complete);
    }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    public int getID() { return ID; }

    public void updateAlarmIntentSettings(String a, String rn, String rcn) {
        action = a;
        receiverNamespace = rn;
        receiverClassName = rcn;
    }

    public String[] getAlarmIntentSettings() {
        return new String[] {
                action,
                receiverNamespace,
                receiverClassName
        };
    }

    public PendingIntent getAlarmIntent(Context context) {
        if (action == null || action.isEmpty()) {
            Log.e(Constants.LOG_TAG, "Cannot recreate alarm PI without action name!");
            return null;
        }

        if (receiverNamespace == null || receiverNamespace.isEmpty()) {
            Log.e(Constants.LOG_TAG, "Cannot recreate alarm PI without alarm receiver namespace!");
            return null;
        }

        if (receiverClassName == null || receiverClassName.isEmpty()) {
            Log.e(Constants.LOG_TAG, "Cannot recreate alarm PI without alarm receiver class name!");
            return null;
        }

        Intent baseIntent = new Intent();
        baseIntent.setComponent(new ComponentName(receiverNamespace,receiverClassName));
        baseIntent.setAction(action);

        baseIntent.putExtra(INTENT_EXTRA_ALARM_ID, ID);
        baseIntent.putExtra(INTENT_EXTRA_ORIG_TIME, getTimeString());
        baseIntent.addFlags(BASE_INTENT_FLAGS);

        return PendingIntent.getBroadcast(context, ID, baseIntent, PENDING_INTENT_FLAGS);
    }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    public String getTimeAcknowledged() { return timeAcknowledged; }

    public String getTimeCompleted() { return timeCompleted; }

    public String toString() { return label; }

}