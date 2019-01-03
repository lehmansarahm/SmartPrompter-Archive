package edu.temple.smartprompter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.SpAlarmManager;

public class AlarmReceiver extends BroadcastReceiver {

    private SpAlarmManager mAlarmMgr;
    private Alarm mAlarm;
    private int alarmID;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(Alarm.INTENT_EXTRA_ALARM_ID) &&
                intent.hasExtra(Alarm.INTENT_EXTRA_ORIG_TIME)) {
            alarmID = intent.getIntExtra(Alarm.INTENT_EXTRA_ALARM_ID, -1);
            String timeString = intent.getStringExtra(Alarm.INTENT_EXTRA_ORIG_TIME);
            Log.e(Constants.LOG_TAG, "Alarm broadcast has been received "
                    + "for alarmID: " + alarmID + " at original time: " + timeString);
        } else {
            Log.e(Constants.LOG_TAG, "Alarm broadcast has been received, "
                    + "but is missing parameters.");
            return;
        }

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();

        mAlarmMgr = new SpAlarmManager(context);
        mAlarm = mAlarmMgr.get(alarmID);
        mAlarm.updateStatus(Alarm.STATUS.Complete);
        mAlarmMgr.update(mAlarm);

        // just for sanity's sake ...
        mAlarm = mAlarmMgr.get(alarmID);
        Log.i(Constants.LOG_TAG, "Received and acknowledged alarm broadcast for alarm ID: "
                + alarmID + ".  \t\t Current alarm status: " + mAlarm.getStatus());
    }

}