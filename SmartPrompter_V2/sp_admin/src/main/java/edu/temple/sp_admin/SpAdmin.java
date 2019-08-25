package edu.temple.sp_admin;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;

import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.obj.SurveyQuestion;
import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.StorageUtil;

import static edu.temple.sp_res_lib.utils.Constants.DEFAULT_ALARM_ID;

public class SpAdmin extends Application {

    public static final String LOG_TAG = "SmartPrompter-Admin";

    private ArrayList<Alarm> alarms;
    private ArrayList<SurveyQuestion> questions;

    @Override
    public void onCreate() {
        super.onCreate();
        alarms = StorageUtil.getAlarmsFromStorage(this);
        questions = StorageUtil.getSurveyQuestionsFromStorage();
    }

    public void onAppStopped() {
        StorageUtil.writeAlarmsToStorage(this, alarms);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    // public Alarm getAlarm(int alarmID) {
    //     return alarms.get(alarmID);
    // }

    public Alarm getAlarm(String guid) {
        for (Alarm alarm : alarms) {
            if (alarm.getGuid().equals(guid))
                return alarm;
        }
        return null;
    }

    public ArrayList<Alarm> getCurrentAlarms() {
        ArrayList<Alarm> currentAlarms = new ArrayList<>();
        for (Alarm alarm : alarms) {
            if (!alarm.isArchived())
                currentAlarms.add(alarm);
        }
        return currentAlarms;
    }

    public ArrayList<Alarm> getArchivedAlarms() {
        ArrayList<Alarm> archivedAlarms = new ArrayList<>();
        for (Alarm alarm : alarms) {
            if (alarm.isArchived())
                archivedAlarms.add(alarm);
        }
        return archivedAlarms;
    }

    public ArrayList<SurveyQuestion> getQuestions() {
        return this.questions;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public void saveAlarm(Alarm newAlarm) {
        Log.i(LOG_TAG, "Attempting to save alarm record with: "
                + "\t GUID: " + newAlarm.getGuid()
                + "\t Description: " + newAlarm.getDesc()
                + "\t Date: " + newAlarm.getDateString()
                + "\t Time: " + newAlarm.getTimeString()
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

            // TODO - if ALARM alarm already exists for this record, delete it
        }

        // TODO - set a new ALARM alarm for this record
        // TODO - update any relevant listeners
    }

    public void deleteAlarm(Alarm alarm) {
        Log.i(LOG_TAG, "Attempting to delete alarm record with: "
                + "\t GUID: " + alarm.getGuid()
                + "\t Description: " + alarm.getDesc());

        StorageUtil.deleteFileFromStorage(this, alarm.getGuid());

        int oldAlarmIndex = getAlarmIndex(alarm);
        if (oldAlarmIndex != DEFAULT_ALARM_ID)
            alarms.remove(oldAlarmIndex);

        // TODO - cancel ALARM alarm for this record
        // TODO - update any relevant listeners
    }

    private int getAlarmIndex(Alarm alarm) {
        for (Alarm oldAlarm : alarms) {
            if (oldAlarm.getGuid().equals(alarm.getGuid())) {
                return alarms.indexOf(oldAlarm);
            }
        }

        return DEFAULT_ALARM_ID;
    }

}