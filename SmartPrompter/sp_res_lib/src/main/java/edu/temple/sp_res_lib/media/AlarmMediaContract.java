package edu.temple.sp_res_lib.media;

import android.net.Uri;

public class AlarmMediaContract {

    public static final String CONTENT_AUTHORITY = "edu.temple.smartprompter.media";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public enum MEDIA_TYPE { Image, Ringtone }

    public static Uri getContentUriWithID(MEDIA_TYPE mediaType, String id) {
        return BASE_CONTENT_URI.buildUpon()
                .appendPath(mediaType.toString())
                .appendPath(id)
                .build();
    }

}