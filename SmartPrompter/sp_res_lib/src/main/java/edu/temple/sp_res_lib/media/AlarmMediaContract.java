package edu.temple.sp_res_lib.media;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import edu.temple.sp_res_lib.utils.MediaUtil;

public class AlarmMediaContract {

    public static final String CONTENT_AUTHORITY = "edu.temple.smartprompter.media";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MEDIA_TYPE = "mediaType";
    public static final String COLUMN_MEDIA = "media";

    public static final String[] ALL_COLUMNS = new String[] {
            COLUMN_ID,
            COLUMN_MEDIA_TYPE,
            COLUMN_MEDIA
    };

    public enum MEDIA_TYPE { Image, Ringtone }

    public static Uri getContentUri(MEDIA_TYPE mediaType) {
        return BASE_CONTENT_URI.buildUpon()
                .appendPath(mediaType.toString())
                .build();
    }

    public static List<Bitmap> populateImages(Cursor cursor) {
        List<Bitmap> images = new ArrayList<>();
        if (cursor == null || !cursor.moveToFirst()) return images;

        do {
            int index = cursor.getColumnIndex(COLUMN_MEDIA);
            byte[] imageBlob = cursor.getBlob(index);
            images.add(MediaUtil.convertToBitmap(imageBlob));
        }  while(cursor.moveToNext());

        cursor.close();
        return images;
    }

}