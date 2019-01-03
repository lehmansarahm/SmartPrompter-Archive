package edu.temple.sp_res_lib.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class AlarmDbProvider extends ContentProvider {

    public static final int CODE_ALARM = 100;
    public static final int CODE_ALARM_WITH_ID = 101;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AlarmDbContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, AlarmDbContract.AlarmEntry.TABLE_NAME, CODE_ALARM);
        matcher.addURI(authority, AlarmDbContract.AlarmEntry.TABLE_NAME + "/#", CODE_ALARM_WITH_ID);
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
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        /*
        // To query specific record
        long _id = 2; //Suppose we want to second row in the table
        Cursor cursor = getContentResolver()
                .query(TodoContract.TodoEntry.buildTodoUriWithId(_id),null,null,null,null);

        // To query all records
        Cursor cursor = getContentResolver()
                .query(TodoContract.TodoEntry.CONTENT_URI,null,null,null,null);
        */

        Cursor cursor;
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case CODE_ALARM_WITH_ID: {
                String _ID = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{_ID};
                cursor = db.query(AlarmDbContract.AlarmEntry.TABLE_NAME, projection,
                        AlarmDbContract.AlarmEntry._ID + " = ? ",
                        selectionArguments, null, null, sortOrder);
                break;
            }
            case CODE_ALARM: {
                cursor = db.query(AlarmDbContract.AlarmEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        /* Uri insertNewRecord(String task) {
            ContentValues values = new ContentValues();
            values.put(TodoContract.TodoEntry.COLUMN_TASK,task);
            values.put(TodoContract.TodoEntry.COLUMN_STATUS,0);
            values.put(TodoContract.TodoEntry.COLUMN_TASK,task);
            values.put(TodoContract.TodoEntry.COLUMN_DATE,
                    System.currentTimeMillis());

            return getContentResolver().
                    insert(TodoContract.TodoEntry.CONTENT_URI,values);
        } */

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case CODE_ALARM:
                long _id = db.insert(AlarmDbContract.AlarmEntry.TABLE_NAME,
                        null, values);
                if (_id != -1) {
                    // if _id is equal to -1 insertion failed ... If insertion succeeded,
                    // firing this will broadcast that database has been changed, trigger
                    // dependent entities to perform automatic update.
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return AlarmDbContract.AlarmEntry.getContentUriWithID(_id);
            default:
                return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int countRowsDeleted = 0;
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case CODE_ALARM:
                countRowsDeleted = db.delete(AlarmDbContract.AlarmEntry.TABLE_NAME,
                        selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
            default:
                // do nothing
        }

        return countRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        int countRowsUpdated = 0;
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case CODE_ALARM:
                countRowsUpdated = db.update(AlarmDbContract.AlarmEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
            default:
                // do nothing
        }

        return countRowsUpdated;
    }

}