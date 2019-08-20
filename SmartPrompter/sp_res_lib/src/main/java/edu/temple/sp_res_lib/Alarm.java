package edu.temple.sp_res_lib;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import edu.temple.sp_res_lib.utils.BaseScheduleable;
import edu.temple.sp_res_lib.utils.Constants;

public class Alarm extends BaseScheduleable {

    private String label;
    private Constants.ALARM_STATUS status;

    protected String timeAcknowledged;
    protected String timeCompleted;
    protected String completionMediaID;

    public Alarm(int ID, String label, String status, int year, int month, int day,
                 int hour, int minute, String action, String namespace, String className,
                 String timeAcknowledged, String timeCompleted, String completionMediaID) {

        this.ID = ID;
        this.label = label;
        this.status = Constants.ALARM_STATUS.valueOf(status);

        updateDate(year, month, day);
        updateTime(hour, minute);

        updateIntentSettings(action, namespace, className);

        this.timeAcknowledged = timeAcknowledged;
        this.timeCompleted = timeCompleted;
        this.completionMediaID = completionMediaID;
    }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    public int getRequestCode() { return Constants.getAlarmRequestCode(ID); }

    public String getLabel() { return label; }

    public void updateLabel(String newLabel) {
        label = newLabel;
    }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    public void updateStatus(Constants.ALARM_STATUS newStatus) {
        status = newStatus;
    }

    public Constants.ALARM_STATUS getStatus() { return status; }

    public String getStatusString() { return status.toString(); }

    public boolean hasStatus(Constants.ALARM_STATUS compStatus) {
        return (status.equals(compStatus));
    }

    public boolean isActive() {
        // an alarm is "inactive" if its status is "new" or "complete"
        // therefore, an "active" alarm is one with a status OTHER THAN these
        return !status.equals(Constants.ALARM_STATUS.Inactive) &&
                !status.equals(Constants.ALARM_STATUS.Complete) &&
                !status.equals(Constants.ALARM_STATUS.TimedOut);
    }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    public String getTimeAcknowledged() { return timeAcknowledged; }

    public void updateTimeAcknowledged() {
        Calendar cal = Calendar.getInstance();
        timeAcknowledged = Constants.DATE_TIME_FORMAT.format(cal.getTime());
    }

    public String getTimeCompleted() { return timeCompleted; }

    public void updateTimeCompleted() {
        Calendar cal = Calendar.getInstance();
        timeCompleted = Constants.DATE_TIME_FORMAT.format(cal.getTime());
    }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    public String getCompletionMediaID() {
        boolean invalidTime = (timeCompleted == null || timeCompleted.isEmpty());
        boolean invalidLabel = (label == null || label.isEmpty());

        if (!status.equals(Constants.ALARM_STATUS.Complete) || invalidTime || invalidLabel)
            return ""; // "headshot.jpg";

        String formattedTime = timeCompleted
                .replace("-", "")
                .replace(":", "")
                .replace(" ", "_");
        String formattedLabel = label
                .replace(" ", "");

        // Formatted image string = time_label.jpg
        return (formattedTime + "_" + formattedLabel + ".jpg");
    }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    public PendingIntent getPendingIntent(Context context) {
        Intent baseIntent = super.getBaseBroadcastIntent();
        return PendingIntent.getBroadcast(context, ID, baseIntent,
                super.PENDING_INTENT_FLAGS);
    }

    public String toString() { return label; }

}