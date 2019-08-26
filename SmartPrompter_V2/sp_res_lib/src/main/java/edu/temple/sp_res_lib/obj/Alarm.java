package edu.temple.sp_res_lib.obj;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.UUID;

import edu.temple.sp_res_lib.utils.Constants;

public class Alarm implements Parcelable {

    public enum STATUS { New, Active, Unacknowledged, Incomplete, Complete }

    private String uuid;
    private String desc;
    private Calendar alarmTime, timeAcknowledged, timeCompleted;
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
        return Integer.parseInt(uuid);
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
                Log.i(Constants.LOG_TAG, "Alarm with GUID: " + uuid + "\t was acknowledged at time: "
                        + Constants.DATE_TIME_FORMAT.format(timeAcknowledged.getTime()));
                break;
            case Complete:
                timeCompleted = Calendar.getInstance();
                Log.i(Constants.LOG_TAG, "Alarm with GUID: " + uuid + "\t was completed at time: "
                        + Constants.DATE_TIME_FORMAT.format(timeCompleted.getTime()));
                archived = true;
                break;
        }
    }

    public String getPhotoPath() {
        String timeCompletedString = Constants.DATE_TIME_FORMAT.format(timeCompleted.getTime());
        boolean invalidTime = (timeCompletedString == null || timeCompletedString.isEmpty());
        boolean invalidLabel = (desc == null || desc.isEmpty());

        if (!status.equals(STATUS.Complete) || invalidTime || invalidLabel) {
            Log.e(Constants.LOG_TAG, "Can't return a photo path for an invalid record!");
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

    public String getAlarmDateString() { return Constants.DATE_FORMAT.format(alarmTime.getTime()); }

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

    public String getAlarmTimeString() { return Constants.TIME_FORMAT.format(alarmTime.getTime()); }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public String getAlarmDateTimeString() {
        return Constants.DATE_TIME_FORMAT.format(alarmTime.getTime());
    }

    public String getAcknowledgedDateTimeString() {
        return Constants.DATE_TIME_FORMAT.format(timeAcknowledged.getTime());
    }

    public String getCompletionDateTimeString() {
        return Constants.DATE_TIME_FORMAT.format(timeCompleted.getTime());
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