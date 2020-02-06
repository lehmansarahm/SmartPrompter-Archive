package edu.temple.smartprompter_v3.res_lib.utils;

import android.media.RingtoneManager;
import android.net.Uri;

import java.util.concurrent.TimeUnit;

public class Constants {

    public static final String LOG_TAG = "SmartPrompter_v3 ResLib";

    public static final String BUNDLE_ARG_BUTTON_ID = "bundle_button_id";
    public static final String BUNDLE_ARG_FIELD_ID = "bundle_field_id";
    public static final String BUNDLE_ARG_FIELD_OLD_VAL = "bundle_field_old_val";
    public static final String BUNDLE_ARG_FIELD_NEW_VAL = "bundle_field_new_val";

    public static final String BUNDLE_ARG_USER_EMAIL = "bundle_user_email";
    public static final String BUNDLE_ARG_ALARM_GUID = "bundle_alarm_guid";
    public static final String BUNDLE_ARG_ALARM_DESC = "bundle_alarm_desc";

    public static final String BUNDLE_ARG_YEAR = "bundle_year";
    public static final String BUNDLE_ARG_MONTH = "bundle_month";
    public static final String BUNDLE_ARG_DAY = "bundle_day";

    public static final String BUNDLE_ARG_HOUR = "bundle_hour";
    public static final String BUNDLE_ARG_MINUTE = "bundle_minute";

    public static final String BUNDLE_REMIND_ME_LATER_ACK = "bundle_remind_me_later_ack";
    public static final String BUNDLE_REMIND_ME_LATER_COMP = "bundle_remind_me_later_comp";
    public static final String BUNDLE_TASK_COMPLETE = "bundle_task_complete";

    public static final String BUNDLE_ARG_IMAGE_BYTES = "bundle_alarm_image_bytes";

    public static final String BUNDLE_ARG_ALARM_WAKEUP = "bundle_arg_alarm_wakeup";
    public static final String BUNDLE_ARG_PLAY_ALERTS = "bundle_arg_play_alerts";
    public static final String BUNDLE_ARG_ALERT_TYPE = "bundle_arg_alert_type";

    public static final String DEFAULT_ALARM_GUID = "XXXX-YYYY-ZZZZ";
    public static final String DEFAULT_ALARM_DESC = "New Alarm";

    public static final long ALARM_ALERT_DURATION = TimeUnit.SECONDS.toMillis(15);
    public static final Uri ALARM_ALERT_TONE =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

    public static final boolean PLAY_ALARM_TONE = true;
    public static final boolean PLAY_ALARM_VIBRATE = false;

    public static final int REMINDER_DURATION_MIN_EXPL = 15; // minutes
    public static final int REMINDER_DURATION_MIN_IMPL = 5; // minutes
    public static final int REMINDER_COUNT_LIMIT = 2; // zero-based ... actual value = 3

}