package edu.temple.sp_res_lib.obj;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.UUID;

import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.DateTimeUtil;
import edu.temple.sp_res_lib.utils.Log;
import edu.temple.sp_res_lib.utils.MediaUtil;

import static edu.temple.sp_res_lib.utils.Constants.LOG_TAG;

public class Alarm implements Parcelable {

    public enum STATUS { New, Active, Unacknowledged, Incomplete, Complete }

    public enum REMINDER { None, Explicit, Implicit}

    private String uuid;
    private String desc;
    private Calendar alarmTime, reminderTime;
    private REMINDER reminderType;
    private int reminderCount;
    private Calendar timeAcknowledged, timeCompleted;
    private STATUS status;
    private boolean archived;

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public static String exportToJson(Alarm alarm) {
        return (new Gson()).toJson(alarm);
    }

    public static Alarm importFromJson(String json) {
        return (new Gson()).fromJson(json, Alarm.class);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public Alarm(String uuid, String desc, long time, STATUS status, boolean archived) {
        this.uuid = uuid;
        this.desc = desc;
        this.alarmTime = Calendar.getInstance();
        this.alarmTime.setTimeInMillis(time);
        this.status = status;
        this.archived = archived;
    }

    public Alarm(Parcel in) {
        uuid = in.readString();
        desc = in.readString();
        alarmTime = Calendar.getInstance();
        alarmTime.setTimeInMillis(in.readLong());

        // NOTE - don't need reminder time, time acknowledged, or time completed for parcel ops ...
        // Only using parcelable methods for displaying records in a list

        status = STATUS.valueOf(in.readString());
        archived = (in.readInt() == 1);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public void setNewGuid() {
        this.uuid = String.format("%040d", new BigInteger(UUID.randomUUID()
                .toString().replace("-", ""), 16));
        // this.uuid = Integer.parseInt(lUUID);
        // this.uuid = UUID.randomUUID().toString();
    }

    public String getGuid() {
        return uuid;
    }

    public int getGuidInt() {
        BigInteger bigInt = new BigInteger(uuid);
        return bigInt.intValue();
    }

    public String getDesc() {
        return desc;
    }

    public void updateDesc(String newDesc) {
        desc = newDesc;
    }

    public STATUS getStatus() { return status; }

    public void updateStatus(STATUS newStatus) {
        this.status = newStatus;

        switch (newStatus) {
            case Incomplete:
                timeAcknowledged = Calendar.getInstance();
                Log.i(LOG_TAG, "Alarm with GUID: " + uuid + "\t was acknowledged at time: "
                        + DateTimeUtil.formatTime(timeAcknowledged, DateTimeUtil.FORMAT.DateTime));
                break;
            case Complete:
                timeCompleted = Calendar.getInstance();
                Log.i(LOG_TAG, "Alarm with GUID: " + uuid + "\t was completed at time: "
                        + DateTimeUtil.formatTime(timeCompleted, DateTimeUtil.FORMAT.DateTime));
                archived = true;
                break;
        }
    }

    public String getPhotoPath() {
        String timeCompletedString =
                DateTimeUtil.formatTime(timeCompleted, DateTimeUtil.FORMAT.DateTime);
        boolean invalidTime = (timeCompletedString == null || timeCompletedString.isEmpty());
        boolean invalidLabel = (desc == null || desc.isEmpty());

        if (!status.equals(STATUS.Complete) || invalidTime || invalidLabel) {
            Log.e(LOG_TAG, "Can't return a photo path for an invalid record!");
            return "";
        }

        String formattedTime = timeCompletedString.replace("-", "")
                .replace(":", "").replace(" ", "_");
        String formattedDesc = desc.replace(" ", "");

        // Formatted image string = time_desc.jpg
        return (formattedTime + "_" + formattedDesc + ".jpg");
    }

    public boolean isArchived() { return archived; }

    @Override
    public String toString() {
        return desc + " (" + status.toString() + ")";
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public boolean hasReminder() {
        return (reminderTime != null);
    }

    public void setReminder(REMINDER type) {
        reminderCount++;
        Log.i(LOG_TAG, "Setting reminder #" + reminderCount + " for alarm: " + desc);

        reminderType = type;
        switch (type) {
            case Explicit:
                Calendar expReminder = Calendar.getInstance();
                expReminder.set(Calendar.SECOND, 0);
                expReminder.add(Calendar.MILLISECOND, Constants.REMINDER_DURATION_EXP.intValue());
                reminderTime = expReminder;
                break;
            case Implicit:
                Calendar impReminder = Calendar.getInstance();
                impReminder.set(Calendar.SECOND, 0);
                impReminder.add(Calendar.MILLISECOND, Constants.REMINDER_DURATION_IMP.intValue());
                reminderTime = impReminder;
                break;
            case None:
                reminderTime = null;
                break;
        }
    }

    public long getReminderTimeMillis() {
        return reminderTime.getTimeInMillis();
    }

    public boolean isReminderLimitReached() {
        return (reminderCount > Constants.REMINDER_COUNT_LIMIT);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public void updateAlarmDate(int y, int m, int d) {
        alarmTime.set(Calendar.YEAR, y);
        alarmTime.set(Calendar.MONTH, m);
        alarmTime.set(Calendar.DAY_OF_MONTH, d);
    }

    public int[] getAlarmDate() {
        return new int[] {
                alarmTime.get(Calendar.YEAR),
                alarmTime.get(Calendar.MONTH),
                alarmTime.get(Calendar.DAY_OF_MONTH)
        };
    }

    public String getAlarmDateString() {
        return DateTimeUtil.formatTime(alarmTime, DateTimeUtil.FORMAT.Date);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public void updateAlarmTime(int h, int m) {
        alarmTime.set(Calendar.HOUR_OF_DAY, h);
        alarmTime.set(Calendar.MINUTE, m);
    }

    public int[] getAlarmTime() {
        return new int[] {
                alarmTime.get(Calendar.HOUR_OF_DAY),
                alarmTime.get(Calendar.MINUTE),
                alarmTime.get(Calendar.AM_PM)
        };
    }

    public long getAlarmTimeMillis() {
        return alarmTime.getTimeInMillis();
    }

    public String getAlarmTimeString() {
        return DateTimeUtil.formatTime(alarmTime, DateTimeUtil.FORMAT.Time);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public String getAlarmDateTimeString() {
        return DateTimeUtil.formatTime(alarmTime, DateTimeUtil.FORMAT.DateTime);
    }

    public String getReminderDateTimeString() {
        return DateTimeUtil.formatTime(reminderTime, DateTimeUtil.FORMAT.DateTime);
    }

    public String getAcknowledgedDateTimeString() {
        return DateTimeUtil.formatTime(timeAcknowledged, DateTimeUtil.FORMAT.DateTime);
    }

    public String getCompletionDateTimeString() {
        return DateTimeUtil.formatTime(timeCompleted, DateTimeUtil.FORMAT.DateTime);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uuid);
        parcel.writeString(desc);
        parcel.writeLong(alarmTime.getTimeInMillis());

        // NOTE - don't need reminder time, time acknowledged, or time completed for parcel ops ...
        // Only using parcelable methods for displaying records in a list

        parcel.writeString(status.toString());
        parcel.writeInt(archived ? 1 : 0);
    }

    public static final Parcelable.Creator<Alarm> CREATOR = new Parcelable.Creator<Alarm>()
    {
        public Alarm createFromParcel(Parcel in)
        {
            return new Alarm(in);
        }
        public Alarm[] newArray(int size)
        {
            return new Alarm[size];
        }
    };

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public PendingIntent getPI(Context context, Class<?> responseClass, boolean isReminder) {
        Intent intent = new Intent(context, responseClass);
        intent.putExtra(Constants.BUNDLE_ARG_ALARM_GUID, getGuid());
        intent.putExtra(Constants.BUNDLE_ARG_ALERT_TYPE, isReminder
                ? MediaUtil.AUDIO_TYPE.Reminder.toString()
                : MediaUtil.AUDIO_TYPE.Alarm.toString());
        return PendingIntent.getBroadcast(context, getGuidInt() /* request code */,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

}