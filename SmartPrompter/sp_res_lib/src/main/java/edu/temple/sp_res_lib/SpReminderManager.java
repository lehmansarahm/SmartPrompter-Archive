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

    public Reminder get(int reminderID) {
        Uri uri = AlarmDbContract.ReminderEntry.getContentUriWithID(reminderID);
        Cursor cursor = context.getContentResolver().query(uri,
                AlarmDbContract.ReminderEntry.getAllFields(),
                null, null, null);

        List<Reminder> results = AlarmDbContract.ReminderEntry.populateFromCursor(cursor);
        if (!results.isEmpty()) return results.get(0);
        else return null;
    }

    public Reminder get(int alarmID, Constants.REMINDER_TYPE type) {
        String whereClause = (AlarmDbContract.ReminderEntry.COLUMN_ALARM_ID + "=? AND "
                + AlarmDbContract.ReminderEntry.COLUMN_TYPE + "=?");
        String[] whereArgs = new String[] { String.valueOf(alarmID), type.toString() };

        Cursor cursor = context.getContentResolver()
                .query(AlarmDbContract.ReminderEntry.CONTENT_URI,
                        AlarmDbContract.ReminderEntry.getAllFields(),
                        whereClause, whereArgs, null);

        List<Reminder> results = AlarmDbContract.ReminderEntry.populateFromCursor(cursor);
        if (!results.isEmpty()) return results.get(0);
        else return null;
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
        if (reminder.hasReachedCountLimit())
            return false;

        // Retrieve the associated intent settings
        String[] intentSettings = reminder.getIntentSettings();
        reminder.updateIntentSettings(
                intentSettings[0],      // broadcast action name
                intentSettings[1],      // broadcast receiver namespace
                intentSettings[2]);     // broadcast receiver class name

        // Schedule the actual alarm with the system
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                reminder.getAlarmTimeMillis(), reminder.getPendingIntent(context));

        // Alarm reminder has been set ... increment reminder count
        // and commit changes to DB ...
        SpReminderManager reminderMgr = new SpReminderManager(context);
        reminder.incrementCount();
        reminderMgr.update(reminder);

        Log.e(Constants.LOG_TAG, "Scheduled new alarm reminder with request code: "
                + reminder.getRequestCode()
                + " \t\t at current count: " + reminder.getCount()
                + " \t\t for time: " + reminder.getTimeString());
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