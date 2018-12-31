package edu.temple.smartprompter.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.temple.smartprompter.R;
import edu.temple.smartprompter.util.Constants;

public class Alarm {

    public enum STATUS { New, Active, Unacknowledged, Incomplete, Complete }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    private Calendar cal;
    private String label;
    private STATUS status;
    private long createTimeMillis;

    public Alarm(int h, int m, String l) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, h);
        c.set(Calendar.MINUTE, m);

        cal = c;
        label = l;
        status = STATUS.New;
        createTimeMillis = System.currentTimeMillis();
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

    public String getDateString() { return dateFormat.format(cal.getTime()); }

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

    public String getTimeString() { return timeFormat.format(cal.getTime()); }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    public String getLabel() { return label; }

    public void setStatus(STATUS newStatus) { status = newStatus; }

    public String getStatus() { return status.toString(); }

    public String toString() { return label; }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private int requestCode = 0, flags = 0;

    public void scheduleReminder(Context context) {
        if (requestCode == 0) requestCode = AlarmMaster.getNewRequestCode();
        Log.i(Constants.LOG_TAG, "Scheduling new alarm reminder with request code: "
                + requestCode);

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(context.getResources().getString(R.string.app_alarm_action));
        alarmIntent = PendingIntent.getBroadcast(context, requestCode, intent, flags);

        Log.d(Constants.LOG_TAG, "Alarm will go off at time (millis): " + cal.getTimeInMillis());
        Log.d(Constants.LOG_TAG, "Current time (millis): " + System.currentTimeMillis());

        long intervalMillis = (cal.getTimeInMillis() - System.currentTimeMillis());
        double intervalSec = (intervalMillis / 1000.d);
        Log.d(Constants.LOG_TAG, "Alarm time interval (sec): " + intervalSec);

        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                cal.getTimeInMillis(), alarmIntent);
    }

    public void cancelAllReminders() {
        Log.i(Constants.LOG_TAG, "Cancelling active reminders.");
        if (alarmMgr!= null && alarmIntent != null) {
            alarmMgr.cancel(alarmIntent);
            Log.i(Constants.LOG_TAG, "Reminders cancelled.");
        } else {
            Log.e(Constants.LOG_TAG, "Could not cancel active reminders!  Either "
                    + "Alarm Manager or alarm intent (or both) were null.");
        }
    }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    public static Alarm getNewAlarm() {
        return new Alarm(12, 0, "New Alarm");
    }

    public static List<Alarm> getDefaults() {
        List<Alarm> alarmList = new ArrayList<>();
        alarmList.add(new Alarm(13, 0, "Default Alarm 1"));
        alarmList.add(new Alarm(14, 0, "Default Alarm 2"));
        alarmList.add(new Alarm(15, 0, "Default Alarm 3"));
        alarmList.add(new Alarm(16, 0, "Default Alarm 4"));
        return alarmList;
    }

}