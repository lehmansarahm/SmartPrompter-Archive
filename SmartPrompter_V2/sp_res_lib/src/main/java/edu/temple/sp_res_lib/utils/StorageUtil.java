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

import static edu.temple.sp_res_lib.utils.Constants.LOG_TAG;
import static edu.temple.sp_res_lib.utils.Constants.SP_KEY_GUIDS;

public class StorageUtil {

    private static final String OUTPUT_DIR = "sp_alarms";
    private static final String ALARM_DELIMITER = "\t";

    public static ArrayList<Alarm> getAlarmsFromStorage(Context ctx) {
        Log.i(LOG_TAG, "Retrieving alarm records from storage!");
        ArrayList<Alarm> alarms = new ArrayList<>();
        String[] guids = StorageUtil.readFileFromStorage(ctx,
                SP_KEY_GUIDS).split(ALARM_DELIMITER);
        int alarmCount = 0;

        for (String guid : guids) {
            if (guid != "" && guid != " ") {
                String jsonAlarm = StorageUtil.readFileFromStorage(ctx, guid);
                Alarm alarm = Alarm.importFromJson(jsonAlarm);
                alarm.setID(alarmCount);
                alarms.add(alarm);
                alarmCount++;
            }
        }

        return alarms;
    }

    public static void writeAlarmsToStorage(Context ctx, ArrayList<Alarm> alarms) {
        String guidListString = "";
        for (Alarm alarm : alarms) {
            guidListString += (alarm.getGuid() + ALARM_DELIMITER);
            String jsonAlarm = Alarm.exportToJson(alarm);
            Log.i(LOG_TAG, "Writing alarm to storage: " + jsonAlarm);
            StorageUtil.writeFileToStorage(ctx, alarm.getGuid(), jsonAlarm);
        }

        StorageUtil.writeFileToStorage(ctx, SP_KEY_GUIDS, guidListString);
    }

    public static void deleteFileFromStorage(Context ctx, String filename) {
        File outputDir = verifyOutputDir(ctx);

        try {
            File outputFile = new File(outputDir, filename);
            if (outputFile.exists())
                outputFile.delete();
            else
                Log.e(LOG_TAG, "Delete file: " + filename
                        + "\t if it doesn't exist!");

            Log.i(LOG_TAG, "Deleted file: " + outputFile.getAbsolutePath());
        } catch (Exception ex) {
            Log.e(LOG_TAG, "An error occurred while deleting file: "
                    + filename, ex);
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

            Log.i(LOG_TAG, "Wrote contents: " + content
                    + "\n \t to file: " + outputFile.getAbsolutePath());
        } catch (Exception ex) {
            Log.e(LOG_TAG, "An error occurred while writing data to file: "
                    + filename, ex);
        }
    }

    private static File verifyOutputDir(Context ctx) {
        File outputDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), OUTPUT_DIR);
        if (!outputDir.exists()) {
            Log.e(LOG_TAG, "Output dir does not exist.  Creating output dir: "
                    + outputDir.getAbsolutePath());
            outputDir.mkdir();
        }

        return outputDir;
    }

}