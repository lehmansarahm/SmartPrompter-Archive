package edu.temple.mci_res_lib2.utils;

import android.view.WindowManager;

public class Constants {

    public static final String LOG_TAG = "MCIReminderAppsV2";
    public enum EXEC_MODES { Simple, Advanced, None }

    public static final int CAN_USE_CAMERA_CODE = 1337;
    public static final int CAN_USE_VIBRATE_CODE = 1338;
    public static final int CAN_READ_EXTERNAL_CODE = 1339;
    public static final int CAN_WRITE_EXTERNAL_CODE = 1340;

    public static final int CAMERA_REQUEST_CODE = 1888;

    public static final String AM = "AM";
    public static final String PM = "PM";

    public static final String INTENT_PARAM_EXEC_MODE = "exec_mode";
    public static final String INTENT_PARAM_ALARM_ID = "alarm_id";
    public static final String INTENT_PARAM_PLAY_TONE = "play_tone";

    public static final int DEFAULT_ALARM_ID = -999;
    public static final boolean DEFAULT_PLAY_TONE = true;

    public static final int NOTIFICATION_PLAY_TIME = 5000; // 15sec in milli's

    public static final int ACKNOWLEDGE_REMINDER_INTERVAL = 60000; // 60sec in milli's
    public static final int ACKNOWLEDGE_REMINDER_LIMIT = 3;

    public static final int COMPLETION_REMINDER_INTERVAL = 5 * 60 * 1000; // 5min in milli's
    public static final int COMPLETION_REMINDER_LIMIT = 1;

    //public static int ALARM_WINDOW_FLAGS = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
    //        + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
    //        + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
    //        + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

}