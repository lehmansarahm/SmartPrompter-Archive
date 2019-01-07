package edu.temple.sp_res_lib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.List;

import edu.temple.sp_res_lib.media.AlarmMediaContract;
import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.MediaUtil;
import edu.temple.sp_res_lib.utils.StorageUtil;

public class SpMediaManager {

    private Context context;

    private File rootDir, imageDir, ringtoneDir;

    public SpMediaManager(Context context) {
        this.context = context;

        rootDir = StorageUtil.getPublicRootDir();
        imageDir = StorageUtil.getPublicDir(rootDir, Constants.PUBLIC_DIR_IMAGES);
        ringtoneDir = StorageUtil.getPublicDir(rootDir, Constants.PUBLIC_DIR_RINGTONES);
    }

    public void saveImage(String ID, byte[] bytes) {
        // TODO - WHY IS THE MEDIA C.P. 'INSERT' METHOD NOT FIRING?!
        /* ContentValues values = new ContentValues();
        values.put(AlarmMediaContract.ImageEntry.COLUMN_ID, ID);
        values.put(AlarmMediaContract.ImageEntry.COLUMN_MEDIA_TYPE,
                AlarmMediaContract.ImageEntry.TABLE_NAME);
        values.put(AlarmMediaContract.ImageEntry.COLUMN_MEDIA, bytes);

        Log.i(Constants.LOG_TAG, "Preparing to save image with ID: " + ID);
        Uri uri = context.getContentResolver()
                .insert(AlarmMediaContract.ImageEntry.CONTENT_URI, values);
        Log.i(Constants.LOG_TAG, "Inserted new image into media storage.  "
                + "Received URI: " + uri); */

        // TODO - get rid of this once we figure out why the C.P. is being stupid ...
        Log.i(Constants.LOG_TAG, "Attempting to write image to file at location: "
                + imageDir.getAbsolutePath() + "/" + ID);
        Bitmap media = MediaUtil.convertToBitmap(bytes);
        StorageUtil.writeToFile(imageDir, ID, media);
    }

    public Bitmap getImage(String ID) {
        // TODO - AGAIN!!  FIGURE OUT WHY C.P. ISN'T WORKING
        /* String whereClause = (AlarmMediaContract.ImageEntry.COLUMN_ID + "=?");
        String[] args = new String[] { ID };

        Cursor cursor = context.getContentResolver().query(
                AlarmMediaContract.ImageEntry.CONTENT_URI,
                AlarmMediaContract.ImageEntry.ALL_COLUMNS,
                whereClause, args, null);

        List<Bitmap> images = AlarmMediaContract.ImageEntry.populateFromCursor(cursor);
        if (images == null || images.size() == 0) return null;
        else return images.get(0); */

        // TODO - get rid of this once we figure out why the C.P. is being stupid ...
        String filepath = (imageDir.getAbsolutePath() + "/" + ID);
        Log.i(Constants.LOG_TAG, "Attempting to retrieve image file by ID: "
                + ID + " \t\t using file path: " + filepath);

        Bitmap media = MediaUtil.loadBitmap(filepath);
        if (media == null) {
            Log.e(Constants.LOG_TAG, "UNABLE TO LOAD IMAGE FROM PATH: \t\t"
                    + filepath);
        }

        return media;
    }

}