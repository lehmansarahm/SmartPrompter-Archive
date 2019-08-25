package edu.temple.smartprompter_v2;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.temple.smartprompter_v2.activities.AcknowledgmentActivity;
import edu.temple.smartprompter_v2.receivers.AlarmReceiver;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.StorageUtil;

import static edu.temple.sp_res_lib.utils.Constants.SHARED_PREFS_FILENAME;
import static edu.temple.sp_res_lib.utils.Constants.SP_KEY_GUIDS;

public class SmartPrompter extends Application {

    public static final String LOG_TAG = "SmartPrompterV2";

    private static final List<Alarm.STATUS> ACTIVE_STATUSES =
            Arrays.asList(Alarm.STATUS.New, Alarm.STATUS.Unacknowledged, Alarm.STATUS.Incomplete);

    private ArrayList<Alarm> alarms;
    private Ringtone currentRingtone;

    @Override
    public void onCreate() {
        super.onCreate();
        getAlarmsFromStorage();
    }

    public ArrayList<Alarm> getAlarms() {
        return this.alarms;
    }

    public void setAlarm(Context context, AlarmManager manager, Alarm alarm) {
        manager.setAlarmClock(
                new AlarmManager.AlarmClockInfo(
                        alarm.getTimeInMillis(),
                        PendingIntent.getActivity(context, 0,
                                new Intent(context, AcknowledgmentActivity.class), 0)
                ),
                getIntent(context, alarm.getID())
        );
    }

    public void cancelAlarm(Context context, AlarmManager manager, Alarm alarm) {
        manager.cancel(getIntent(context, alarm.getID()));
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void getAlarmsFromStorage() {
        Log.i(LOG_TAG, "Retrieving alarm records from storage!");
        alarms = new ArrayList<>();
        int alarmCount = 0;

        ArrayList<Alarm> allAlarms = StorageUtil.getAlarmsFromStorage(this);
        for (Alarm alarm : allAlarms) {
            if (ACTIVE_STATUSES.contains(alarm.getStatus())) {
                alarm.setID(alarmCount);
                alarms.add(alarm);
                alarmCount++;
            }
        }
    }

    private PendingIntent getIntent(Context context, int id) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Constants.BUNDLE_ARG_ALARM_ID, id);
        return PendingIntent.getBroadcast(context, id /* request code */,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}