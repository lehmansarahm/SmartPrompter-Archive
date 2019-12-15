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

    public enum REMINDER { None, Explicit, Implicit }

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

    public static PendingIntent getPI(Context context, Alarm alarm,
                                      Class<?> responseClass, boolean isReminder) {
        Intent intent = alarm.getIntent(context, responseClass, isReminder);
        return PendingIntent.getBroadcast(context, alarm.getGuidInt() /* request code */,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public static boolean isAlarmSet(Context context, Alarm alarm,
                                      Class<?> responseClass, boolean isReminder) {
        Intent intent = alarm.getIntent(context, responseClass, isReminder);
        PendingIntent alarmPI = PendingIntent.getBroadcast(context, alarm.getGuidInt() /* request code */,
                intent, PendingIntent.FLAG_NO_CREATE);
        return (alarmPI != null);
    }

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
        reminderCount = in.readInt();

        String remType = in.readString();
        if (!remType.equals("")) reminderType = REMINDER.valueOf(remType);

        long remTime = in.readLong();
        if (remTime != 0) reminderTime.setTimeInMillis(remTime);

        long ackTime = in.readLong();
        if (ackTime != 0) timeAcknowledged.setTimeInMillis(ackTime);

        long compTime = in.readLong();
        if (compTime != 0) timeCompleted.setTimeInMillis(compTime);

        status = STATUS.valueOf(in.readString());
        archived = (in.readInt() == 1);
    }

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

    public boolean isScheduledForToday() {
        Calendar today = Calendar.getInstance();
        int[] alarmDate = getAlarmDate();
        return (alarmDate[0] == today.get(Calendar.YEAR) &&
                alarmDate[1] == today.get(Calendar.MONTH) &&
                alarmDate[2] == today.get(Calendar.DAY_OF_MONTH));
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public void setNewGuid() {
        this.uuid = String.format("%040d", new BigInteger(UUID.randomUUID()
                .toString().replace("-", ""), 16));
    }

    public String getGuid() {
        return uuid;
    }

    public int getGuidInt() {
        BigInteger bigInt = new BigInteger(uuid);
        return bigInt.intValue();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public String getDesc() {
        return desc;
    }

    public void updateDesc(String newDesc) {
        desc = newDesc;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public STATUS getStatus() { return status; }

    public void updateStatus(STATUS newStatus) {
        status = newStatus;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public boolean isArchived() { return archived; }

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

    @Override
    public String toString() {
        return desc + " (" + status.toString() + ")";
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

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

    public REMINDER getReminderType() {
        return reminderType;
    }

    public void setReminderType(REMINDER type) {
        reminderType = type;
    }

    public boolean hasReminder() {
        return (reminderTime != null);
    }

    public void setReminderTime(Calendar expReminder) {
        reminderTime = expReminder;
    }

    public long getReminderTimeMillis() {
        return reminderTime.getTimeInMillis();
    }

    public String getReminderDateTimeString() {
        return DateTimeUtil.formatTime(reminderTime, DateTimeUtil.FORMAT.DateTime);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public void updateAlarmDate(int y, int m, int d) {
        alarmTime.set(Calendar.YEAR, y);
        alarmTime.set(Calendar.MONTH, m);
        alarmTime.set(Calendar.DAY_OF_MONTH, d);
    }

    public void updateAlarmTime(int h, int m) {
        alarmTime.set(Calendar.HOUR_OF_DAY, h);
        alarmTime.set(Calendar.MINUTE, m);
    }

    public int[] getAlarmDate() {
        return new int[] {
                alarmTime.get(Calendar.YEAR),
                alarmTime.get(Calendar.MONTH),
                alarmTime.get(Calendar.DAY_OF_MONTH)
        };
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

    public String getAlarmDateString() {
        return DateTimeUtil.formatTime(alarmTime, DateTimeUtil.FORMAT.Date);
    }

    public String getAlarmTimeString() {
        return DateTimeUtil.formatTime(alarmTime, DateTimeUtil.FORMAT.Time);
    }

    public String getAlarmDateTimeString() {
        return DateTimeUtil.formatTime(alarmTime, DateTimeUtil.FORMAT.DateTime);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public void setTimeAcknowledged() {
        timeAcknowledged = Calendar.getInstance();
    }

    public String getAcknowledgedDateTimeString() {
        return DateTimeUtil.formatTime(timeAcknowledged, DateTimeUtil.FORMAT.DateTime);
    }

    public void setTimeCompleted() {
        timeCompleted = Calendar.getInstance();
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
        parcel.writeInt(reminderCount);

        if (reminderType != null) parcel.writeString(reminderType.toString());
        else parcel.writeString("");

        if (reminderTime != null) parcel.writeLong(reminderTime.getTimeInMillis());
        else parcel.writeLong(0);

        if (timeAcknowledged != null) parcel.writeLong(timeAcknowledged.getTimeInMillis());
        else parcel.writeLong(0);

        if (timeCompleted != null) parcel.writeLong(timeCompleted.getTimeInMillis());
        else parcel.writeLong(0);

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

}