package edu.temple.sp_res_lib.utils;

import android.annotation.SuppressLint;
import android.media.RingtoneManager;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Constants {

    public static final String LOG_TAG = "SpResLib";

    public static final int DEFAULT_ALARM_ID = -99999;
    public static final String DEFAULT_ALARM_GUID = "XXXX-YYYY-ZZZZ";
    public static final String DEFAULT_ALARM_DESC = "New Alarm";

    public static final Long REMINDER_DURATION_ACK = TimeUnit.MINUTES.toMillis(1);
    public static final Long REMINDER_DURATION_COMP = TimeUnit.MINUTES.toMillis(1);

    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");

    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mma");

    public static String formatTimeInMillis(long time, SimpleDateFormat dateFormat) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return dateFormat.format(cal.getTime());
    }

    public static final String BUNDLE_ARG_ALARM_GUID = "bundle_alarm_guid";
    public static final String BUNDLE_ARG_ALARM_WAKEUP = "bundle_arg_alarm_wakeup";

    public static final String BUNDLE_ARG_IMAGE_BYTES = "bundle_alarm_image_bytes";

    public static final String BUNDLE_ARG_YEAR = "bundle_year";
    public static final String BUNDLE_ARG_MONTH = "bundle_month";
    public static final String BUNDLE_ARG_DAY = "bundle_day";

    public static final String BUNDLE_ARG_HOUR = "bundle_hour";
    public static final String BUNDLE_ARG_MINUTE = "bundle_minute";

    public static final String BUNDLE_REMIND_ME_LATER_ACK = "bundle_remind_me_later_ack";
    public static final String BUNDLE_REMIND_ME_LATER_COMP = "bundle_remind_me_later_comp";
    public static final String BUNDLE_TASK_COMPLETE = "bundle_task_complete";

    public static final long ALARM_ALERT_DURATION = TimeUnit.SECONDS.toMillis(15);
    public static final Uri ALARM_ALERT_TONE =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

    public static final boolean PLAY_ALARM_TONE = true;
    public static final boolean PLAY_ALARM_VIBRATE = true;

}