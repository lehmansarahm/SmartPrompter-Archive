package edu.temple.sp_res_lib.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import edu.temple.sp_res_lib.utils.Constants;

public class AlarmDbProvider extends ContentProvider {

    public static final int CODE_ALARM = 100;
    public static final int CODE_ALARM_WITH_ID = 101;

    public static final int CODE_REMINDER = 200;
    public static final int CODE_REMINDER_WITH_ID = 201;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String alarmAuthority = AlarmDbContract.CONTENT_AUTHORITY;
        matcher.addURI(alarmAuthority, AlarmDbContract.AlarmEntry.TABLE_NAME, CODE_ALARM);
        matcher.addURI(alarmAuthority, AlarmDbContract.AlarmEntry.TABLE_NAME + "/#", CODE_ALARM_WITH_ID);

        final String reminderAuthority = AlarmDbContract.CONTENT_AUTHORITY;
        matcher.addURI(reminderAuthority, AlarmDbContract.ReminderEntry.TABLE_NAME, CODE_REMINDER);
        matcher.addURI(reminderAuthority, AlarmDbContract.ReminderEntry.TABLE_NAME + "/#", CODE_REMINDER_WITH_ID);

        return matcher;
    }


    // ------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------


    private AlarmDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new AlarmDbHelper(getContext());
        return dbHelper != null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.i(Constants.LOG_TAG, "Received query request with URI: " + uri);
        Cursor cursor;
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case CODE_ALARM_WITH_ID: {
                String _ID = uri.getLastPathSegment();
                Log.i(Constants.LOG_TAG, "Attempting to retrieve record by ID: " + _ID);

                String[] selectionArguments = new String[]{_ID};
                cursor = db.query(AlarmDbContract.AlarmEntry.TABLE_NAME, projection,
                        AlarmDbContract.AlarmEntry._ID + " = ? ",
                        selectionArguments, null, null, sortOrder);
                break;
            }
            case CODE_ALARM: {
                if (selection == null || selection.isEmpty())
                    Log.i(Constants.LOG_TAG, "Attempting to retrieve all alarm records.");
                else {
                    StringBuilder builder = new StringBuilder();
                    for (String s : selectionArgs) { builder.append(s + ","); }
                    String selectArgs = builder.toString();

                    Log.i(Constants.LOG_TAG, "Attempting to retrieve alarm records "
                            + "which match the following selection clause: " + selection
                            + " \n\t\t and arguments: " + selectArgs);
                }

                cursor = db.query(AlarmDbContract.AlarmEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case CODE_REMINDER_WITH_ID: {
                String _ID = uri.getLastPathSegment();
                Log.i(Constants.LOG_TAG, "Attempting to retrieve record by ID: " + _ID);

                String[] selectionArguments = new String[]{_ID};
                cursor = db.query(AlarmDbContract.ReminderEntry.TABLE_NAME, projection,
                        AlarmDbContract.ReminderEntry._ID + " = ? ",
                        selectionArguments, null, null, sortOrder);
                break;
            }
            case CODE_REMINDER: {
                if (selection == null || selection.isEmpty())
                    Log.i(Constants.LOG_TAG, "Attempting to retrieve all reminder records.");
                else {
                    StringBuilder builder = new StringBuilder();
                    for (String s : selectionArgs) { builder.append(s + ","); }
                    String selectArgs = builder.toString();

                    Log.i(Constants.LOG_TAG, "Attempting to retrieve reminder records "
                            + "which match the following selection clause: " + selection
                            + " \n\t\t and arguments: " + selectArgs);
                }

                cursor = db.query(AlarmDbContract.ReminderEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            }
            default:
                Log.e(Constants.LOG_TAG, "Unexpected URI: " + uri);
                return null;
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.i(Constants.LOG_TAG, "Received insert request with URI: " + uri);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case CODE_ALARM: {
                long alarmID = db.insert(AlarmDbContract.AlarmEntry.TABLE_NAME,
                        null, values);
                Log.i(Constants.LOG_TAG, "Inserted new alarm record!  ID: " + alarmID);

                if (alarmID == -1) {
                    Log.e(Constants.LOG_TAG, "Alarm record insertion failed.");
                    return null;
                }

                getContext().getContentResolver().notifyChange(uri, null);
                return AlarmDbContract.AlarmEntry.getContentUriWithID(alarmID);
            }
            case CODE_REMINDER: {
                long reminderID = db.insert(AlarmDbContract.ReminderEntry.TABLE_NAME,
                        null, values);
                Log.i(Constants.LOG_TAG, "Inserted new reminder record!  ID: " + reminderID);

                if (reminderID == -1) {
                    Log.e(Constants.LOG_TAG, "Reminder record insertion failed.");
                    return null;
                }

                getContext().getContentResolver().notifyChange(uri, null);
                return AlarmDbContract.ReminderEntry.getContentUriWithID(reminderID);
            }
            default: {
                Log.e(Constants.LOG_TAG, "Unexpected URI: " + uri);
                return null;
            }
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        Log.i(Constants.LOG_TAG, "Received delete request with URI: " + uri);
        switch (uriMatcher.match(uri)) {
            case CODE_ALARM: {
                int countRowsDeleted = dbHelper.getWritableDatabase()
                        .delete(AlarmDbContract.AlarmEntry.TABLE_NAME,
                                selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return countRowsDeleted;
            }
            case CODE_REMINDER: {
                int countRowsDeleted = dbHelper.getWritableDatabase()
                        .delete(AlarmDbContract.ReminderEntry.TABLE_NAME,
                                selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return countRowsDeleted;
            }
            default: {
                Log.e(Constants.LOG_TAG, "Unexpected URI: " + uri);
                return -1;
            }
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.i(Constants.LOG_TAG, "Received update request with URI: " + uri);
        switch (uriMatcher.match(uri)) {
            case CODE_ALARM: {
                int countRowsUpdated = dbHelper.getWritableDatabase()
                        .update(AlarmDbContract.AlarmEntry.TABLE_NAME,
                                values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return countRowsUpdated;
            }
            case CODE_REMINDER: {
                int countRowsUpdated = dbHelper.getWritableDatabase()
                        .update(AlarmDbContract.ReminderEntry.TABLE_NAME,
                                values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return countRowsUpdated;
            }
            default: {
                Log.e(Constants.LOG_TAG, "Unexpected URI: " + uri);
                return -1;
            }
        }
    }

}