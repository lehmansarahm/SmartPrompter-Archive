package edu.temple.sp_res_lib.utils;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public abstract class BaseScheduleable {

    protected static final int BASE_INTENT_FLAGS = Intent.FLAG_INCLUDE_STOPPED_PACKAGES;
    protected static final int PENDING_INTENT_FLAGS = PendingIntent.FLAG_CANCEL_CURRENT;

    protected static final int DEFAULT_SECONDS = 0;
    protected static final int DEFAULT_MILLISECONDS = 0;

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    protected int ID;

    public int getID() { return ID; }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    protected Calendar cal;

    private void setCalendar() {
        cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, DEFAULT_SECONDS);
        cal.set(Calendar.MILLISECOND, DEFAULT_MILLISECONDS);
    }

    public void updateDate(int y, int m, int d) {
        if (cal == null) setCalendar();
        cal.set(Calendar.YEAR, y);
        cal.set(Calendar.MONTH, m);
        cal.set(Calendar.DAY_OF_MONTH, d);
    }

    public void updateTime(int h, int m) {
        if (cal == null) setCalendar();
        cal.set(Calendar.HOUR_OF_DAY, h);
        cal.set(Calendar.MINUTE, m);
    }

    public int[] getDate() {
        return new int[] {
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        };
    }

    public int[] getTime() {
        return new int[] {
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.AM_PM)
        };
    }

    public String getDateString() { return Constants.DATE_FORMAT.format(cal.getTime()); }

    public String getTimeString() { return Constants.TIME_FORMAT.format(cal.getTime()); }

    public long getAlarmTimeMillis() { return cal.getTimeInMillis(); }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    protected String action;
    protected String receiverNamespace;
    protected String receiverClassName;

    public void updateIntentSettings(String a, String rn, String rcn) {
        action = a;
        receiverNamespace = rn;
        receiverClassName = rcn;
    }

    public String[] getIntentSettings() {
        return new String[] {
                action,
                receiverNamespace,
                receiverClassName
        };
    }

    protected Intent getBaseBroadcastIntent(Context context) {
        if (action == null || action.isEmpty()) {
            Log.e(Constants.LOG_TAG,
                    "Cannot recreate alarm PI without action name!");
            return null;
        }

        if (receiverNamespace == null || receiverNamespace.isEmpty()) {
            Log.e(Constants.LOG_TAG,
                    "Cannot recreate alarm PI without alarm receiver namespace!");
            return null;
        }

        if (receiverClassName == null || receiverClassName.isEmpty()) {
            Log.e(Constants.LOG_TAG,
                    "Cannot recreate alarm PI without alarm receiver class name!");
            return null;
        }

        Intent baseIntent = new Intent();
        baseIntent.setComponent(new ComponentName(receiverNamespace, receiverClassName));
        baseIntent.setAction(action);

        baseIntent.putExtra(Constants.INTENT_EXTRA_ALARM_ID, ID);
        baseIntent.putExtra(Constants.INTENT_EXTRA_ORIG_TIME, getTimeString());
        baseIntent.addFlags(BASE_INTENT_FLAGS);

        return baseIntent;
    }

}