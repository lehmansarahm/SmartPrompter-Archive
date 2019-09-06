package edu.temple.sp_res_lib.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;

import java.io.ByteArrayOutputStream;

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
        return byteArray;
    }

    public static Ringtone loadRingtone(String filepath) {
        // TODO - figure out how to load a ringtone from file
        return null;
    }

}