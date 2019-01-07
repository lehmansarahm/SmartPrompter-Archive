package edu.temple.sp_res_lib;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.temple.sp_res_lib.db.AlarmDbContract;
import edu.temple.sp_res_lib.utils.Constants;

public class SpAlarmManager {

    private Context context;

    public SpAlarmManager(Context context) {
        this.context = context;
    }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    public Alarm create() {
        ContentResolver cr = context.getContentResolver();
        Uri uri = cr.insert(AlarmDbContract.AlarmEntry.CONTENT_URI,
                AlarmDbContract.AlarmEntry.getDefaultValues());
        Cursor cursor = cr.query(uri, AlarmDbContract.AlarmEntry.getAllFields(),
                null, null, null);
        return AlarmDbContract.AlarmEntry.populateFromCursor(cursor).get(0);
    }

    public Alarm get(int alarmID) {
        Uri uri = AlarmDbContract.AlarmEntry.getContentUriWithID(alarmID);
        Cursor cursor = context.getContentResolver().query(uri,
                AlarmDbContract.AlarmEntry.getAllFields(),
                null, null, null);
        return AlarmDbContract.AlarmEntry.populateFromCursor(cursor).get(0);
    }

    public List<Alarm> get(Constants.ALARM_STATUS[] statuses) {
        List<String> whereArgs = new ArrayList<>();
        String whereClause = (AlarmDbContract.AlarmEntry.COLUMN_STATUS + "=?");
        for (int i = 0; i < statuses.length; i++) {
            whereArgs.add(statuses[i].toString());
            if (i > 0) {
                whereClause += (" OR " + AlarmDbContract.AlarmEntry.COLUMN_STATUS
                        + "=?");
            }
        }

        Cursor cursor = context.getContentResolver()
                .query(AlarmDbContract.AlarmEntry.CONTENT_URI,
                        AlarmDbContract.AlarmEntry.getAllFields(),
                        whereClause, whereArgs.toArray(new String[0]),
                        null);
        return AlarmDbContract.AlarmEntry.populateFromCursor(cursor);
    }

    public List<Alarm> getAll() {
        Cursor cursor = context.getContentResolver()
                .query(AlarmDbContract.AlarmEntry.CONTENT_URI,
                        AlarmDbContract.AlarmEntry.getAllFields(),
                        null,null,null);
        return AlarmDbContract.AlarmEntry.populateFromCursor(cursor);
    }

    public int update(Alarm alarm) {
        Log.i(Constants.LOG_TAG, "Committing details for alarm with ID: "
                + alarm.getID());
        String whereClause = (AlarmDbContract.AlarmEntry._ID + "=?");
        String[] args = new String[] { String.valueOf(alarm.getID()) };
        return context.getContentResolver()
                .update(AlarmDbContract.AlarmEntry.CONTENT_URI,
                        AlarmDbContract.AlarmEntry.getValues(alarm),
                        whereClause, args);
    }

    public int delete(Alarm alarm) {
        String whereClause = (AlarmDbContract.AlarmEntry._ID + "=?");
        String[] args = new String[] { String.valueOf(alarm.getID()) };
        return context.getContentResolver()
                .delete(AlarmDbContract.AlarmEntry.CONTENT_URI, whereClause, args);
    }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    public boolean areAlarmsAvailable() {
        return (getAll().size() > 0);
    }

    public boolean areAlarmsAvailable(Constants.ALARM_STATUS[] statuses) {
        return (get(statuses).size() != 0);
    }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    /*
            NOTE !!!  ADMIN APP IS RESPONSIBLE FOR PROVIDING ALARM RECEIVER DETAILS !!!
     */
    public boolean scheduleAlarm(Alarm alarm, String alarmAction,
                                 String receiverNamespace, String receiverClassName) {

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // String alarmAction = context.getResources().getString(R.string.action_alarms);
        alarm.updateIntentSettings(alarmAction, receiverNamespace, receiverClassName);

        long alarmTime = alarm.getAlarmTimeMillis();
        Log.d(Constants.LOG_TAG, "Alarm will go off at time (millis): " + alarmTime);
        Log.d(Constants.LOG_TAG, "Current time (millis): " + System.currentTimeMillis());

        long intervalMillis = (alarmTime - System.currentTimeMillis());
        double intervalSec = (intervalMillis / 1000.d);
        Log.d(Constants.LOG_TAG, "Alarm time interval (sec): " + intervalSec);

        if (intervalSec <= 0) {
            Log.e(Constants.LOG_TAG, "CANNOT SET AN ALARM FOR A TIME IN THE PAST.");
            return false;
        }

        Log.e(Constants.LOG_TAG, "Scheduling new alarm reminder with request code: "
                + alarm.getID() + " \t\t for time: " + alarm.getTimeString());
        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                alarmTime, alarm.getPendingIntent(context));
        alarm.updateStatus(Constants.ALARM_STATUS.Active);
        return true;
    }

    public void cancelAlarm(Alarm alarm) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Log.i(Constants.LOG_TAG,
                "Attempting to cancel active reminders for alarm with ID: "
                + alarm.getID());

        if (alarmMgr == null) {
            Log.e(Constants.LOG_TAG,
                    "Cannot cancel active reminders!  Alarm Manager is null.");
            return;
        }

        PendingIntent alarmIntent = alarm.getPendingIntent(context);
        if (alarmIntent == null) {
            Log.e(Constants.LOG_TAG,
                    "Cannot cancel active reminders!  Alarm intent is null.");
            return;
        }

        alarmMgr.cancel(alarmIntent);
        alarm.updateStatus(Constants.ALARM_STATUS.New);
        Log.i(Constants.LOG_TAG, "Reminders cancelled.");
    }

}