package edu.temple.sp_res_lib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.obj.SurveyQuestion;

import static edu.temple.sp_res_lib.utils.Constants.LOG_TAG;

public class StorageUtil {

    private static final String ALARMS_DIR = "sp_alarms";
    private static final String AUDIO_DIR = "sp_audio";
    private static final String PHOTOS_DIR = "sp_photos";
    private static final String LOGS_DIR = "sp_logs";

    private static final String DIRTY_FLAG = "dirty";

    public static File getAudioFile(Context ctx, String filename) {
        File audioDir = verifyOutputDir(ctx, AUDIO_DIR);
        return (new File(audioDir, filename));
    }

    public static String[] getLogsDirContents(Context context) {
        Log.i(LOG_TAG, "Retrieving names of files currently in SP_LOGS directory.");
        File logsDir = verifyOutputDir(context, LOGS_DIR);

        if (logsDir.exists() && logsDir.list() != null && logsDir.list().length > 0) {
            String[] logs = logsDir.list();
            for (int i = 0; i < logs.length; i++) {
                File logFile = new File(logsDir, logs[i]);
                logs[i] = logFile.getAbsolutePath();
            }
            return logs;
        } else {
            return new String[]{};
        }
    }

    public static File getAlarmsDirectory(Context context) {
        return verifyOutputDir(context, ALARMS_DIR);
    }

    public static ArrayList<Alarm> getAlarmsFromStorage(Context ctx) {
        Log.i(LOG_TAG, "Retrieving alarm records from storage!");
        ArrayList<Alarm> alarms = new ArrayList<>();
        File alarmsDir = verifyOutputDir(ctx, ALARMS_DIR);

        if (alarmsDir.exists() && alarmsDir.list() != null && alarmsDir.list().length > 0) {
            for (String alarmFile : alarmsDir.list()) {
                Log.i(LOG_TAG, "Scanning file: " + alarmFile);
                String jsonAlarm = StorageUtil.readFile(ctx, ALARMS_DIR, alarmFile);
                if (jsonAlarm != null && !jsonAlarm.equals("")) {
                    Alarm alarm = Alarm.importFromJson(jsonAlarm);
                    alarms.add(alarm);
                }
            }
        }

        return alarms;
    }

    public static boolean getDirtyStatus(Context ctx) {
        File alarmsDir = verifyOutputDir(ctx, ALARMS_DIR);
        File dirtyFile = new File(alarmsDir, DIRTY_FLAG);
        return (dirtyFile.exists());
    }

    public static void writeDirtyFlag(Context ctx) {
        StorageUtil.writeToFile(ctx, ALARMS_DIR, DIRTY_FLAG, "");
    }

    public static void writeAlarmsToStorage(Context ctx, ArrayList<Alarm> alarms) {
        for (Alarm alarm : alarms) {
            String jsonAlarm = Alarm.exportToJson(alarm);
            Log.i(LOG_TAG, "Writing alarm to storage: " + jsonAlarm);

            if (alarm.isArchived()) {
                StorageUtil.deleteAlarmFromStorage(ctx, alarm);
                StorageUtil.writeToFile(ctx, LOGS_DIR, alarm.getGuid(), jsonAlarm);
            } else {
                StorageUtil.writeToFile(ctx, ALARMS_DIR, alarm.getGuid(), jsonAlarm);
            }
        }
    }

    public static void deleteDirtyFlag(Context ctx) {
        StorageUtil.deleteFile(ctx, ALARMS_DIR, DIRTY_FLAG);
    }

    public static void deleteAlarmFromStorage(Context ctx, Alarm alarm) {
        deleteFile(ctx, ALARMS_DIR, alarm.getGuid());
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public static ArrayList<Alarm> getLogsFromStorage(Context ctx) {
        Log.i(LOG_TAG, "Retrieving log records from storage!");
        ArrayList<Alarm> alarms = new ArrayList<>();
        File alarmsDir = verifyOutputDir(ctx, LOGS_DIR);

        if (alarmsDir.exists() && alarmsDir.list() != null && alarmsDir.list().length > 0) {
            for (String alarmFile : alarmsDir.list()) {
                Log.i(LOG_TAG, "Scanning file: " + alarmFile);
                if (!alarmFile.endsWith(".csv")) {
                    String jsonAlarm = StorageUtil.readFile(ctx, LOGS_DIR, alarmFile);
                    Alarm alarm = Alarm.importFromJson(jsonAlarm);
                    alarms.add(alarm);
                }
            }
        }

        return alarms;
    }

    public static void appendToLog(Context ctx, String filename, List<String> content) {
        appendToFile(ctx, LOGS_DIR, filename, content);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public static void writeImageToFile(Context ctx, String filename, Bitmap bmp) {
        File outputDir = verifyOutputDir(ctx, PHOTOS_DIR);

        try {
            File outputFile = new File(outputDir, filename);
            if (!outputFile.exists())
                outputFile.createNewFile();

            Log.i(LOG_TAG, "Attempting to write image to file at location: "
                    + outputFile.getAbsolutePath());

            FileOutputStream out = new FileOutputStream(outputFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            Log.i(LOG_TAG, "Wrote contents to file: "
                    + outputFile.getAbsolutePath());
        } catch (Exception ex) {
            Log.e(LOG_TAG, "An error occurred while writing data to file: "
                    + filename, ex);
        }
    }

    public static Bitmap getImageFromFile(Context ctx, String filename) {
        File outputDir = verifyOutputDir(ctx, PHOTOS_DIR);

        try {
            File imageFile = new File(outputDir, filename);
            if (!imageFile.exists()) {
                Log.e(LOG_TAG, "Image file does not exist at path: "
                        + imageFile.getAbsolutePath());
                return null;
            }

            Log.i(LOG_TAG, "Image file exists!  Attempting to retrieve "
                    + "from absolute path: \t\t " + imageFile.getAbsolutePath());
            BitmapFactory.Options options = new BitmapFactory.Options();
            return BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        } catch (Exception ex) {
            Log.e(LOG_TAG, "An error occurred while retrieving image file: "
                    + filename, ex);
            return null;
        }
    }

    public static ArrayList<SurveyQuestion> getSurveyQuestionsFromStorage() {
        Log.i(LOG_TAG, "Retrieving survey questions from storage!");

        // TODO - populate survey question list for real
        ArrayList<SurveyQuestion> questions = new ArrayList<>();
        return questions;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private static String readFile(Context ctx, String dirName, String filename) {
        File outputDir = verifyOutputDir(ctx, dirName);
        String contents = "";

        try {
            File outputFile = new File(outputDir, filename);
            if (outputFile.exists()) {
                String line;
                BufferedReader br = new BufferedReader(new FileReader(outputFile));
                while ((line = br.readLine()) != null) contents += line;
            } else {
                Log.e(LOG_TAG, "Cannot read from file: "
                        + filename + "\t if it doesn't exist!");
            }

            Log.i(LOG_TAG, "Read contents: " + contents
                    + "\n \t from file: " + outputFile.getAbsolutePath());
        } catch (Exception ex) {
            Log.e(LOG_TAG, "An error occurred while reading data from file: "
                    + filename, ex);
        }

        return contents;
    }

    private static void writeToFile(Context ctx, String dirName, String filename, String content) {
        File outputDir = verifyOutputDir(ctx, dirName);

        try {
            File outputFile = new File(outputDir, filename);
            if (!outputFile.exists())
                outputFile.createNewFile();

            FileWriter writer = new FileWriter(outputFile);
            writer.write(content);
            writer.flush();
            writer.close();

            Log.i(LOG_TAG, "Wrote contents: " + content
                    + "\n \t to file: " + outputFile.getAbsolutePath());
        } catch (Exception ex) {
            Log.e(LOG_TAG, "An error occurred while writing data to file: "
                    + filename, ex);
        }
    }

    private static void appendToFile(Context ctx, String dirName, String filename, List<String> content) {
        File outputDir = verifyOutputDir(ctx, dirName);

        try {
            File outputFile = new File(outputDir, filename);
            if (!outputFile.exists())
                outputFile.createNewFile();

            FileWriter writer = new FileWriter(outputFile);
            for (String contentLine : content)
                writer.append(contentLine);
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            Log.e(LOG_TAG, "An error occurred while appending data to file: "
                    + filename, ex);
        }
    }

    private static void deleteFile(Context ctx, String dirName, String filename) {
        File outputDir = verifyOutputDir(ctx, dirName);

        try {
            File outputFile = new File(outputDir, filename);
            if (outputFile.exists())
                outputFile.delete();
            else
                Log.e(LOG_TAG, "Can't delete file: " + filename
                        + "\t if it doesn't exist!");

            Log.i(LOG_TAG, "Deleted file: " + outputFile.getAbsolutePath());
        } catch (Exception ex) {
            Log.e(LOG_TAG, "An error occurred while deleting file: "
                    + filename, ex);
        }
    }

    private static File verifyOutputDir(Context ctx, String dirName) {
        File outputDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), dirName);
        if (!outputDir.exists()) {
            Log.e(LOG_TAG, "Output dir does not exist.  Creating output dir: "
                    + outputDir.getAbsolutePath());
            outputDir.mkdir();
        }

        return outputDir;
    }

}