package edu.temple.smartprompter.alarms;

import java.util.List;

import edu.temple.smartprompter.alarms.Alarm;

public class AlarmMaster {

    public static List<Alarm> mAlarmDataset = Alarm.getDefaults();

    public static int mCurrentAlarmIndex = -1;

    public static int mCurrentAlarmRequestCode = 1;

    public static int getNewRequestCode() {
        return (mCurrentAlarmRequestCode++);
    }

}