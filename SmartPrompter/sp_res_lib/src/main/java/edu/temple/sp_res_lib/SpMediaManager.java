package edu.temple.sp_res_lib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.util.List;

import edu.temple.sp_res_lib.media.AlarmMediaContract;
import edu.temple.sp_res_lib.utils.Constants;

public class SpMediaManager {

    private Context context;

    public SpMediaManager(Context context) {
        this.context = context;
    }

    public void saveImage(String ID, byte[] bytes) {
        ContentValues values = new ContentValues();
        values.put(AlarmMediaContract.ImageEntry.COLUMN_ID, ID);
        values.put(AlarmMediaContract.ImageEntry.COLUMN_MEDIA_TYPE,
                AlarmMediaContract.ImageEntry.TABLE_NAME);
        values.put(AlarmMediaContract.ImageEntry.COLUMN_MEDIA, bytes);

        Uri uri = context.getContentResolver()
                .insert(AlarmMediaContract.ImageEntry.CONTENT_URI, values);
        Log.i(Constants.LOG_TAG, "Inserted new image into media storage.  "
                + "Received URI: " + uri);
    }

    public Bitmap getImage(String ID) {
        String whereClause = (AlarmMediaContract.ImageEntry.COLUMN_ID + "=?");
        String[] args = new String[] { ID };

        Cursor cursor = context.getContentResolver().query(
                AlarmMediaContract.ImageEntry.CONTENT_URI,
                AlarmMediaContract.ImageEntry.ALL_COLUMNS,
                whereClause, args, null);

        List<Bitmap> images = AlarmMediaContract.ImageEntry.populateFromCursor(cursor);
        if (images == null || images.size() == 0) return null;
        else return images.get(0);
    }

}