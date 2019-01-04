package edu.temple.sp_res_lib.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class StorageUtil {

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static File getPublicRootDir() {
        File rootDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), Constants.PUBLIC_DIR_ROOT);
        if (!rootDir.exists()) {
            Log.e(Constants.LOG_TAG, "Media directory: " + rootDir.getName()
                    + " does not exist!  Attempting to create.");
            if (!rootDir.mkdirs())
                Log.e(Constants.LOG_TAG, "Failed to create media directory: "
                        + rootDir.getName());
        }

        Log.i(Constants.LOG_TAG, "Retrieved public root directory: "
                + rootDir.getAbsolutePath());
        return rootDir;
    }

    public static File getPublicDir(File rootDir, String subdirName) {
        File taskMediaDir = new File(rootDir, subdirName);
        if (!taskMediaDir.exists()) {
            Log.e(Constants.LOG_TAG, "Media directory: " + subdirName
                    + " does not exist!  Attempting to create.");
            if (!taskMediaDir.mkdirs())
                Log.e(Constants.LOG_TAG, "Failed to create media directory: "
                        + subdirName);
        }

        Log.i(Constants.LOG_TAG, "Retrieved public directory: "
                + taskMediaDir.getAbsolutePath());
        return taskMediaDir;
    }

    public static void writeToFile(File directory, String filename, Bitmap bmp) {
        try {
            File file = new File(directory, filename);
            if (file.exists()) file.delete();

            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "Something went wrong while trying to write image: "
                    + filename + " to file.", e);
        }
    }

}
