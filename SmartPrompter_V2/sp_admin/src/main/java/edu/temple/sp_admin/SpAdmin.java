package edu.temple.sp_admin;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.obj.SurveyQuestion;
import edu.temple.sp_res_lib.utils.Constants;

public class SpAdmin extends Application {

    public static final String LOG_TAG = "SmartPrompter-Admin";

    private ArrayList<Alarm> alarms, logs;
    private ArrayList<SurveyQuestion> questions;

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO - populate current alarms list for real
        alarms = new ArrayList<>();
        alarms.add(new Alarm(1, UUID.randomUUID().toString(),
                "Take out the trash",  0, Alarm.STATUS.Active));
        alarms.add(new Alarm(2, UUID.randomUUID().toString(),
                "Water the plants", 0, Alarm.STATUS.Incomplete));

        // TODO - populate past alarms list for real
        logs = new ArrayList<>();
        logs.add(new Alarm(1, UUID.randomUUID().toString(),
                "Feed the dog", 0, Alarm.STATUS.Complete));

        // TODO - populate survey question list for real
        questions = new ArrayList<>();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public Alarm getAlarm(int alarmID) {
        // TODO - confirm what an alarm "ID" is ... is it the ordinal number of the alarm in
        //  the overall list?  is it the index of the alarm in the collection?  the ACTUAL unique
        //  identifier of the alarm is the GUID...
        return alarms.get(alarmID - 1);
    }

    public ArrayList<Alarm> getCurrentAlarms() {
        return this.alarms;
    }

    public ArrayList<Alarm> getPastAlarms() {
        return this.logs;
    }

    public ArrayList<SurveyQuestion> getQuestions() {
        return this.questions;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public void saveAlarm(Alarm alarm) {
        Log.i(LOG_TAG, "Attempting to save alarm record with: "
                + "\t ID: " + alarm.getID()
                + "\t GUID: " + alarm.getUUID()
                + "\t Description: " + alarm.getDesc()
                + "\t Date: " + alarm.getDateString()
                + "\t Time: " + alarm.getTimeString()
                + "\t Status: " + alarm.getStatus());

        if (alarm.getID() == Constants.DEFAULT_ALARM_ID ||
                alarm.getUUID() == Constants.DEFAULT_ALARM_GUID) {
            Log.i(LOG_TAG, "Creating new record for alarm: " + alarm.getDesc());
            // TODO - calculate new ID for new alarm
            // TODO - generate new GUID for new alarm
            // TODO - insert alarm into collection
        } else {
            Log.i(LOG_TAG, "Updating details for existing alarm: " + alarm.getDesc());
            // TODO - override old alarm record in collection by ID
        }

        // TODO - update any relevant listeners
    }

    public void deleteAlarm(Alarm alarm) {
        Log.i(LOG_TAG, "Attempting to delete alarm record with: "
                + "\t ID: " + alarm.getID()
                + "\t GUID: " + alarm.getUUID()
                + "\t Description: " + alarm.getDesc());

        // TODO - remove alarm from collection
        // TODO - delete alarm from persistent storage
        // TODO - update any relevant listeners
    }

}