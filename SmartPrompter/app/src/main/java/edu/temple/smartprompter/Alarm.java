package edu.temple.smartprompter;

import java.util.ArrayList;
import java.util.List;

public class Alarm {

    public enum STATUS { New, Active, Unacknowledged, Incomplete, Complete }

    private String date, time, label;
    private STATUS status;

    public Alarm(String d, String t, String l) {
        date = d;
        time = t;
        label = l;
        status = STATUS.New;
    }

    public String getDate() { return date; }

    public String getTime() { return time; }

    public String getLabel() { return label; }

    public String getStatus() { return status.toString(); }

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