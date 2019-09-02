package edu.temple.sp_res_lib.utils;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class Constants {

    public static final String LOG_TAG = "SpResLib";

    public static final String SHARED_PREFS_FILENAME = "SmartPrompter_Prefs";
    public static final String SP_KEY_GUIDS = "Current_Alarm_GUIDs";

    public static final int DEFAULT_ALARM_ID = -99999;
    public static final String DEFAULT_ALARM_GUID = "XXXX-YYYY-ZZZZ";
    public static final String DEFAULT_ALARM_DESC = "New Alarm";

    public static final Long REMINDER_DURATION_ACK = TimeUnit.MINUTES.toMillis(2);
    public static final Long REMINDER_DURATION_COMP = TimeUnit.MINUTES.toMillis(2);

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mma");

    // public static final String BUNDLE_ARG_ALARM_ID = "bundle_alarm_id";
    public static final String BUNDLE_ARG_ALARM_GUID = "bundle_alarm_guid";
    public static final String BUNDLE_ARG_ALARM_PLAY_TONE = "bundle_alarm_play_tone";
    public static final String BUNDLE_ARG_ALARM_VIBRATE = "bundle_alarm_vibrate";

    public static final String BUNDLE_ARG_IMAGE_BYTES = "bundle_alarm_image_bytes";

    public static final String BUNDLE_ARG_YEAR = "bundle_year";
    public static final String BUNDLE_ARG_MONTH = "bundle_month";
    public static final String BUNDLE_ARG_DAY = "bundle_day";

    public static final String BUNDLE_ARG_HOUR = "bundle_hour";
    public static final String BUNDLE_ARG_MINUTE = "bundle_minute";

    public static final String BUNDLE_REMIND_ME_LATER_ACK = "bundle_remind_me_later_ack";
    public static final String BUNDLE_REMIND_ME_LATER_COMP = "bundle_remind_me_later_comp";
    public static final String BUNDLE_TASK_COMPLETE = "bundle_task_complete";

}