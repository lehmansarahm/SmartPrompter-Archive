package edu.temple.sp_res_lib.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.Reminder;
import edu.temple.sp_res_lib.utils.Constants;

public final class AlarmDbContract {

    public static final String CONTENT_AUTHORITY = "edu.temple.smartprompter.alarms";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class AlarmEntry implements BaseScheduleable {

        public static final String TABLE_NAME = "alarm";

        public static final String COLUMN_LABEL = "label";
        public static final String COLUMN_STATUS = "status";

        public static final String COLUMN_TIME_ACKNOWLEDGED = "timeAcknowledged";
        public static final String COLUMN_TIME_COMPLETED = "timeCompleted";
        public static final String COLUMN_COMPLETION_MEDIA_ID = "completionMediaID";

        private static final String DEFAULT_LABEL = "New Alarm";
        private static final String DEFAULT_STATUS = Constants.ALARM_STATUS.New.toString();
        private static final String DEFAULT_MEDIA_ID = "headshot.jpg";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME)
                .build();

        public static Uri getContentUriWithID(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }

        public static String[] getAllFields() {
            String[] firstFields = new String[]{
                    AlarmEntry._ID,
                    AlarmEntry.COLUMN_LABEL,
                    AlarmEntry.COLUMN_STATUS
            };

            String[] lastFields = new String[] {
                    AlarmEntry.COLUMN_TIME_ACKNOWLEDGED,
                    AlarmEntry.COLUMN_TIME_COMPLETED,
                    AlarmEntry.COLUMN_COMPLETION_MEDIA_ID
            };

            String[] allFields = concat(firstFields, SCHEDULEABLE_FIELDS);
            allFields = concat(allFields, lastFields);
            return allFields;
        }

        public static ContentValues getDefaultValues() {
            ContentValues values = new ContentValues();
            values.put(AlarmEntry.COLUMN_LABEL, DEFAULT_LABEL);
            values.put(AlarmEntry.COLUMN_STATUS, DEFAULT_STATUS);

            // default the alarm to today's date
            Calendar cal = Calendar.getInstance();
            values.put(AlarmEntry.COLUMN_YEAR, cal.get(Calendar.YEAR));
            values.put(AlarmEntry.COLUMN_MONTH, cal.get(Calendar.MONTH));
            values.put(AlarmEntry.COLUMN_DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));

            // default the alarm time to 12 noon
            values.put(AlarmEntry.COLUMN_HOUR_OF_DAY, Constants.DEFAULT_HOUR_OF_DAY);
            values.put(AlarmEntry.COLUMN_MINUTE, Constants.DEFAULT_MINUTE);

            // default the completion media ID to placeholder image
            values.put(AlarmEntry.COLUMN_COMPLETION_MEDIA_ID, DEFAULT_MEDIA_ID);

            return values;
        }

        public static ContentValues getValues(Alarm alarm) {
            ContentValues values = new ContentValues();

            values.put(AlarmEntry.COLUMN_LABEL, alarm.getLabel());
            values.put(AlarmEntry.COLUMN_STATUS, alarm.getStatusString());

            int[] date = alarm.getDate();
            values.put(AlarmEntry.COLUMN_YEAR, date[0]);
            values.put(AlarmEntry.COLUMN_MONTH, date[1]);
            values.put(AlarmEntry.COLUMN_DAY_OF_MONTH, date[2]);

            int[] time = alarm.getTime();
            values.put(AlarmEntry.COLUMN_HOUR_OF_DAY, time[0]);
            values.put(AlarmEntry.COLUMN_MINUTE, time[1]);

            String[] alarmIntentSettings = alarm.getIntentSettings();
            values.put(AlarmEntry.COLUMN_ACTION, alarmIntentSettings[0]);
            values.put(AlarmEntry.COLUMN_RECEIVER_NAMESPACE, alarmIntentSettings[1]);
            values.put(AlarmEntry.COLUMN_RECEIVER_CLASS_NAME, alarmIntentSettings[2]);

            values.put(AlarmEntry.COLUMN_TIME_ACKNOWLEDGED, alarm.getTimeAcknowledged());
            values.put(AlarmEntry.COLUMN_TIME_COMPLETED, alarm.getTimeCompleted());
            values.put(AlarmEntry.COLUMN_COMPLETION_MEDIA_ID, alarm.getCompletionMediaID());

            return values;
        }

        public static List<Alarm> populateFromCursor(Cursor cursor) {
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
                        cursor.getString(cursor.getColumnIndex(AlarmEntry.COLUMN_TIME_COMPLETED)),
                        cursor.getString(cursor.getColumnIndex(AlarmEntry.COLUMN_COMPLETION_MEDIA_ID))
                );
                alarms.add(alarm);
            }  while(cursor.moveToNext());

            cursor.close();
            return alarms;
        }

    }

    public static final class ReminderEntry implements BaseScheduleable {

        public static final String TABLE_NAME = "reminder";

        public static final String COLUMN_ALARM_ID = "alarmID";
        public static final String COLUMN_TYPE = "reminderType";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME)
                .build();

        public static Uri getContentUriWithID(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }

        public static String[] getAllFields() {
            String[] firstFields = new String[]{
                    ReminderEntry._ID,
                    ReminderEntry.COLUMN_ALARM_ID,
                    ReminderEntry.COLUMN_TYPE
            };

            String[] allFields = concat(firstFields, SCHEDULEABLE_FIELDS);
            return allFields;
        }

        public static ContentValues getDefaultValues(int alarmID, Constants.REMINDER_TYPE type) {
            ContentValues values = new ContentValues();
            values.put(ReminderEntry.COLUMN_ALARM_ID, alarmID);
            values.put(ReminderEntry.COLUMN_TYPE, type.toString());

            // default the reminder to today's date
            Calendar cal = Calendar.getInstance();
            values.put(ReminderEntry.COLUMN_YEAR, cal.get(Calendar.YEAR));
            values.put(ReminderEntry.COLUMN_MONTH, cal.get(Calendar.MONTH));
            values.put(ReminderEntry.COLUMN_DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));

            // default the reminder time to 12 noon
            values.put(ReminderEntry.COLUMN_HOUR_OF_DAY, Constants.DEFAULT_HOUR_OF_DAY);
            values.put(ReminderEntry.COLUMN_MINUTE, Constants.DEFAULT_MINUTE);

            return values;
        }

        public static ContentValues getValues(Reminder reminder) {
            ContentValues values = new ContentValues();

            values.put(ReminderEntry.COLUMN_ALARM_ID, reminder.getAlarmID());
            values.put(ReminderEntry.COLUMN_TYPE, reminder.getTypeString());

            int[] date = reminder.getDate();
            values.put(ReminderEntry.COLUMN_YEAR, date[0]);
            values.put(ReminderEntry.COLUMN_MONTH, date[1]);
            values.put(ReminderEntry.COLUMN_DAY_OF_MONTH, date[2]);

            int[] time = reminder.getTime();
            values.put(ReminderEntry.COLUMN_HOUR_OF_DAY, time[0]);
            values.put(ReminderEntry.COLUMN_MINUTE, time[1]);

            String[] intentSettings = reminder.getIntentSettings();
            values.put(ReminderEntry.COLUMN_ACTION, intentSettings[0]);
            values.put(ReminderEntry.COLUMN_RECEIVER_NAMESPACE, intentSettings[1]);
            values.put(ReminderEntry.COLUMN_RECEIVER_CLASS_NAME, intentSettings[2]);

            return values;
        }

        public static List<Reminder> populateFromCursor(Cursor cursor) {
            List<Reminder> reminders = new ArrayList<>();
            if (cursor == null || !cursor.moveToFirst()) return reminders;

            do {
                Reminder reminder = new Reminder(
                        cursor.getInt(cursor.getColumnIndex(ReminderEntry._ID)),
                        cursor.getInt(cursor.getColumnIndex(ReminderEntry.COLUMN_ALARM_ID)),
                        cursor.getString(cursor.getColumnIndex(ReminderEntry.COLUMN_TYPE)),
                        // -----------------------------------------------------------------------
                        cursor.getInt(cursor.getColumnIndex(ReminderEntry.COLUMN_YEAR)),
                        cursor.getInt(cursor.getColumnIndex(ReminderEntry.COLUMN_MONTH)),
                        cursor.getInt(cursor.getColumnIndex(ReminderEntry.COLUMN_DAY_OF_MONTH)),
                        // -----------------------------------------------------------------------
                        cursor.getInt(cursor.getColumnIndex(ReminderEntry.COLUMN_HOUR_OF_DAY)),
                        cursor.getInt(cursor.getColumnIndex(ReminderEntry.COLUMN_MINUTE)),
                        // -----------------------------------------------------------------------
                        cursor.getString(cursor.getColumnIndex(ReminderEntry.COLUMN_ACTION)),
                        cursor.getString(cursor.getColumnIndex(ReminderEntry.COLUMN_RECEIVER_NAMESPACE)),
                        cursor.getString(cursor.getColumnIndex(ReminderEntry.COLUMN_RECEIVER_CLASS_NAME))
                );
                reminders.add(reminder);
            }  while(cursor.moveToNext());

            cursor.close();
            return reminders;
        }

    }

    public interface BaseScheduleable extends BaseColumns {

        String COLUMN_YEAR = "year";
        String COLUMN_MONTH = "month";
        String COLUMN_DAY_OF_MONTH = "dayOfMonth";

        String COLUMN_HOUR_OF_DAY = "hourOfDay";
        String COLUMN_MINUTE = "minute";

        String COLUMN_ACTION = "action";
        String COLUMN_RECEIVER_NAMESPACE = "receiverNamespace";
        String COLUMN_RECEIVER_CLASS_NAME = "receiverClassName";

        String[] SCHEDULEABLE_FIELDS = new String[] {
                COLUMN_YEAR, COLUMN_MONTH, COLUMN_DAY_OF_MONTH,
                COLUMN_HOUR_OF_DAY, COLUMN_MINUTE,
                COLUMN_ACTION, COLUMN_RECEIVER_NAMESPACE, COLUMN_RECEIVER_CLASS_NAME
        };

    }

    public static String[] concat(String[] first, String[] second) {
        String[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

}