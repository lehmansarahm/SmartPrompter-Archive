package edu.temple.smartprompter_v3.res_lib.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

    public static File getAudioFile(Context context, String filename) {
        File audioDir = verifyOutputDir(context, Constants.PACKAGE_NAME_ADMIN, AUDIO_DIR);
        return (new File(audioDir, filename));
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public static void writeImageToFile(Context context, String packageName, String filename, Bitmap bmp) {
        try {
            File outputDir = verifyOutputDir(context, packageName, PHOTOS_DIR);
            File outputFile = new File(outputDir, filename);

            if (!outputFile.exists())
                outputFile.createNewFile(); // fails here

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

    public static Bitmap getImageFromFile(Context context, String packageName, String filename) {
        try {
            File outputDir = verifyOutputDir(context, packageName, PHOTOS_DIR);
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

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private static File verifyOutputDir(Context context, String packageName, String dirName) {
        /* File externalFilesDir = new File(context.getExternalFilesDir(null), "");
        Log.i(Constants.LOG_TAG, "Attempting to access external files dir: "
                + externalFilesDir.getAbsolutePath());

        context.grantUriPermission(Constants.PACKAGE_NAME_ADMIN, Uri.fromFile(externalFilesDir),
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.grantUriPermission(Constants.PACKAGE_NAME_PATIENT, Uri.fromFile(externalFilesDir),
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        File androidDataDir = externalFilesDir.getParentFile().getParentFile();
        Log.i(Constants.LOG_TAG, "Attempting to access Android data dir: "
                + androidDataDir.getAbsolutePath());

        File outputDir = new File(androidDataDir, packageName + "/files/" + dirName);
        Log.i(Constants.LOG_TAG, "Attempting to access PRIMARY external files dir: "
                + outputDir.getAbsolutePath()); */

        File outputDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), dirName);

        if (!outputDir.exists()) {
            Log.e(Constants.LOG_TAG, "Output dir does not exist.  Creating output dir: "
                    + outputDir.getAbsolutePath());
            outputDir.mkdir();
        }

        return outputDir;
    }

}
