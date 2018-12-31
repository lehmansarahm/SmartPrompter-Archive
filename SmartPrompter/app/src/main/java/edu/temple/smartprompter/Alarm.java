package edu.temple.smartprompter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Alarm {

    public enum STATUS { New, Active, Unacknowledged, Incomplete, Complete }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

    private Calendar cal;
    private String label;
    private STATUS status;

    public Alarm(int h, int mi, int y, int mo, int d, String l) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, h);
        c.set(Calendar.MINUTE, mi);
        c.set(Calendar.YEAR, y);
        c.set(Calendar.MONTH, mo);
        c.set(Calendar.DAY_OF_MONTH, d);

        cal = c;
        label = l;
        status = STATUS.New;
    }

    public void updateDate(int y, int m, int d) {
        cal.set(Calendar.YEAR, y);
        cal.set(Calendar.MONTH, m);
        cal.set(Calendar.DAY_OF_MONTH, d);
    }

    public int[] getDate() {
        return new int[] {
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        };
    }

    public String getDateString() { return dateFormat.format(cal.getTime()); }

    public void updateTime(int h, int m) {
        cal.set(Calendar.HOUR_OF_DAY, h);
        cal.set(Calendar.MINUTE, m);
    }

    public int[] getTime() {
        return new int[] {
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.AM_PM)
        };
    }

    public String getTimeString() { return timeFormat.format(cal.getTime()); }

    public String getLabel() { return label; }

    public String getStatus() { return status.toString(); }

    public String toString() { return label; }

    public static Alarm getNewAlarm() {
        return new Alarm(12, 0, 2019, Calendar.JANUARY, 1,"New Alarm");
    }

    public static List<Alarm> getDefaults() {
        List<Alarm> alarmList = new ArrayList<>();
        alarmList.add(new Alarm(13, 0, 2019, Calendar.JANUARY, 1,"Default Alarm 1"));
        alarmList.add(new Alarm(14, 0, 2019, Calendar.FEBRUARY, 2,"Default Alarm 2"));
        alarmList.add(new Alarm(15, 0, 2019, Calendar.MARCH, 3,"Default Alarm 3"));
        alarmList.add(new Alarm(16, 0, 2019, Calendar.APRIL, 4,"Default Alarm 4"));
        return alarmList;
    }

}