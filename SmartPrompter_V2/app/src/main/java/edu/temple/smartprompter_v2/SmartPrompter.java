package edu.temple.smartprompter_v2;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
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
        // TODO - this loading logic is firing multiple times on application start up ...
        //  debug eventually ...
        super.onCreate();
        getAlarmsFromStorage();
        setNewAlarms();
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
        // TODO - figure out a better way to do this because this is gross ...
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

        for (Alarm alarm : outstandingAlarms) {
            if (alarm.getGuid().equals(guid))
                return alarm;
        }

        return null;
    }

    public void setAlarmReminder(Alarm alarm, Alarm.REMINDER type) {
        alarm.setReminder(type);
        setAlarm(alarm, true);
    }

    public void cancelAlarm(Alarm alarm) {
        Context context = getApplicationContext();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager != null) {
            Log.i(LOG_TAG, "Cancelling existing alarms for GUID-int: " + alarm.getGuidInt());
            manager.cancel(alarm.getPI(context, AlarmAlertReceiver.class));
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

    private void setNewAlarms() {
        Calendar now = Calendar.getInstance();
        Log.i(LOG_TAG, "Current time: " + Constants.DATE_TIME_FORMAT.format(now.getTime()));

        for (Alarm alarm : futureAlarms) {
            Log.i(LOG_TAG, "Current time millis: " + now.getTimeInMillis());
            Log.i(LOG_TAG, "Alarm time millis: " + alarm.getAlarmTimeMillis());

            if (alarm.getAlarmTimeMillis() < Calendar.getInstance().getTimeInMillis())
                Log.e(LOG_TAG, "CAN'T SET ALARM FOR TIME IN THE PAST.");
            else setAlarm(alarm, false);
        }

        // TODO - check "outstanding alarms" and set reminders where necessary
    }

    private void setAlarm(Alarm alarm, boolean isReminder) {
        Context context = getApplicationContext();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        long alarmTime = (isReminder ? alarm.getReminderTimeMillis() : alarm.getAlarmTimeMillis());
        String alarmString = (isReminder ? alarm.getReminderDateTimeString() : alarm.getAlarmDateTimeString());

        if (manager != null) {
            Log.e(LOG_TAG, "Setting alarm for task: " + alarm.getDesc()
                    + " \t \t and GUID-int: " + alarm.getGuidInt()
                    + " \t \t with date/time: " + alarmString);
            PendingIntent notificationPI = alarm.getPI(context, MainActivity.class);
            PendingIntent receiverPI = alarm.getPI(context, AlarmAlertReceiver.class);
            manager.setAlarmClock(new AlarmManager.AlarmClockInfo(alarmTime, notificationPI), receiverPI);
        }
    }

}