package edu.temple.sp_res_lib.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Log {

    private enum LEVEL { Info, Debug, Warning, Error }
    private static List<String> messages = new ArrayList<>();

    public static void dump(Context ctx) {
        Calendar now = Calendar.getInstance();
        String logName = Constants.formatTimeInMillis(now.getTimeInMillis(), Constants.DATE_FORMAT);
        StorageUtil.appendToLog(ctx, logName + ".csv", messages);
    }

    public static void i (String logTag, String message) {
        android.util.Log.i(logTag, message);
        messages.add(consolidateMessage(LEVEL.Info, logTag, message));
    }

    public static void e (String logTag, String message) {
        android.util.Log.e(logTag, message);
        messages.add(consolidateMessage(LEVEL.Error, logTag, message));
    }

    public static void e (String logTag, String message, Exception ex) {
        android.util.Log.e(logTag, message, ex);
        message += ex.getStackTrace();
        messages.add(consolidateMessage(LEVEL.Error, logTag, message));
    }

    private static String consolidateMessage(LEVEL level, String logTag, String message) {
        Calendar now = Calendar.getInstance();
        String date = Constants.formatTimeInMillis(now.getTimeInMillis(), Constants.DATE_FORMAT);
        String time = Constants.formatTimeInMillis(now.getTimeInMillis(), Constants.TIME_FORMAT);
        return (date + "," + time + "," + level.toString() + "," + logTag
                + "," + message + "\n");
    }

}