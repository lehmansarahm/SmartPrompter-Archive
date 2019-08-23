package edu.temple.sp_res_lib.obj;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

import edu.temple.sp_res_lib.utils.Constants;

public class Alarm implements Parcelable {

    public enum STATUS { New, Active, Unacknowledged, Incomplete, Complete }

    private int id;
    private String uuid;
    private String desc;
    private Calendar time;
    private STATUS status;

    public Alarm(int id, String uuid, String desc, long time, STATUS status) {
        this.id = id;
        this.uuid = uuid;
        this.desc = desc;
        this.time = Calendar.getInstance();
        this.time.setTimeInMillis(time);
        this.status = status;
    }

    public Alarm(Parcel in) {
        id = in.readInt();
        uuid = in.readString();
        desc = in.readString();
        time = Calendar.getInstance();
        time.setTimeInMillis(in.readLong());
        status = STATUS.valueOf(in.readString());
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public int getID() {
        return id;
    }

    public String getUUID() {
        return uuid;
    }

    public String getDesc() {
        return desc;
    }

    public int[] getDate() {
        return new int[] {
                time.get(Calendar.YEAR),
                time.get(Calendar.MONTH),
                time.get(Calendar.DAY_OF_MONTH)
        };
    }

    public String getDateString() { return Constants.DATE_FORMAT.format(time.getTime()); }

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

    public String getStatusString() { return status.toString(); }

    @Override
    public String toString() {
        return desc + " (" + status.toString() + ")";
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(uuid);
        parcel.writeString(desc);
        parcel.writeLong(time.getTimeInMillis());
        parcel.writeString(status.toString());
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