package edu.temple.sp_res_lib.obj;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

public class Alarm implements Parcelable {

    public enum STATUS { Active, Unacknowledged, Incomplete, Complete }

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

    public long getTimeInMillis() {
        return time.getTimeInMillis();
    }

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