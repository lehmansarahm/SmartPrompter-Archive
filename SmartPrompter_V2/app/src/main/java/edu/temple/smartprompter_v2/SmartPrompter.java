package edu.temple.smartprompter_v2;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.temple.smartprompter_v2.receivers.AlarmReceiver;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.MediaUtil;
import edu.temple.sp_res_lib.utils.StorageUtil;

public class SmartPrompter extends Application {

    public static final String LOG_TAG = "SmartPrompterV2";

    private static final List<Alarm.STATUS> ACTIVE_STATUSES =
            Arrays.asList(Alarm.STATUS.Unacknowledged, Alarm.STATUS.Incomplete);

    private ArrayList<Alarm> alarms;

    @Override
    public void onCreate() {
        super.onCreate();
        getAlarmsFromStorage();
    }

    public void onAppStopped() {
        StorageUtil.writeAlarmsToStorage(this, alarms);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public Alarm getAlarm(String guid) {
        for (Alarm alarm : alarms) {
            if (alarm.getGuid().equals(guid))
                return alarm;
        }
        return null;
    }

    public ArrayList<Alarm> getAlarms() {
        return getAlarms(false);
    }

    public ArrayList<Alarm> getAlarms(boolean refreshFromStorage) {
        if (refreshFromStorage) getAlarmsFromStorage();
        return this.alarms;
    }

    public void updateAlarmStatus(String alarmGUID, Alarm.STATUS newStatus) {
        for (Alarm alarm : alarms) {
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

    /* public void setAlarm(Context context, AlarmManager manager, Alarm alarm) {
        manager.setAlarmClock(
                new AlarmManager.AlarmClockInfo(
                        alarm.getAlarmTimeMillis(),
                        PendingIntent.getActivity(context, 0,
                                new Intent(context, AcknowledgmentActivity.class), 0)
                ),
                getIntent(context, alarm.getID())
        );
    }

    public void cancelAlarm(Context context, AlarmManager manager, Alarm alarm) {
        manager.cancel(getIntent(context, alarm.getID()));
    } */

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void getAlarmsFromStorage() {
        Log.i(LOG_TAG, "Retrieving alarm records from storage!");
        alarms = new ArrayList<>();

        ArrayList<Alarm> allAlarms = StorageUtil.getAlarmsFromStorage(this);
        for (Alarm alarm : allAlarms) {
            if (ACTIVE_STATUSES.contains(alarm.getStatus())) {
                alarms.add(alarm);
            }
        }
    }

    private PendingIntent getIntent(Context context, int id) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Constants.BUNDLE_ARG_ALARM_GUID, id);
        return PendingIntent.getBroadcast(context, id /* request code */,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}