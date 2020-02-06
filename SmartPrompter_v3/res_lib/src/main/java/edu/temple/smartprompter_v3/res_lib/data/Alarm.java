package edu.temple.smartprompter_v3.res_lib.data;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import edu.temple.smartprompter_v3.res_lib.utils.Constants;
import edu.temple.smartprompter_v3.res_lib.utils.DateTimeUtil;
import edu.temple.smartprompter_v3.res_lib.utils.MediaUtil;

import static edu.temple.smartprompter_v3.res_lib.utils.Constants.LOG_TAG;

public class Alarm implements FirebaseConnector.FbDataClass {

    public enum STATUS { New, Active, Unacknowledged, Incomplete, Complete }

    public enum REMINDER { None, Explicit, Implicit }

    public static final String                      // MAKE SURE THESE MATCH WHAT'S IN FIRE BASE!!
            COLLECTION = "alarms",
    // -------------------------------------------------
            FIELD_DESC = "desc",
            FIELD_REQUEST_CODE = "requestCode",
            FIELD_ALARM_TIME = "alarmTime",
            FIELD_REMINDER_TIME = "reminderTime",
            FIELD_REMINDER_TYPE = "reminderType",
            FIELD_REMINDER_COUNT = "reminderCount",
    // -------------------------------------------------
            FIELD_TIME_ACKNOWLEDGED = "timeAcknowledged",
            FIELD_TIME_COMPLETED = "timeCompleted",
            FIELD_PHOTO_PATH = "photoPath",
    // -------------------------------------------------
            FIELD_STATUS = "status",
            FIELD_ARCHIVED = "archived",
            FIELD_USER_EMAIL = "userEmail",
    // -------------------------------------------------
            DEFAULT_VALUE = "n/a";


    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------


    private int requestCode;
    private String id, desc, userEmail, photoPath;
    private Timestamp alarmTime, reminderTime;
    private REMINDER reminderType;
    private int reminderCount;
    private Timestamp timeAcknowledged, timeCompleted;
    private STATUS status;
    private boolean archived;

    public Alarm() {
        // populate with default values
        id = Constants.DEFAULT_ALARM_GUID;
        desc = Constants.DEFAULT_ALARM_DESC;
        alarmTime = Timestamp.now();
        reminderType = REMINDER.None;
        photoPath = "";
        status = STATUS.Active;
        archived = false;

        FirebaseConnector.getLatestAlarm(
                completeResult -> requestCode = (((Alarm) completeResult).getRequestCode() + 1),
                (error) -> Log.e(LOG_TAG, "Something went wrong while trying to retrieve "
                        + "the latest alarm GUID.", error));
    }

    public Alarm (DocumentSnapshot document) {
        id = document.getId();
        requestCode = document.getLong(FIELD_REQUEST_CODE).intValue();
        desc = document.contains(FIELD_DESC) ? document.get(FIELD_DESC).toString() : DEFAULT_VALUE;
        userEmail = document.contains(FIELD_USER_EMAIL) ? document.get(FIELD_USER_EMAIL).toString() : DEFAULT_VALUE;

        alarmTime = document.getTimestamp(FIELD_ALARM_TIME);
        reminderTime = document.getTimestamp(FIELD_REMINDER_TIME);
        reminderCount = document.getLong(FIELD_REMINDER_COUNT).intValue();

        timeAcknowledged = document.getTimestamp(FIELD_TIME_ACKNOWLEDGED);
        timeCompleted = document.getTimestamp(FIELD_TIME_COMPLETED);
        photoPath = document.contains(FIELD_PHOTO_PATH) ? document.get(FIELD_PHOTO_PATH).toString() : FIELD_PHOTO_PATH;

        archived = document.contains(FIELD_ARCHIVED) ? document.getBoolean(FIELD_ARCHIVED) : false;

        // -------------------------------------------------------------------------------

        try {
            reminderType = document.contains(FIELD_REMINDER_TYPE)
                    ? REMINDER.valueOf(document.get(FIELD_REMINDER_TYPE).toString())
                    : REMINDER.None;
        } catch (Exception ex) {
            Log.e(Constants.LOG_TAG, "Something went wrong while attempting to parse reminder type: "
                    + document.get(FIELD_REMINDER_TYPE) + "\t\t for alarm guid: " + id);
            reminderType = REMINDER.None;
        }

        try {
            status = document.contains(FIELD_STATUS)
                    ? STATUS.valueOf(document.get(FIELD_STATUS).toString())
                    : STATUS.New;
        } catch (Exception ex) {
            Log.e(Constants.LOG_TAG, "Something went wrong while attempting to parse status: "
                    + document.get(FIELD_STATUS) + "\t\t for alarm guid: " + id);
            status = STATUS.New;
        }

    }

    public String getGuid() {
        return id;
    }

    public int getRequestCode() { return requestCode; }

    public void updateDesc(String newDesc) { this.desc = newDesc; }

    public String getDesc() {
        return desc;
    }

    public STATUS getStatus() {
        return status;
    }

    public void updateStatus(STATUS newStatus) {
        this.status = newStatus;
    }

    public boolean isArchived() { return archived; }

    public void updateUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public String toString() {
        return desc + " (" + status.toString() + ")";
    }

    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------


    public void updateAlarmDate(int year, int month, int day) {
        Date alarmDate = alarmTime.toDate();
        alarmDate.setYear(year - 1900);     // now reverse the change we made in 'getAlarmDate'
        alarmDate.setMonth(month);
        alarmDate.setDate(day);
        alarmTime = new Timestamp(alarmDate);
    }

    public int[] getAlarmDate() {
        Date alarmDate = alarmTime.toDate();
        return new int[] {
                alarmDate.getYear() + 1900, // "Date" class subtracts 1900 from stored value for some reason?!
                alarmDate.getMonth(),
                alarmDate.getDate() };
    }

    public String getAlarmDateString() {
        return DateTimeUtil.formatTime(alarmTime, DateTimeUtil.FORMAT.Date);
    }

    public boolean isAlarmScheduledForToday() {
        int[] date = getAlarmDate();
        Date now = Timestamp.now().toDate();
        int nowYear = (now.getYear() + 1900);     // stupid 'date' class...
        Log.i(LOG_TAG, "Checking the 'now' year because it's stupid: " + nowYear);
        return (date[0] == nowYear && date[1] == now.getMonth() && date[2] == now.getDate());
    }


    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------


    public void updateAlarmTime(int hours, int minutes) {
        Date alarmDate = alarmTime.toDate();
        alarmDate.setHours(hours);
        alarmDate.setMinutes(minutes);
        alarmDate.setSeconds(0);
        alarmTime = new Timestamp(alarmDate);
    }

    public int[] getAlarmTime() {
        Date alarmDate = alarmTime.toDate();
        return new int[] {
                alarmDate.getHours(),
                alarmDate.getMinutes()
        };
    }

    public long getAlarmTimeMillis() {
        return (TimeUnit.SECONDS.toMillis(alarmTime.getSeconds())
                + TimeUnit.NANOSECONDS.toMillis(alarmTime.getNanoseconds()));
    }

    public String getAlarmTimeString() {
        return DateTimeUtil.formatTime(alarmTime, DateTimeUtil.FORMAT.Time);
    }

    public String getAlarmDateTimeString() {
        return DateTimeUtil.formatTime(alarmTime, DateTimeUtil.FORMAT.DateTime);
    }

    public boolean hasAlarmTimePassed() {
        return (Timestamp.now().compareTo(alarmTime) > 0);
    }


    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------


    public int getReminderCount() {
        return reminderCount;
    }

    public int incrementReminderCount() {
        reminderCount++;
        return reminderCount;
    }

    public void resetReminderCount() {
        reminderCount = 0;
    }

    public boolean isReminderLimitReached() {
        return (reminderCount > Constants.REMINDER_COUNT_LIMIT);
    }

    public void setReminderTime(REMINDER type, boolean increaseCount) {
        Date now = Timestamp.now().toDate();
        now.setSeconds(0);

        reminderType = type;
        switch (reminderType) {
            case None:
                Log.i(LOG_TAG, "Clearing reminder settings for alarm: " + getDesc());
                reminderTime = null;
                resetReminderCount();
                break;

            case Implicit:
                int reminderCount = increaseCount ? incrementReminderCount() : getReminderCount();
                now.setMinutes(now.getMinutes() + Constants.REMINDER_DURATION_MIN_IMPL);
                reminderTime = new Timestamp(now);
                Log.i(LOG_TAG, "System has implicitly set reminder #" + reminderCount
                        + " for alarm: " + getDesc()
                        + " \t\t New reminder time: " + getReminderDateTimeString());
                break;

            case Explicit:
                resetReminderCount();
                now.setMinutes(now.getMinutes() + Constants.REMINDER_DURATION_MIN_EXPL);
                reminderTime = new Timestamp(now);
                Log.i(LOG_TAG, "User has explicitly set a reminder for alarm: " + getDesc()
                        + " \t\t New reminder time: " + getReminderDateTimeString());
                break;
        }
    }

    public long getReminderTimeMillis() {
        return (TimeUnit.SECONDS.toMillis(reminderTime.getSeconds())
                + TimeUnit.NANOSECONDS.toMillis(reminderTime.getNanoseconds()));
    }

    public String getReminderDateTimeString() {
        if (reminderTime != null)
            return DateTimeUtil.formatTime(reminderTime, DateTimeUtil.FORMAT.DateTime);
        else return "";
    }

    public boolean hasReminderTimePassed() {
        return (reminderTime != null && Timestamp.now().compareTo(reminderTime) > 0);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------


    public void setTimeAcknowledged() {
        timeAcknowledged = Timestamp.now();
    }

    public String getAcknowledgedDateTimeString() {
        return DateTimeUtil.formatTime(timeAcknowledged, DateTimeUtil.FORMAT.DateTime);
    }

    public void setTimeCompleted() {
        timeCompleted = Timestamp.now();

        String timeCompletedString =
                DateTimeUtil.formatTime(timeCompleted, DateTimeUtil.FORMAT.DateTime);
        boolean invalidTime = (timeCompletedString == null || timeCompletedString.isEmpty());
        boolean invalidLabel = (desc == null || desc.isEmpty());

        if (!status.equals(STATUS.Complete) || invalidTime || invalidLabel) {
            Log.e(Constants.LOG_TAG, "Can't return a photo path for an invalid record!");
            photoPath = "";
        }

        String formattedTime = timeCompletedString.replace("-", "")
                .replace(":", "").replace(" ", "_");
        String formattedDesc = desc.replace(" ", "");

        // Formatted image string = time_desc.jpg
        photoPath = (formattedTime + "_" + formattedDesc + ".jpg");
    }

    public String getCompletionDateTimeString() {
        return DateTimeUtil.formatTime(timeCompleted, DateTimeUtil.FORMAT.DateTime);
    }

    public String getPhotoPath() { return photoPath; }


    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------


    public Intent getIntent(Context context, Class<?> responseClass, boolean isReminder) {
        Intent intent = new Intent(context, responseClass);
        intent.putExtra(Constants.BUNDLE_ARG_ALARM_GUID, getGuid());
        intent.putExtra(Constants.BUNDLE_ARG_ALERT_TYPE, isReminder
                ? MediaUtil.AUDIO_TYPE.Reminder.toString()
                : MediaUtil.AUDIO_TYPE.Alarm.toString());
        return intent;
    }


    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------


    @Override
    public OnCompleteListener<QuerySnapshot> getListCompletionListener(FirebaseConnector.FbQueryListener listener) {
        return task -> {
            if (!wasSuccessful(task)) return;
            try {
                List<FirebaseConnector.FbDataClass> results = new ArrayList<>();
                for (QueryDocumentSnapshot result : task.getResult()) {
                    results.add(new Alarm(result));
                }
                listener.OnResultsAvailable(results);
            } catch (Exception ex) {
                Log.e(Constants.LOG_TAG, "Something went wrong while trying to convert "
                        + "task results to class instances: "
                        + getClass().getSimpleName(), ex);
            }
        };
    }

    @Override
    public OnCompleteListener<DocumentSnapshot> getSingletonCompletionListener(FirebaseConnector.FbDocListener listener) {
        return task -> {
            if (!wasSuccessful(task)) return;
            try {
                listener.OnResultsAvailable(new Alarm(task.getResult()));
            } catch (Exception ex) {
                Log.e(Constants.LOG_TAG, "Something went wrong while trying to convert "
                        + "task results to class instances: "
                        + getClass().getSimpleName(), ex);
            }
        };
    }

    @Override
    public boolean wasSuccessful(Task task) {
        if (task.isSuccessful()) return true;
        Log.e(Constants.LOG_TAG, "Something went wrong while trying to retrieve "
                + "documents from collection: " + COLLECTION);
        return false;
    }

    @Override
    public Map<String, Object> getFbProperties() {
        Map<String, Object> fbProps = new HashMap<>();
        fbProps.put(FIELD_ALARM_TIME, alarmTime);
        fbProps.put(FIELD_ARCHIVED, archived);
        fbProps.put(FIELD_DESC, desc);
        fbProps.put(FIELD_REMINDER_COUNT, reminderCount);
        fbProps.put(FIELD_REMINDER_TIME, reminderTime);
        fbProps.put(FIELD_REMINDER_TYPE, reminderType);
        fbProps.put(FIELD_REQUEST_CODE, requestCode);
        fbProps.put(FIELD_STATUS, status);
        fbProps.put(FIELD_TIME_ACKNOWLEDGED, timeAcknowledged);
        fbProps.put(FIELD_TIME_COMPLETED, timeCompleted);
        fbProps.put(FIELD_PHOTO_PATH, photoPath);
        fbProps.put(FIELD_USER_EMAIL, userEmail);
        return fbProps;
    }

    public String getFbPropertiesString() {
        String outString = "";
        Map<String, Object> properties = getFbProperties();

        for (Map.Entry<String, Object> entry : properties.entrySet())
            if (entry.getValue() != null)
                outString += (" \n Key = " + entry.getKey() +
                        " \t Value = " + entry.getValue().toString());

        return outString;
    }

}