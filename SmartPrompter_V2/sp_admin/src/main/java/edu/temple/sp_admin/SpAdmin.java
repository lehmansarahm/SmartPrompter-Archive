package edu.temple.sp_admin;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.obj.SurveyQuestion;
import edu.temple.sp_res_lib.utils.Constants;

public class SpAdmin extends Application {

    public static final String LOG_TAG = "SmartPrompter-Admin";

    private static final String SHARED_PREFS_FILENAME = "SmartPrompter_Prefs";
    private static final String SP_KEY_GUIDS = "Current_Alarm_GUIDs";
    private SharedPreferences preferences;

    private ArrayList<Alarm> alarms;
    private ArrayList<SurveyQuestion> questions;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences(SHARED_PREFS_FILENAME, 0);
        getAlarmsFromStorage();
        getSurveyQuestionsFromStorage();
    }

    public void onAppStopped() {
        writeAlarmsToStorage();
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
        alarms.remove(alarm.getID());

        // TODO - cancel ALARM alarm for this record
        // TODO - update any relevant listeners
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void getAlarmsFromStorage() {
        Log.i(LOG_TAG, "Retrieving alarm records from storage!");
        alarms = new ArrayList<>();

        Set<String> guidList = preferences.getStringSet(SP_KEY_GUIDS, new HashSet<String>());
        int alarmCount = 0;

        for (String guid : guidList) {
            String jsonAlarm = preferences.getString(guid, null);
            Alarm alarm = Alarm.importFromJson(jsonAlarm);
            alarm.setID(alarmCount);
            alarms.add(alarm);
            alarmCount++;
        }
    }

    private void writeAlarmsToStorage() {
        SharedPreferences.Editor spEditor = preferences.edit();
        Set<String> guidList = new HashSet<>();

        for (Alarm alarm : alarms) {
            guidList.add(alarm.getGuid());
            String jsonAlarm = Alarm.exportToJson(alarm);
            Log.i(LOG_TAG, "Writing alarm to storage: " + jsonAlarm);
            spEditor.putString(alarm.getGuid(), jsonAlarm);
        }

        spEditor.putStringSet(SP_KEY_GUIDS, guidList);
        spEditor.apply();
    }

    private void getSurveyQuestionsFromStorage() {
        Log.i(LOG_TAG, "Retrieving survey questions from storage!");

        // TODO - populate survey question list for real
        questions = new ArrayList<>();
    }

}