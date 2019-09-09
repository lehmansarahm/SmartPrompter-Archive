package edu.temple.sp_res_lib.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTimeUtil {

    public enum FORMAT { Date, Time, DateTime }

    public static String formatTime(Calendar cal, FORMAT format) {
        return formatTimeInMillis(cal.getTimeInMillis(), format);
    }

    public static String formatTimeInMillis(long time, FORMAT format) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return getDateTimeFormat(format).format(cal.getTime());
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
                SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mma");
                return DATE_TIME_FORMAT;
        }
        return null;
    }

}