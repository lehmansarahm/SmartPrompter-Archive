package edu.temple.smartprompter_v3.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class StorageUtil {

    private static final String ALARMS_DIR = "sp_alarms";
    private static final String ARCHIVE_DIR = "sp_archive";
    private static final String AUDIO_DIR = "sp_audio";
    private static final String PHOTOS_DIR = "sp_photos";
    private static final String LOGS_DIR = "sp_logs";

    public static Bitmap getImageFromFile(Context ctx, String filename) {
        File outputDir = verifyOutputDir(ctx, PHOTOS_DIR);

        try {
            File imageFile = new File(outputDir, filename);
            if (!imageFile.exists()) {
                Log.e(Constants.LOG_TAG, "Image file does not exist at path: "
                        + imageFile.getAbsolutePath());
                return null;
            }

            Log.i(Constants.LOG_TAG, "Image file exists!  Attempting to retrieve "
                    + "from absolute path: \t\t " + imageFile.getAbsolutePath());
            BitmapFactory.Options options = new BitmapFactory.Options();
            return BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        } catch (Exception ex) {
            Log.e(Constants.LOG_TAG, "An error occurred while retrieving image file: "
                    + filename, ex);
            return null;
        }
    }

    private static File verifyOutputDir(Context ctx, String dirName) {
        File outputDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), dirName);
        if (!outputDir.exists()) {
            Log.e(Constants.LOG_TAG, "Output dir does not exist.  Creating output dir: "
                    + outputDir.getAbsolutePath());
            outputDir.mkdir();
        }

        return outputDir;
    }

}
