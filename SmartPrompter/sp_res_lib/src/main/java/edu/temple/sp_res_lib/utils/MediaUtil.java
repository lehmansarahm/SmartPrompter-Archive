package edu.temple.sp_res_lib.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class MediaUtil {

    public static Bitmap convertToBitmap(byte[] bytes) {
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            return bitmap;
        } catch (Exception ex) {
            Log.e(Constants.LOG_TAG, "Something went wrong while attempting to "
                    + "converting byte array to bitmap.", ex);
            return null;
        }
    }

    public static byte[] convertToByteArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bmp.recycle();
        return byteArray;
    }

    public static Bitmap loadBitmap(String filepath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(filepath, options);
    }

    public static Ringtone loadRingtone(String filepath) {
        // TODO - figure out how to load a ringtone from file
        return null;
    }

}