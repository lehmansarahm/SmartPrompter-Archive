package edu.temple.smartprompter_v3.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import edu.temple.smartprompter_v3.R;
import edu.temple.smartprompter_v3.activities.AcknowledgmentActivity;
import edu.temple.smartprompter_v3.activities.BaseActivity;
import edu.temple.smartprompter_v3.activities.CompletionActivity;
import edu.temple.smartprompter_v3.activities.LoginActivity;
import edu.temple.smartprompter_v3.res_lib.SpController;
import edu.temple.smartprompter_v3.res_lib.data.Alarm;
import edu.temple.smartprompter_v3.res_lib.data.FirebaseConnector;
import edu.temple.smartprompter_v3.res_lib.utils.Constants;
import edu.temple.smartprompter_v3.res_lib.utils.FbaEventLogger;
import edu.temple.smartprompter_v3.res_lib.utils.MediaUtil;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AlarmAlertReceiver extends BroadcastReceiver {

    private FbaEventLogger eventLogger;
    private boolean isAdminAppEvent;
    private String mAlarmGUID;
    private Alarm mAlarm;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        mAlarmGUID = intent.getStringExtra(Constants.BUNDLE_ARG_ALARM_GUID);
        isAdminAppEvent = intent.getAction().equals(context.getString(R.string.event_alarms_ready));

        eventLogger = new FbaEventLogger(context);
        eventLogger.broadcastReceived(AlarmAlertReceiver.class, intent.getAction(), mAlarmGUID);

        FirebaseConnector.getAlarmByGuid(mAlarmGUID, result -> {
            mAlarm = (Alarm)result;
            assert(mAlarm != null);

            if (isAdminAppEvent) respondToAdminAppEvent(context);
            else respondToAlarmClock(context, intent);
        });
    }

    private void respondToAdminAppEvent(Context context) {
        Log.i(BaseActivity.LOG_TAG, "ALARM ALERT RECEIVED!");
        SpController.setAlarm(context, mAlarm,
                BaseActivity.ALARM_NOTIFICATION_CLASS, BaseActivity.ALARM_RECEIVER_CLASS);
    }

    private void respondToAlarmClock(Context context, Intent intent) {
        Log.i(BaseActivity.LOG_TAG, "ALARM ALERT BROADCAST RECEIVED FOR GUID: " + mAlarmGUID
                + " \t WITH ORIG ALARM TIME: " + mAlarm.getAlarmDateTimeString()
                + " \t AND STATUS: " + mAlarm.getStatus());

        // setAlarm implicit reminder
        SpController.setReminder(context, mAlarm,
                BaseActivity.ALARM_NOTIFICATION_CLASS,
                BaseActivity.ALARM_RECEIVER_CLASS,
                Alarm.REMINDER.Implicit,
                true);

        // select the appropriate response activity ...
        Intent newIntent;
        if (mAlarm.getStatus().equals(Alarm.STATUS.Incomplete)) {
            Log.i(BaseActivity.LOG_TAG, "Launching completion activity for alarm: " + mAlarmGUID);
            newIntent = new Intent(context, CompletionActivity.class);
        } else {
            Log.i(BaseActivity.LOG_TAG, "Launching acknowledgment activity for alarm: " + mAlarmGUID);
            newIntent = new Intent(context, AcknowledgmentActivity.class);
        }

        // start up the response activity ...
        newIntent.putExtra(Constants.BUNDLE_ARG_ALARM_GUID, mAlarmGUID);
        newIntent.putExtra(Constants.BUNDLE_ARG_ALARM_WAKEUP, true);
        newIntent.putExtra(Constants.BUNDLE_ARG_PLAY_ALERTS, true);

        String alertType = intent.getStringExtra(Constants.BUNDLE_ARG_ALERT_TYPE);
        if (alertType == null || alertType.equals(""))
            alertType = MediaUtil.AUDIO_TYPE.None.toString();
        newIntent.putExtra(Constants.BUNDLE_ARG_ALERT_TYPE, alertType);

        newIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);
    }

}