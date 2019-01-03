package edu.temple.sp_res_lib.content;

import android.net.Uri;
import android.provider.BaseColumns;

public final class AlarmDbContract {

    public static final String CONTENT_AUTHORITY = "edu.temple.sp_res_lib.alarms";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class AlarmEntry implements BaseColumns {

        public static final String TABLE_NAME = "alarm";

        /*
         * AlarmEntry did not explicitly declare a column called "_ID". However,
         * AlarmEntry implements the interface, "BaseColumns", which does have a field
         * named "_ID". We use that to designate our table's primary key.
         */

        public static final String COLUMN_LABEL = "label";
        public static final String COLUMN_STATUS = "status";

        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_MONTH = "month";
        public static final String COLUMN_DAY_OF_MONTH = "dayOfMonth";

        public static final String COLUMN_HOUR_OF_DAY = "hourOfDay";
        public static final String COLUMN_MINUTE = "minute";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME)
                .build();

        public static Uri getContentUriWithID(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }

    }

}