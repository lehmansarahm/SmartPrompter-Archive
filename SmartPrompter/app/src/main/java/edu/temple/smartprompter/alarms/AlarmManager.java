package edu.temple.smartprompter.alarms;

import java.util.List;

import edu.temple.smartprompter.alarms.Alarm;

public class AlarmManager {

    public static int mCurrentAlarmIndex = -1;

    public static List<Alarm> mAlarmDataset = Alarm.getDefaults();

}