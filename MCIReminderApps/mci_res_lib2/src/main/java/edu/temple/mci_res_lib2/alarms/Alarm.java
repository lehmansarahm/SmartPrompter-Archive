package edu.temple.mci_res_lib2.alarms;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import edu.temple.mci_res_lib2.utils.Constants;

public class Alarm {

    private static final String TITLE_FORMAT_STRING = "Alarm #%s";
    private static final String LIST_ITEM_FORMAT_STRING = "%d:%02d %s (%s)";

    private int id;
    private String position;
    private int hour;
    private int minute;
    private boolean am;
    private Calendar originalAlarm;

    private int ackReminders;
    private Calendar timeAcknowledged;

    private int compReminders;
    private Calendar timeCompleted;
    private String compPhotoName;

    public enum STATUS { Active, Unacknowledged, Incomplete, Complete, Inactive }
    private STATUS status;

    private boolean writtenToFile = false;

    public Alarm(int position, int hour, int minute, boolean am, STATUS status) {
        this.id = (position - 1);
        this.position = String.valueOf(position);
        this.hour = hour;
        this.minute = minute;
        this.am = am;
        this.status = status;
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        this.position = String.valueOf(id + 1);
    }

    public String getPosition() {
        return position;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public boolean isAm() {
        return am;
    }

    public Calendar getOriginalAlarm() {
        if (originalAlarm != null) return originalAlarm;

        originalAlarm = Calendar.getInstance();
        originalAlarm.set(Calendar.MINUTE, this.minute);
        originalAlarm.set(Calendar.SECOND, 0);
        originalAlarm.set(Calendar.MILLISECOND, 0);
        originalAlarm.set(Calendar.AM_PM, this.am ? 0 : 1);

        // The Java "Calendar" object is finicky (especially with the 12 AM / PM hours)
        // This little hacky block seems to handle it ...
        if (this.am) {
            if (this.hour == 12) originalAlarm.set(Calendar.HOUR_OF_DAY, 0);
            else originalAlarm.set(Calendar.HOUR_OF_DAY, this.hour);
        } else {
            if (this.hour == 12) originalAlarm.set(Calendar.HOUR_OF_DAY, 12);
            else originalAlarm.set(Calendar.HOUR_OF_DAY, (this.hour + 12));
        }

        // if desired alarm time has already passed, bump to next day
        long calDiff = (Calendar.getInstance().getTimeInMillis() - originalAlarm.getTimeInMillis());
        if (calDiff > 0) originalAlarm.add(Calendar.HOUR, 24);
        return originalAlarm;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
        if (status.equals(STATUS.Incomplete)) // meaning that it has been acknowledged
            timeAcknowledged = Calendar.getInstance();
        else if (status.equals(STATUS.Complete))
            timeCompleted = Calendar.getInstance();
    }

    public void setCompPhotoName(String photoPath) {
        compPhotoName = photoPath.substring(photoPath.lastIndexOf("/") + 1);
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        String statusString;
        switch (status) {
            case Unacknowledged:
                if (writtenToFile) statusString = "Ack. Reminder Limit Reached";
                else statusString = status.toString();
                break;
            case Incomplete:
                if (writtenToFile) {
                    statusString = "Comp. Reminder Limit Reached";
                    break;
                }
            default:
                statusString = status.toString();
                break;
        }
        return String.format(LIST_ITEM_FORMAT_STRING, hour, minute, (am ? "AM" : "PM"), statusString);
    }

    public boolean hasAcknowledgementRemindersRemaining() {
        Log.i(Constants.LOG_TAG, "Verifying current acknowledgement reminder count: "
                + ackReminders + ", against limit: " + Constants.ACKNOWLEDGE_REMINDER_LIMIT);
        boolean remindersRemaining = (ackReminders <= Constants.ACKNOWLEDGE_REMINDER_LIMIT);

        // little hacky check to make sure that the final value displays correctly in output logs
        if (!remindersRemaining) ackReminders--;
        return remindersRemaining;
    }

    public void cancelAckReminder() {
        ackReminders--;
    }

    public Calendar getNextAcknowledgementCalendar() {
        ackReminders++;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, Constants.ACKNOWLEDGE_REMINDER_INTERVAL);
        return cal;
    }

    public boolean hasCompletionRemindersRemaining() {
        Log.i(Constants.LOG_TAG, "Verifying current completion reminder count: "
                + compReminders + ", against limit: " + Constants.COMPLETION_REMINDER_LIMIT);
        boolean remindersRemaining = (compReminders <= Constants.COMPLETION_REMINDER_LIMIT);

        // little hacky check to make sure that the final value displays correctly in output logs
        if (!remindersRemaining) compReminders--;
        return remindersRemaining;
    }

    public void cancelCompReminder() {
        compReminders--;
    }

    public Calendar getNextCompletionCalendar() {
        compReminders++;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, Constants.COMPLETION_REMINDER_INTERVAL);
        return cal;
    }

    public String getTitle() {
        return String.format(TITLE_FORMAT_STRING, position);
    }

    public void writeToFile(String alarmType) {
        // getting all our properties together ...
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd_hh.mm.ss");
        String filename = String.format("%sAlarm%s_%s.txt", alarmType, position, formatter.format(Calendar.getInstance().getTime()));
        String json = (new Gson()).toJson(this);

        File docsFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "Documents");
        if (!docsFolder.exists() && !docsFolder.mkdir()) {
            // if Docs dir does not exist and the attempt to create a new version is unsuccessful ...
            Log.e(Constants.LOG_TAG, "Unable to access Documents directory to export output file.");
        } else {
            try {
                File file = new File(docsFolder.getAbsolutePath(),filename);
                if (!file.exists() && !file.createNewFile()) {
                    // if file does not exist and the attempt to create a new version fails ...
                    throw new Exception("File creation failed!");
                }

                Log.i(Constants.LOG_TAG, "Saving alarm log file to: " + file.getAbsolutePath());
                FileWriter writer = new FileWriter(file.getAbsolutePath(), false);
                writer.write(json);
                writer.close();

                writtenToFile = true;
            } catch (Exception e) {
                Log.e(Constants.LOG_TAG, "Unable to export output file!", e);
            }
        }
    }

    public boolean wasWrittenToFile() { return writtenToFile; }

}