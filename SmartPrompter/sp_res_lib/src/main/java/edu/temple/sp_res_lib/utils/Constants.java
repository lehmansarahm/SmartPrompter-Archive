package edu.temple.sp_res_lib.utils;

import android.util.Log;

import java.text.SimpleDateFormat;

public class Constants {

    public static final String LOG_TAG = "SmartPrompter_ResLib";

    public static final String PUBLIC_DIR_ROOT = "smartprompter";
    public static final String PUBLIC_DIR_IMAGES = "images";
    public static final String PUBLIC_DIR_RINGTONES = "ringtones";

    public static final String INTENT_EXTRA_SELECTED_MENU_ITEM = "selected_menu_item";
    public static final String INTENT_EXTRA_ALARM_ID = "intent_extra_alarm_ID";
    public static final String INTENT_EXTRA_ALARM_CURRENT_STATUS = "intent_extra_alarm_current_status";
    public static final String INTENT_EXTRA_REMINDER_ID = "intent_extra_reminder_ID";
    public static final String INTENT_EXTRA_ORIG_TIME = "intent_extra_orig_time";

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mma");

    public static final int DEFAULT_HOUR_OF_DAY = 12;
    public static final int DEFAULT_MINUTE = 0;

    public static final int REQUEST_CODE_OFFSET_ALARM = 1000;
    public static final int REQUEST_CODE_OFFSET_ACK_REM = 2000;
    public static final int REQUEST_CODE_OFFSET_COMP_REM = 3000;

    public enum ALARM_STATUS {Inactive, Active, Unacknowledged, Incomplete, Complete, TimedOut }

    public static int getAlarmRequestCode(int alarmID) {
        return (REQUEST_CODE_OFFSET_ALARM + alarmID);
    }

    public enum REMINDER_TYPE { Acknowledgement, Completion }

    public static int getReminderRequestCode(int reminderID, REMINDER_TYPE type) {
        switch (type) {
            case Acknowledgement:
                return (REQUEST_CODE_OFFSET_ACK_REM + reminderID);
            case Completion:
                return (REQUEST_CODE_OFFSET_COMP_REM + reminderID);
            default:
                return reminderID;
        }
    }

    public static int getReminderInterval(REMINDER_TYPE type) {
        switch (type) {
            case Acknowledgement:
                return 1;   // minutes
            case Completion:
                return 5;   // minutes
            default:
                Log.e(Constants.LOG_TAG, "Unknown reminder type: " + type.toString());
                return -1;
        }
    }

    public static int getReminderLimit(REMINDER_TYPE type) {
        switch (type) {
            case Completion:
                return 2;   // attempts
            case Acknowledgement:
                return 3;
            default:
                Log.e(Constants.LOG_TAG, "Unknown reminder type: " + type.toString());
                return -1;
        }
    }

}