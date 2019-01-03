package edu.temple.sp_res_lib.content;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.temple.sp_res_lib.Alarm;

public final class AlarmDbContract {

    public static final String CONTENT_AUTHORITY = "edu.temple.smartprompter.alarms";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String[] ALL_FIELDS = new String[] {
            AlarmEntry._ID,
            AlarmEntry.COLUMN_LABEL,
            AlarmEntry.COLUMN_STATUS,
            // -----------------------------------------------------------------------
            AlarmEntry.COLUMN_YEAR,
            AlarmEntry.COLUMN_MONTH,
            AlarmEntry.COLUMN_DAY_OF_MONTH,
            // -----------------------------------------------------------------------
            AlarmEntry.COLUMN_HOUR_OF_DAY,
            AlarmEntry.COLUMN_MINUTE,
            // -----------------------------------------------------------------------
            AlarmEntry.COLUMN_ACTION,
            AlarmEntry.COLUMN_RECEIVER_NAMESPACE,
            AlarmEntry.COLUMN_RECEIVER_CLASS_NAME,
            // -----------------------------------------------------------------------
            AlarmEntry.COLUMN_TIME_ACKNOWLEDGED,
            AlarmEntry.COLUMN_TIME_COMPLETED
    };

    public static final class AlarmEntry implements BaseColumns {

        public static final String TABLE_NAME = "alarm";

        /*
         * AlarmEntry did not explicitly declare a column called "_ID". However,
         * AlarmEntry implements the interface, "BaseColumns", which does have a field
         * named "_ID". We use that to designate our table's primary key.
         */

        public static final String COLUMN_LABEL = "label";
        public static final String COLUMN_STATUS = "status";

        public static final String COLUMN_ACTION = "action";
        public static final String COLUMN_RECEIVER_NAMESPACE = "receiverNamespace";
        public static final String COLUMN_RECEIVER_CLASS_NAME = "receiverClassName";

        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_MONTH = "month";
        public static final String COLUMN_DAY_OF_MONTH = "dayOfMonth";

        public static final String COLUMN_HOUR_OF_DAY = "hourOfDay";
        public static final String COLUMN_MINUTE = "minute";

        public static final String COLUMN_TIME_ACKNOWLEDGED = "timeAcknowledged";
        public static final String COLUMN_TIME_COMPLETED = "timeCompleted";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME)
                .build();

        public static Uri getContentUriWithID(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }

    }

    private static final String DEFAULT_LABEL = "New Alarm";
    private static final String DEFAULT_STATUS = Alarm.STATUS.New.toString();
    private static final int DEFAULT_HOUR_OF_DAY = 12;
    private static final int DEFAULT_MINUTE = 0;

    public static ContentValues getDefaultAlarmValues() {
        ContentValues values = new ContentValues();
        values.put(AlarmEntry.COLUMN_LABEL, DEFAULT_LABEL);
        values.put(AlarmEntry.COLUMN_STATUS, DEFAULT_STATUS);

        // default the alarm to today's date
        Calendar cal = Calendar.getInstance();
        values.put(AlarmEntry.COLUMN_YEAR, cal.get(Calendar.YEAR));
        values.put(AlarmEntry.COLUMN_MONTH, cal.get(Calendar.MONTH));
        values.put(AlarmEntry.COLUMN_DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));

        // default the alarm time to 12 noon
        values.put(AlarmEntry.COLUMN_HOUR_OF_DAY, DEFAULT_HOUR_OF_DAY);
        values.put(AlarmEntry.COLUMN_MINUTE, DEFAULT_MINUTE);

        return values;
    }

    public static ContentValues getAlarmValues(Alarm alarm) {
        ContentValues values = new ContentValues();

        values.put(AlarmEntry.COLUMN_LABEL, alarm.getLabel());
        values.put(AlarmEntry.COLUMN_STATUS, alarm.getStatus());

        int[] date = alarm.getDate();
        values.put(AlarmEntry.COLUMN_YEAR, date[0]);
        values.put(AlarmEntry.COLUMN_MONTH, date[1]);
        values.put(AlarmEntry.COLUMN_DAY_OF_MONTH, date[2]);

        int[] time = alarm.getTime();
        values.put(AlarmEntry.COLUMN_HOUR_OF_DAY, time[0]);
        values.put(AlarmEntry.COLUMN_MINUTE, time[1]);

        String[] alarmIntentSettings = alarm.getAlarmIntentSettings();
        values.put(AlarmEntry.COLUMN_ACTION, alarmIntentSettings[0]);
        values.put(AlarmEntry.COLUMN_RECEIVER_NAMESPACE, alarmIntentSettings[1]);
        values.put(AlarmEntry.COLUMN_RECEIVER_CLASS_NAME, alarmIntentSettings[2]);

        values.put(AlarmEntry.COLUMN_TIME_ACKNOWLEDGED, alarm.getTimeAcknowledged());
        values.put(AlarmEntry.COLUMN_TIME_COMPLETED, alarm.getTimeCompleted());

        return values;
    }

    public static List<Alarm> populateAlarms(Cursor cursor) {
        List<Alarm> alarms = new ArrayList<>();
        if (cursor == null || !cursor.moveToFirst()) return alarms;

        do {
            Alarm alarm = new Alarm(
                    cursor.getInt(cursor.getColumnIndex(AlarmEntry._ID)),
                    cursor.getString(cursor.getColumnIndex(AlarmEntry.COLUMN_LABEL)),
                    cursor.getString(cursor.getColumnIndex(AlarmEntry.COLUMN_STATUS)),
                    // -----------------------------------------------------------------------
                    cursor.getInt(cursor.getColumnIndex(AlarmEntry.COLUMN_YEAR)),
                    cursor.getInt(cursor.getColumnIndex(AlarmEntry.COLUMN_MONTH)),
                    cursor.getInt(cursor.getColumnIndex(AlarmEntry.COLUMN_DAY_OF_MONTH)),
                    // -----------------------------------------------------------------------
                    cursor.getInt(cursor.getColumnIndex(AlarmEntry.COLUMN_HOUR_OF_DAY)),
                    cursor.getInt(cursor.getColumnIndex(AlarmEntry.COLUMN_MINUTE)),
                    // -----------------------------------------------------------------------
                    cursor.getString(cursor.getColumnIndex(AlarmEntry.COLUMN_ACTION)),
                    cursor.getString(cursor.getColumnIndex(AlarmEntry.COLUMN_RECEIVER_NAMESPACE)),
                    cursor.getString(cursor.getColumnIndex(AlarmEntry.COLUMN_RECEIVER_CLASS_NAME)),
                    // -----------------------------------------------------------------------
                    cursor.getString(cursor.getColumnIndex(AlarmEntry.COLUMN_TIME_ACKNOWLEDGED)),
                    cursor.getString(cursor.getColumnIndex(AlarmEntry.COLUMN_TIME_COMPLETED))
            );
            alarms.add(alarm);
        }  while(cursor.moveToNext());

        cursor.close();
        return alarms;
    }

}