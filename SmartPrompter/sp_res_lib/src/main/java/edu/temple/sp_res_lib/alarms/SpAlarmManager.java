package edu.temple.sp_res_lib.alarms;

import java.util.List;

public class SpAlarmManager {

    /*

        THIS WILL EVENTUALLY BE REPLACED BY SOME KIND OF PERMANENT STORAGE

     */

    public static List<Alarm> mAlarmDataset = Alarm.getDefaults();

    public static int mCurrentAlarmIndex = -1;

    public static int mCurrentAlarmRequestCode = 1;

    public static int getNewRequestCode() {
        return (mCurrentAlarmRequestCode++);
    }

}