package edu.temple.sp_res_lib.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Log {

    private enum LEVEL { Info, Debug, Warning, Error, UI }

    private static List<String> app_messages = new ArrayList<>();
    private static List<String> ui_messages = new ArrayList<>();

    public static void dump(Context ctx) {
        Calendar now = Calendar.getInstance();
        String dateTime = DateTimeUtil.formatTime(now, DateTimeUtil.FORMAT.Date);

        String logName = (dateTime + " App.csv");
        StorageUtil.appendToLog(ctx, logName, app_messages);

        logName = (dateTime + " UI.csv");
        StorageUtil.appendToLog(ctx, logName, ui_messages);
    }

    public static void i (String logTag, String message) {
        android.util.Log.i(logTag, message);
        app_messages.add(consolidateMessage(LEVEL.Info, logTag, message));
    }

    public static void e (String logTag, String message) {
        android.util.Log.e(logTag, message);
        app_messages.add(consolidateMessage(LEVEL.Error, logTag, message));
    }

    public static void e (String logTag, String message, Exception ex) {
        android.util.Log.e(logTag, message, ex);
        message += ex.getStackTrace();
        app_messages.add(consolidateMessage(LEVEL.Error, logTag, message));
    }

    public static void ui (String logTag, Context context, String message) {
        logTag += "-UI";
        message = (context.getClass().getSimpleName() + " - \t " + message);
        android.util.Log.i(logTag, message);
        ui_messages.add(consolidateMessage(LEVEL.Info, logTag, message));
    }

    private static String consolidateMessage(LEVEL level, String logTag, String message) {
        Calendar now = Calendar.getInstance();
        String date = DateTimeUtil.formatTime(now, DateTimeUtil.FORMAT.Date);
        String time = DateTimeUtil.formatTime(now, DateTimeUtil.FORMAT.Time);
        return (date + "," + time + "," + level.toString() + "," + logTag
                + "," + message + "\n");
    }

}