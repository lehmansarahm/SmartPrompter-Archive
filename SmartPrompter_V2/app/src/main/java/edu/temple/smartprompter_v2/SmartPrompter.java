package edu.temple.smartprompter_v2;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.UUID;

import edu.temple.smartprompter_v2.activities.AcknowledgmentActivity;
import edu.temple.smartprompter_v2.receivers.AlarmReceiver;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.utils.Constants;

public class SmartPrompter extends Application {

    public static final String LOG_TAG = "SmartPrompterV2";

    private SharedPreferences prefs;
    private Ringtone currentRingtone;
    private ArrayList<Alarm> alarms;

    @Override
    public void onCreate() {
        super.onCreate();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        alarms = new ArrayList<>();

        // TODO - populate active alarms list for real
        alarms.add(new Alarm(1, UUID.randomUUID().toString(),
                "Water the plants", 0, Alarm.STATUS.Incomplete));
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

    private PendingIntent getIntent(Context context, int id) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Constants.BUNDLE_ARG_ALARM_ID, id);
        return PendingIntent.getBroadcast(context, id /* request code */,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}