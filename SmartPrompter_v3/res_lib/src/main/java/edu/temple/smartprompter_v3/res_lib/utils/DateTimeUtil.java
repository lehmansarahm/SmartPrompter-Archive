package edu.temple.smartprompter_v3.res_lib.utils;

import android.annotation.SuppressLint;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;

public class DateTimeUtil {

    public enum FORMAT { Date, Time, DateTime }

    public static String formatTime(Timestamp timestamp, FORMAT format) {
        return (getDateTimeFormat(format)).format(timestamp.toDate());
    }

    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat getDateTimeFormat(FORMAT format) {
        switch (format) {
            case Date:
                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
                return DATE_FORMAT;
            case Time:
                SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");
                return TIME_FORMAT;
            case DateTime:
                SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("MM-dd-yyyy      hh:mma");
                return DATE_TIME_FORMAT;
        }
        return null;
    }

}