package edu.temple.smartprompter_v2;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import edu.temple.smartprompter_v2.activities.MainActivity;
import edu.temple.smartprompter_v2.receivers.AlarmAlertReceiver;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.MediaUtil;
import edu.temple.sp_res_lib.utils.StorageUtil;

public class SmartPrompter extends Application {

    public static final String LOG_TAG = "SmartPrompterV2";

    private static final List<Alarm.STATUS> FUTURE_STATUSES =
            Arrays.asList(Alarm.STATUS.Active);

    private static final List<Alarm.STATUS> ACTIVE_STATUSES =
            Arrays.asList(Alarm.STATUS.Unacknowledged, Alarm.STATUS.Incomplete);

    private ArrayList<Alarm> futureAlarms, outstandingAlarms;

    @Override
    public void onCreate() {
        super.onCreate();
        getAlarmsFromStorage();
        setFutureAlarms();
    }

    public void onAppStopped() {
        StorageUtil.writeAlarmsToStorage(this, outstandingAlarms);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public Alarm getAlarm(String guid) {
        for (Alarm alarm : outstandingAlarms) {
            if (alarm.getGuid().equals(guid))
                return alarm;
        }
        return null;
    }

    public ArrayList<Alarm> getAlarms(boolean refreshFromStorage) {
        if (refreshFromStorage) getAlarmsFromStorage();
        return this.outstandingAlarms;
    }

    public void updateAlarmStatus(String alarmGUID, Alarm.STATUS newStatus) {
        for (Alarm alarm : outstandingAlarms) {
            if (alarm.getGuid().equals(alarmGUID))
                alarm.updateStatus(newStatus);
        }
    }

    public void saveTaskImage(String filename, byte[] bytes) {
        Log.i(LOG_TAG, "Attempting to save file: " + filename);
        Bitmap media = MediaUtil.convertToBitmap(bytes);
        StorageUtil.writeImageToFile(this, filename, media);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public Alarm getAlarmForAlert(String guid) {
        if (futureAlarms == null || futureAlarms.size() == 0)
            getAlarmsFromStorage();

        for (Alarm alarm : futureAlarms) {
            if (alarm.getGuid().equals(guid)) {
                if (alarm.getStatus().equals(Alarm.STATUS.Active)) {
                    alarm.updateStatus(Alarm.STATUS.Unacknowledged);
                    futureAlarms.remove(alarm);
                    outstandingAlarms.add(alarm);
                }
                return alarm;
            }
        }

        return null;
    }

    public void setAlarmReminder(Alarm alarm, Alarm.REMINDER type) {

        // TODO - figure out why alarm reminders aren't playing (the app is waking up, but the
        //  alarm alert receiver isn't firing...)

        cancelAlarm(alarm);
        alarm.setReminder(type);
        setAlarm(getApplicationContext(), alarm, true);
    }

    public void cancelAlarm(Alarm alarm) {
        Context context = getApplicationContext();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager != null) {
            Log.i(LOG_TAG, "Cancelling existing alarms for GUID-int: " + alarm.getGuidInt());
            manager.cancel(getAlarmResponseIntent(context, alarm));
        }
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void getAlarmsFromStorage() {
        Log.i(LOG_TAG, "Retrieving alarm records from storage!");
        futureAlarms = new ArrayList<>();
        outstandingAlarms = new ArrayList<>();

        ArrayList<Alarm> allAlarms = StorageUtil.getAlarmsFromStorage(this);
        for (Alarm alarm : allAlarms) {
            if (FUTURE_STATUSES.contains(alarm.getStatus()))
                futureAlarms.add(alarm);
            else if (ACTIVE_STATUSES.contains(alarm.getStatus()))
                outstandingAlarms.add(alarm);
        }
    }

    private void setFutureAlarms() {
        Calendar now = Calendar.getInstance();
        Log.i(LOG_TAG, "Current time: " + Constants.DATE_TIME_FORMAT.format(now.getTime()));

        for (Alarm alarm : futureAlarms) {
            Log.i(LOG_TAG, "Current time millis: " + now.getTimeInMillis());
            Log.i(LOG_TAG, "Alarm time millis: " + alarm.getAlarmTimeMillis());

            if (alarm.getAlarmTimeMillis() < Calendar.getInstance().getTimeInMillis())
                Log.e(LOG_TAG, "CAN'T SET ALARM FOR TIME IN THE PAST.");
            else
                setAlarm(getApplicationContext(), alarm);
        }
    }

    private void setAlarm(Context context, Alarm alarm) {
        setAlarm(context, alarm, false);
    }

    private void setAlarm(Context context, Alarm alarm, boolean isReminder) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long alarmTime = (isReminder ? alarm.getReminderTimeMillis() : alarm.getAlarmTimeMillis());
        String alarmString = (isReminder ? alarm.getReminderDateTimeString() : alarm.getAlarmDateTimeString());

        if (manager != null) {
            Log.e(LOG_TAG, "Setting alarm for task: " + alarm.getDesc()
                    + " \t \t and GUID-int: " + alarm.getGuidInt()
                    + " \t \t with date/time: " + alarmString);
            AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(alarmTime,
                    getAlarmResponseIntent(context, alarm));
            manager.setAlarmClock(info, getAlarmResponseIntent(context, alarm));
        }
    }

    private PendingIntent getNotificationResponseIntent(Context context, Alarm alarm) {
        Intent intent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, alarm.getGuidInt() /* request code */,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getAlarmResponseIntent(Context context, Alarm alarm) {
        Intent intent = new Intent(context, AlarmAlertReceiver.class);
        intent.putExtra(Constants.BUNDLE_ARG_ALARM_GUID, alarm.getGuid());
        return PendingIntent.getBroadcast(context, alarm.getGuidInt() /* request code */,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}