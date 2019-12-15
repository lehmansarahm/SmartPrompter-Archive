package edu.temple.sp_admin;

import android.app.Application;
import android.util.DisplayMetrics;

import java.util.ArrayList;

import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.obj.SurveyQuestion;
import edu.temple.sp_res_lib.utils.AlarmUtil;
import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.Log;
import edu.temple.sp_res_lib.utils.StorageUtil;

import static edu.temple.sp_res_lib.utils.Constants.DEFAULT_ALARM_ID;

public class SpAdmin extends Application {

    public static final String LOG_TAG = "SmartPrompter-Admin";

    private ArrayList<Alarm> activeAlarms, inactiveAlarms;
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

        getActiveAlarms();
        getInactiveAlarms();
        getQuestions();

        super.onCreate();
    }

    public Alarm getAlarm(String guid) {
        for (Alarm alarm : activeAlarms) {
            if (alarm.getGuid().equals(guid))
                return alarm;
        }
        return null;
    }

    public Alarm getAlarmLog(String guid) {
        for (Alarm alarm : inactiveAlarms) {
            if (alarm.getGuid().equals(guid))
                return alarm;
        }
        return null;
    }

    public ArrayList<Alarm> getActiveAlarms() {
        activeAlarms = AlarmUtil.getAlarmsFromStorage(this);
        return activeAlarms;
    }

    public ArrayList<Alarm> getInactiveAlarms() {
        inactiveAlarms = StorageUtil.getInactiveAlarmsFromStorage(this);
        return inactiveAlarms;
    }

    public ArrayList<Alarm> getArchivedAlarms() {
        return StorageUtil.getArchivedLogsFromStorage(this);
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
            activeAlarms.add(newAlarm);
        } else {
            Log.i(LOG_TAG, "Updating details for existing alarm: " + newAlarm.getDesc());
            int oldAlarmIndex = getAlarmIndex(newAlarm);
            if (oldAlarmIndex != DEFAULT_ALARM_ID)
                activeAlarms.set(oldAlarmIndex, newAlarm);
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
            activeAlarms.remove(oldAlarmIndex);
    }

    public void commitChanges() {
        StorageUtil.writeAlarmsToStorage(this, activeAlarms);
        StorageUtil.writeDirtyFlag(this);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private int getAlarmIndex(Alarm alarm) {
        for (Alarm oldAlarm : activeAlarms) {
            if (oldAlarm.getGuid().equals(alarm.getGuid())) {
                return activeAlarms.indexOf(oldAlarm);
            }
        }

        return DEFAULT_ALARM_ID;
    }

}