package edu.temple.sp_admin;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.obj.SurveyQuestion;
import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.StorageUtil;

import static edu.temple.sp_res_lib.utils.Constants.SHARED_PREFS_FILENAME;
import static edu.temple.sp_res_lib.utils.Constants.SP_KEY_GUIDS;

public class SpAdmin extends Application {

    public static final String LOG_TAG = "SmartPrompter-Admin";

    // private SharedPreferences prefs;
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

    public Alarm getAlarm(int alarmID) {
        return alarms.get(alarmID);
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

    public void saveAlarm(Alarm alarm) {
        Log.i(LOG_TAG, "Attempting to save alarm record with: "
                + "\t ID: " + alarm.getID()
                + "\t GUID: " + alarm.getGuid()
                + "\t Description: " + alarm.getDesc()
                + "\t Date: " + alarm.getDateString()
                + "\t Time: " + alarm.getTimeString()
                + "\t Status: " + alarm.getStatus()
                + "\t Is Archived: " + alarm.isArchived());

        if (alarm.getID() == Constants.DEFAULT_ALARM_ID ||
                alarm.getGuid() == Constants.DEFAULT_ALARM_GUID) {
            Log.i(LOG_TAG, "Creating new record for alarm: " + alarm.getDesc());
            alarm.setID(alarms.size());
            alarm.setNewGuid();
            alarms.add(alarm);
        } else {
            Log.i(LOG_TAG, "Updating details for existing alarm: " + alarm.getDesc());
            alarms.set(alarm.getID(), alarm);
            // TODO - if ALARM alarm already exists for this record, delete it
        }

        // TODO - set a new ALARM alarm for this record
        // TODO - update any relevant listeners
    }

    public void deleteAlarm(Alarm alarm) {
        Log.i(LOG_TAG, "Attempting to delete alarm record with: "
                + "\t ID: " + alarm.getID()
                + "\t GUID: " + alarm.getGuid()
                + "\t Description: " + alarm.getDesc());

        StorageUtil.deleteFileFromStorage(this, alarm.getGuid());
        alarms.remove(alarm.getID());

        // TODO - cancel ALARM alarm for this record
        // TODO - update any relevant listeners
    }

}