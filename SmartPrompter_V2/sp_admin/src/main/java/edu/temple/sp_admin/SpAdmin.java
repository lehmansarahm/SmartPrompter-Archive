package edu.temple.sp_admin;

import android.app.Application;

import java.util.ArrayList;
import java.util.UUID;

import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.obj.SurveyQuestion;

public class SpAdmin extends Application {

    public static final String LOG_TAG = "SmartPrompter-Admin";

    private ArrayList<Alarm> currentAlarms, pastAlarms;
    private ArrayList<SurveyQuestion> questions;

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO - populate current alarms list for real
        currentAlarms = new ArrayList<>();
        currentAlarms.add(new Alarm(1, UUID.randomUUID().toString(),
                "Take out the trash",  0, Alarm.STATUS.Active));
        currentAlarms.add(new Alarm(2, UUID.randomUUID().toString(),
                "Water the plants", 0, Alarm.STATUS.Incomplete));

        // TODO - populate past alarms list for real
        pastAlarms = new ArrayList<>();
        pastAlarms.add(new Alarm(1, UUID.randomUUID().toString(),
                "Feed the dog", 0, Alarm.STATUS.Complete));

        // TODO - populate survey question list for real
        questions = new ArrayList<>();
    }

    public ArrayList<Alarm> getCurrentAlarms() {
        return this.currentAlarms;
    }

    public ArrayList<Alarm> getPastAlarms() {
        return this.pastAlarms;
    }

    public ArrayList<SurveyQuestion> getQuestions() {
        return this.questions;
    }

}