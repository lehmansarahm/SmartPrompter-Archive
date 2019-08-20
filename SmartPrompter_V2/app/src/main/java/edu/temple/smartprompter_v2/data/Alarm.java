package edu.temple.smartprompter_v2.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Alarm implements Parcelable {

    public String id;
    public String content;
    public String details;

    public Alarm(String id, String content, String details) {
        this.id = id;
        this.content = content;
        this.details = details;
    }

    public Alarm(Parcel in) {
        this.id = in.readString();
        this.content = in.readString();
        this.details = in.readString();
    }

    @Override
    public String toString() {
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(content);
        parcel.writeString(details);
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