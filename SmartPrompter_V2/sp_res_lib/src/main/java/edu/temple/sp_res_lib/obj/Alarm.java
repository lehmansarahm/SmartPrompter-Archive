package edu.temple.sp_res_lib.obj;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.UUID;

import edu.temple.sp_res_lib.utils.Constants;

public class Alarm implements Parcelable {

    public enum STATUS { New, Active, Unacknowledged, Incomplete, Complete }

    private String uuid;
    private String desc;
    private Calendar time;
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
        this.time = Calendar.getInstance();
        this.time.setTimeInMillis(time);
        this.status = status;
        this.archived = archived;
    }

    public Alarm(Parcel in) {
        uuid = in.readString();
        desc = in.readString();
        time = Calendar.getInstance();
        time.setTimeInMillis(in.readLong());
        status = STATUS.valueOf(in.readString());
        archived = (in.readInt() == 1);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public void setNewGuid() {
        this.uuid = String.format("%040d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
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

    public boolean isArchived() { return archived; }

    @Override
    public String toString() {
        return desc + " (" + status.toString() + ")";
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public void updateDate(int y, int m, int d) {
        time.set(Calendar.YEAR, y);
        time.set(Calendar.MONTH, m);
        time.set(Calendar.DAY_OF_MONTH, d);
    }

    public int[] getDate() {
        return new int[] {
                time.get(Calendar.YEAR),
                time.get(Calendar.MONTH),
                time.get(Calendar.DAY_OF_MONTH)
        };
    }

    public String getDateString() { return Constants.DATE_FORMAT.format(time.getTime()); }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public void updateTime(int h, int m) {
        time.set(Calendar.HOUR_OF_DAY, h);
        time.set(Calendar.MINUTE, m);
    }

    public int[] getTime() {
        return new int[] {
                time.get(Calendar.HOUR_OF_DAY),
                time.get(Calendar.MINUTE),
                time.get(Calendar.AM_PM)
        };
    }

    public String getTimeString() { return Constants.TIME_FORMAT.format(time.getTime()); }

    public long getTimeInMillis() {
        return time.getTimeInMillis();
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
        parcel.writeLong(time.getTimeInMillis());
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