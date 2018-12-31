package edu.temple.smartprompter;

import java.util.ArrayList;
import java.util.List;

public class Alarm {

    private String date, time, label, status;

    public Alarm(String d, String t, String l) {
        date = d;
        time = t;
        label = l;
        status = "New";
    }

    public String getDate() { return date; }

    public String getTime() { return time; }

    public String getLabel() { return label; }

    public String getStatus() { return status; }

    public String toString() {
        return label;
    }

    public static List<Alarm> getDefaults() {
        List<Alarm> alarmList = new ArrayList<>();
        alarmList.add(new Alarm("01/01/19", "12:00 PM", "Default Alarm 1"));
        alarmList.add(new Alarm("01/01/19", "1:00 PM", "Default Alarm 2"));
        alarmList.add(new Alarm("01/01/19", "2:00 PM", "Default Alarm 3"));
        alarmList.add(new Alarm("01/01/19", "3:00 PM", "Default Alarm 4"));
        return alarmList;
    }

}