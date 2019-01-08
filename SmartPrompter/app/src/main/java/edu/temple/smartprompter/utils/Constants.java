package edu.temple.smartprompter.utils;

import android.app.PendingIntent;

public class Constants {

    public static final String LOG_TAG = "SmartPrompter";

    public static final String BUNDLE_ARG_ALARM_ID = "bundle_arg_alarm_id";
    public static final String BUNDLE_ARG_IMAGE_BYTES = "bundle_arg_image_bytes";

    public static final String CHANNEL_ID = "smartprompter";
    public static final CharSequence CHANNEL_NAME = "channel_smartprompter";
    public static final String CHANNEL_DESCRIPTION = "channel for smartprompter notifications";

    public static final int DEFAULT_ALARM_ID = -1;

    public static final int PENDING_INTENT_FLAGS = PendingIntent.FLAG_CANCEL_CURRENT;

    public static final String INTENT_EXTRA_REMINDER_ID =
            edu.temple.sp_res_lib.utils.Constants.INTENT_EXTRA_REMINDER_ID;

    public static final String INTENT_EXTRA_ALARM_ID =
            edu.temple.sp_res_lib.utils.Constants.INTENT_EXTRA_ALARM_ID;

    public static final String INTENT_EXTRA_ALARM_CURRENT_STATUS =
            edu.temple.sp_res_lib.utils.Constants.INTENT_EXTRA_ALARM_CURRENT_STATUS;

    public static final String INTENT_EXTRA_ORIG_TIME =
            edu.temple.sp_res_lib.utils.Constants.INTENT_EXTRA_ORIG_TIME;

}