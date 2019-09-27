package edu.temple.sp_admin;

import android.app.Application;
import android.util.DisplayMetrics;

import java.util.ArrayList;

import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.obj.SurveyQuestion;
import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.Log;
import edu.temple.sp_res_lib.utils.StorageUtil;

import static edu.temple.sp_res_lib.utils.Constants.DEFAULT_ALARM_ID;

public class SpAdmin extends Application {

    public static final String LOG_TAG = "SmartPrompter-Admin";

    private ArrayList<Alarm> alarms, logs;
    private ArrayList<SurveyQuestion> questions;

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onCreate() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        Log.e(LOG_TAG, "Launching on device with dp height: " + dpHeight
                + " \t\t and dp width: " + dpWidth);

        getCurrentAlarms();
        getArchivedAlarms();
        getQuestions();

        super.onCreate();
    }

    public Alarm getAlarm(String guid) {
        for (Alarm alarm : alarms) {
            if (alarm.getGuid().equals(guid))
                return alarm;
        }
        return null;
    }

    public Alarm getAlarmLog(String guid) {
        for (Alarm alarm : logs) {
            if (alarm.getGuid().equals(guid))
                return alarm;
        }
        return null;
    }

    public ArrayList<Alarm> getCurrentAlarms() {
        alarms = StorageUtil.getAlarmsFromStorage(this);
        return alarms;
    }

    public ArrayList<Alarm> getArchivedAlarms() {
        logs = StorageUtil.getLogsFromStorage(this);
        return logs;
    }

    public ArrayList<SurveyQuestion> getQuestions() {
        questions = StorageUtil.getSurveyQuestionsFromStorage();
        return this.questions;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public void saveAlarm(Alarm newAlarm) {
        Log.i(LOG_TAG, "Attempting to save alarm record with: "
                + "\t GUID: " + newAlarm.getGuid()
                + "\t Description: " + newAlarm.getDesc()
                + "\t Date: " + newAlarm.getAlarmDateString()
                + "\t Time: " + newAlarm.getAlarmTimeString()
                + "\t Status: " + newAlarm.getStatus()
                + "\t Is Archived: " + newAlarm.isArchived());

        if (newAlarm.getGuid().equals(Constants.DEFAULT_ALARM_GUID)) {
            Log.i(LOG_TAG, "Creating new record for alarm: " + newAlarm.getDesc());
            newAlarm.setNewGuid();
            alarms.add(newAlarm);
        } else {
            Log.i(LOG_TAG, "Updating details for existing alarm: " + newAlarm.getDesc());
            int oldAlarmIndex = getAlarmIndex(newAlarm);
            if (oldAlarmIndex != DEFAULT_ALARM_ID)
                alarms.set(oldAlarmIndex, newAlarm);
        }

        commitChanges();
    }

    public void deleteAlarm(Alarm alarm) {
        Log.i(LOG_TAG, "Attempting to delete alarm record with: "
                + "\t GUID: " + alarm.getGuid()
                + "\t Description: " + alarm.getDesc());

        StorageUtil.deleteAlarmFromStorage(this, alarm);

        int oldAlarmIndex = getAlarmIndex(alarm);
        if (oldAlarmIndex != DEFAULT_ALARM_ID)
            alarms.remove(oldAlarmIndex);
    }

    public void commitChanges() {
        StorageUtil.writeAlarmsToStorage(this, alarms);
        StorageUtil.writeDirtyFlag(this);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private int getAlarmIndex(Alarm alarm) {
        for (Alarm oldAlarm : alarms) {
            if (oldAlarm.getGuid().equals(alarm.getGuid())) {
                return alarms.indexOf(oldAlarm);
            }
        }

        return DEFAULT_ALARM_ID;
    }

}