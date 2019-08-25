package edu.temple.sp_res_lib.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.obj.SurveyQuestion;

public class StorageUtil {

    private static final String OUTPUT_DIR = "sp_alarms";

    public static ArrayList<Alarm> getAlarmsFromStorage(Context ctx) {
        Log.i(Constants.LOG_TAG, "Retrieving alarm records from storage!");
        ArrayList<Alarm> alarms = new ArrayList<>();
        File alarmsDir = verifyOutputDir(ctx);
        int alarmCount = 0;

        for (String alarmFile : alarmsDir.list()) {
            Log.i(Constants.LOG_TAG, "Scanning file: " + alarmFile);
            String jsonAlarm = StorageUtil.readFileFromStorage(ctx, alarmFile);
            Alarm alarm = Alarm.importFromJson(jsonAlarm);
            alarm.setID(alarmCount);
            alarms.add(alarm);
            alarmCount++;
        }

        return alarms;
    }

    public static void writeAlarmsToStorage(Context ctx, ArrayList<Alarm> alarms) {
        for (Alarm alarm : alarms) {
            String jsonAlarm = Alarm.exportToJson(alarm);
            Log.i(Constants.LOG_TAG, "Writing alarm to storage: " + jsonAlarm);
            StorageUtil.writeFileToStorage(ctx, alarm.getGuid(), jsonAlarm);
        }
    }

    public static void deleteFileFromStorage(Context ctx, String filename) {
        File outputDir = verifyOutputDir(ctx);

        try {
            File outputFile = new File(outputDir, filename);
            if (outputFile.exists())
                outputFile.delete();
            else
                Log.e(Constants.LOG_TAG, "Can't delete file: " + filename
                        + "\t if it doesn't exist!");

            Log.i(Constants.LOG_TAG, "Deleted file: " + outputFile.getAbsolutePath());
        } catch (Exception ex) {
            Log.e(Constants.LOG_TAG, "An error occurred while deleting file: "
                    + filename, ex);
        }
    }

    public static ArrayList<SurveyQuestion> getSurveyQuestionsFromStorage() {
        Log.i(Constants.LOG_TAG, "Retrieving survey questions from storage!");

        // TODO - populate survey question list for real
        ArrayList<SurveyQuestion> questions = new ArrayList<>();
        return questions;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private static String readFileFromStorage(Context ctx, String filename) {
        File outputDir = verifyOutputDir(ctx);
        String contents = "";

        try {
            File outputFile = new File(outputDir, filename);
            if (outputFile.exists()) {
                String line;
                BufferedReader br = new BufferedReader(new FileReader(outputFile));
                while ((line = br.readLine()) != null) contents += line;
            } else {
                Log.e(Constants.LOG_TAG, "Cannot read from file: "
                        + filename + "\t if it doesn't exist!");
            }

            Log.i(Constants.LOG_TAG, "Read contents: " + contents
                    + "\n \t from file: " + outputFile.getAbsolutePath());
        } catch (Exception ex) {
            Log.e(Constants.LOG_TAG, "An error occurred while reading data from file: "
                    + filename, ex);
        }

        return contents;
    }

    private static void writeFileToStorage(Context ctx, String filename, String content) {
        File outputDir = verifyOutputDir(ctx);

        try {
            File outputFile = new File(outputDir, filename);
            if (!outputFile.exists())
                outputFile.createNewFile();

            FileWriter writer = new FileWriter(outputFile);
            writer.write(content);
            writer.flush();
            writer.close();

            Log.i(Constants.LOG_TAG, "Wrote contents: " + content
                    + "\n \t to file: " + outputFile.getAbsolutePath());
        } catch (Exception ex) {
            Log.e(Constants.LOG_TAG, "An error occurred while writing data to file: "
                    + filename, ex);
        }
    }

    private static File verifyOutputDir(Context ctx) {
        File outputDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), OUTPUT_DIR);
        if (!outputDir.exists()) {
            Log.e(Constants.LOG_TAG, "Output dir does not exist.  Creating output dir: "
                    + outputDir.getAbsolutePath());
            outputDir.mkdir();
        }

        return outputDir;
    }

}