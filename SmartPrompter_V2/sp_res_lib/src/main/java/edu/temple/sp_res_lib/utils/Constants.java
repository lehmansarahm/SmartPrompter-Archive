package edu.temple.sp_res_lib.utils;

import java.text.SimpleDateFormat;

public class Constants {

    public static final String LOG_TAG = "SpResLib";

    public static final int DEFAULT_ALARM_ID = -99999;
    public static final String DEFAULT_ALARM_GUID = "XXXX-YYYY-ZZZZ";
    public static final String DEFAULT_ALARM_DESC = "New Alarm";

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mma");

    public static final String BUNDLE_ARG_ALARM_ID = "bundle_alarm_id";

    public static final String BUNDLE_ARG_IMAGE_BYTES = "bundle_alarm_image_bytes";

    public static final String BUNDLE_ARG_YEAR = "bundle_year";
    public static final String BUNDLE_ARG_MONTH = "bundle_month";
    public static final String BUNDLE_ARG_DAY = "bundle_day";

    public static final String BUNDLE_ARG_HOUR = "bundle_hour";
    public static final String BUNDLE_ARG_MINUTE = "bundle_minute";

}