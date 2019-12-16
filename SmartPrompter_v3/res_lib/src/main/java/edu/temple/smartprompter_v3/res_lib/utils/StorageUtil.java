package edu.temple.smartprompter_v3.res_lib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class StorageUtil {

    private static final String ALARMS_DIR = "sp_alarms";
    private static final String ARCHIVE_DIR = "sp_archive";
    private static final String AUDIO_DIR = "sp_audio";
    private static final String PHOTOS_DIR = "sp_photos";
    private static final String LOGS_DIR = "sp_logs";

    public static File getAudioFile(String filename) {
        File audioDir = verifyOutputDir(AUDIO_DIR);
        return (new File(audioDir, filename));
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public static void writeImageToFile(String filename, Bitmap bmp) {
        try {
            File outputDir = verifyOutputDir(PHOTOS_DIR);
            File outputFile = new File(outputDir, filename);
            if (!outputFile.exists())
                outputFile.createNewFile();

            Log.i(Constants.LOG_TAG, "Attempting to write image to file at location: "
                    + outputFile.getAbsolutePath());

            FileOutputStream out = new FileOutputStream(outputFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            Log.i(Constants.LOG_TAG, "Wrote contents to file: "
                    + outputFile.getAbsolutePath());
        } catch (Exception ex) {
            Log.e(Constants.LOG_TAG, "An error occurred while writing data to file: "
                    + filename, ex);
        }
    }

    public static Bitmap getImageFromFile(String filename) {
        try {
            verifyOutputDir(PHOTOS_DIR);
            File imageFile = new File(filename); // new File(outputDir, filename);
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

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private static File verifyOutputDir(String dirName) {
        File outputDir = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOCUMENTS), dirName);
        if (!outputDir.exists()) {
            Log.e(Constants.LOG_TAG, "Output dir does not exist.  Creating output dir: "
                    + outputDir.getAbsolutePath());
            outputDir.mkdir();
        }

        return outputDir;
    }

}
