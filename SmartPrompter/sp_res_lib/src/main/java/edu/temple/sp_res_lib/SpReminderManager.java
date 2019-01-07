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

public class SpReminderManager {

    private Context context;

    public SpReminderManager(Context context) {
        this.context = context;
    }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    public Reminder create(int alarmID, Constants.REMINDER_TYPE type) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = cr.insert(AlarmDbContract.ReminderEntry.CONTENT_URI,
                AlarmDbContract.ReminderEntry.getDefaultValues(alarmID, type));
        Cursor cursor = cr.query(uri, AlarmDbContract.ReminderEntry.getAllFields(),
                null, null, null);
        return AlarmDbContract.ReminderEntry.populateFromCursor(cursor).get(0);
    }

    public Reminder get(int alarmID) {
        Uri uri = AlarmDbContract.ReminderEntry.getContentUriWithID(alarmID);
        Cursor cursor = context.getContentResolver().query(uri,
                AlarmDbContract.ReminderEntry.getAllFields(),
                null, null, null);
        return AlarmDbContract.ReminderEntry.populateFromCursor(cursor).get(0);
    }

    public List<Reminder> get(Constants.REMINDER_TYPE[] types) {
        List<String> whereArgs = new ArrayList<>();
        String whereClause = (AlarmDbContract.ReminderEntry.COLUMN_TYPE + "=?");
        for (int i = 0; i < types.length; i++) {
            whereArgs.add(types[i].toString());
            if (i > 0) {
                whereClause += (" OR " + AlarmDbContract.ReminderEntry.COLUMN_TYPE
                        + "=?");
            }
        }

        Cursor cursor = context.getContentResolver()
                .query(AlarmDbContract.ReminderEntry.CONTENT_URI,
                        AlarmDbContract.ReminderEntry.getAllFields(),
                        whereClause, whereArgs.toArray(new String[0]),
                        null);
        return AlarmDbContract.ReminderEntry.populateFromCursor(cursor);
    }

    public List<Reminder> getAll() {
        Cursor cursor = context.getContentResolver()
                .query(AlarmDbContract.ReminderEntry.CONTENT_URI,
                        AlarmDbContract.ReminderEntry.getAllFields(),
                        null,null,null);
        return AlarmDbContract.ReminderEntry.populateFromCursor(cursor);
    }

    public int update(Reminder reminder) {
        Log.i(Constants.LOG_TAG, "Committing details for reminder with ID: "
                + reminder.getID());
        String whereClause = (AlarmDbContract.ReminderEntry._ID + "=?");
        String[] args = new String[] { String.valueOf(reminder.getID()) };
        return context.getContentResolver()
                .update(AlarmDbContract.ReminderEntry.CONTENT_URI,
                        AlarmDbContract.ReminderEntry.getValues(reminder),
                        whereClause, args);
    }

    public int delete(Reminder reminder) {
        String whereClause = (AlarmDbContract.ReminderEntry._ID + "=?");
        String[] args = new String[] { String.valueOf(reminder.getID()) };
        return context.getContentResolver()
                .delete(AlarmDbContract.ReminderEntry.CONTENT_URI, whereClause, args);
    }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    /*
            NOTE !!!  ADMIN APP IS RESPONSIBLE FOR PROVIDING ALARM RECEIVER DETAILS !!!
     */
    public boolean scheduleReminder(Reminder reminder) {
        String[] intentSettings = reminder.getIntentSettings();
        reminder.updateIntentSettings(intentSettings[0], intentSettings[1], intentSettings[2]);

        long alarmTime = reminder.getAlarmTimeMillis();
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
                + reminder.getRequestCode() + " \t\t for time: " + reminder.getTimeString());
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                alarmTime, reminder.getPendingIntent(context));
        return true;
    }

    public void cancelReminder(Reminder reminder) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Log.i(Constants.LOG_TAG,
                "Attempting to cancel active reminders for alarm with ID: "
                + reminder.getID());

        if (alarmMgr == null) {
            Log.e(Constants.LOG_TAG,
                    "Cannot cancel active reminders!  Alarm Manager is null.");
            return;
        }

        PendingIntent alarmIntent = reminder.getPendingIntent(context);
        if (alarmIntent == null) {
            Log.e(Constants.LOG_TAG,
                    "Cannot cancel active reminders!  Alarm intent is null.");
            return;
        }

        alarmMgr.cancel(alarmIntent);
        Log.i(Constants.LOG_TAG, "Reminders cancelled.");
    }

}