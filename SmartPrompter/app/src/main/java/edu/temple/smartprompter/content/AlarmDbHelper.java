package edu.temple.smartprompter.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AlarmDbHelper extends SQLiteOpenHelper {

    /*
     * This is the name of our database. Database names should be descriptive and end with the
     * .db extension.
     */
    public static final String DATABASE_NAME = "smartprompter.db";

    /*
     * If you change the database schema, you must increment the database version or the onUpgrade
     * method will not be called.
     */
    private static final int DATABASE_VERSION = 1;

    public AlarmDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ALARM_TABLE =
                "CREATE TABLE " + AlarmDbContract.AlarmEntry.TABLE_NAME + " (" +
                        AlarmDbContract.AlarmEntry._ID                  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        AlarmDbContract.AlarmEntry.COLUMN_LABEL         + " TEXT NOT NULL, "                 +
                        AlarmDbContract.AlarmEntry.COLUMN_STATUS        + " TEXT NOT NULL,"                  +
                        AlarmDbContract.AlarmEntry.COLUMN_YEAR          + " INTEGER NOT NULL,"                  +
                        AlarmDbContract.AlarmEntry.COLUMN_MONTH         + " INTEGER NOT NULL,"                  +
                        AlarmDbContract.AlarmEntry.COLUMN_DAY_OF_MONTH  + " INTEGER NOT NULL,"                  +
                        AlarmDbContract.AlarmEntry.COLUMN_HOUR_OF_DAY   + " INTEGER NOT NULL,"                  +
                        AlarmDbContract.AlarmEntry.COLUMN_MINUTE        + " INTEGER NOT NULL);";
        db.execSQL(SQL_CREATE_ALARM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + AlarmDbContract.AlarmEntry.TABLE_NAME);
        onCreate(db);
    }

}