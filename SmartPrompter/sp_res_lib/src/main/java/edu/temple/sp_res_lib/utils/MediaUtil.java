package edu.temple.sp_res_lib.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MediaUtil {

    public static Bitmap convertToBitmap(byte[] bytes) {
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            // TODO - some sort of dynamic scaling to make sure it displays nicely
            // return Bitmap.createScaledBitmap(bitmap, 500, 500, false);
            return bitmap;
        } catch (Exception ex) {
            Log.e(Constants.LOG_TAG, "Something went wrong while attempting to "
                    + "converting byte array to bitmap.", ex);
            return null;
        }
    }

    public static byte[] convertToByteArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bmp.recycle();
        return byteArray;
    }

    public static Bitmap loadBitmap(String filepath) {
        // First, see if the file exists
        File imageFile = new File(filepath);
        if (!imageFile.exists()) {
            Log.e(Constants.LOG_TAG, "Image file does not exist at path: "
                    + imageFile.getAbsolutePath());
            return null;
        }

        // PATH: /storage/emulated/0/Documents/smartprompter/images/headshot_small.jpg

        Log.i(Constants.LOG_TAG, "Image file exists!  Attempting to retrieve "
                + "from absolute path: \t\t " + imageFile.getAbsolutePath());
        BitmapFactory.Options options = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
    }

    public static Ringtone loadRingtone(String filepath) {
        // TODO - figure out how to load a ringtone from file
        return null;
    }

}