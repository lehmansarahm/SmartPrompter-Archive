package edu.temple.sp_admin;

import android.app.Application;

import java.util.ArrayList;
import java.util.UUID;

import edu.temple.sp_res_lib.obj.Alarm;

public class SpAdmin extends Application {

    public static final String LOG_TAG = "SmartPrompter-Admin";

    private ArrayList<Alarm> currentAlarms, pastAlarms;

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO - populate current, past alarms list for real
        currentAlarms = new ArrayList<>();
        currentAlarms.add(new Alarm(1, UUID.randomUUID().toString(),
                "Take out the trash",  0, Alarm.STATUS.Active));
        currentAlarms.add(new Alarm(2, UUID.randomUUID().toString(),
                "Water the plants", 0, Alarm.STATUS.Incomplete));

        pastAlarms = new ArrayList<>();
        pastAlarms.add(new Alarm(1, UUID.randomUUID().toString(),
                "Feed the dog", 0, Alarm.STATUS.Complete));
    }

    public ArrayList<Alarm> getCurrentAlarms() {
        return this.currentAlarms;
    }

    public ArrayList<Alarm> getPastAlarms() {
        return this.pastAlarms;
    }

}