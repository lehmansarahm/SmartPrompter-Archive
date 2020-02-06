package edu.temple.smartprompter_v3.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import edu.temple.smartprompter_v3.SmartPrompter;
import edu.temple.smartprompter_v3.activities.AcknowledgmentActivity;
import edu.temple.smartprompter_v3.activities.BaseActivity;
import edu.temple.smartprompter_v3.activities.CompletionActivity;
import edu.temple.smartprompter_v3.receivers.AlarmAlertReceiver;
import edu.temple.smartprompter_v3.res_lib.SpController;
import edu.temple.smartprompter_v3.res_lib.data.Alarm;
import edu.temple.smartprompter_v3.res_lib.data.FirebaseConnector;
import edu.temple.smartprompter_v3.res_lib.utils.Constants;
import edu.temple.smartprompter_v3.res_lib.utils.FbaEventLogger;
import edu.temple.smartprompter_v3.res_lib.utils.MediaUtil;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AlarmNotificationService extends BaseService {

    private FbaEventLogger eventLogger;
    protected MediaUtil.AUDIO_TYPE mAlertType;
    private String mAlarmGUID;
    private Alarm mAlarm;

    @Override
    protected String getNotificationTitle() {
        return "SmartPrompter Alarm Response Service";
    }

    @Override
    protected String getNotificationText() {
        return "An alarm has gone off!  Time to do your task!";
    }

    @Override
    protected void doWork(Intent intent) {
        mAlarmGUID = intent.getStringExtra(Constants.BUNDLE_ARG_ALARM_GUID);

        String alertType = intent.getStringExtra(Constants.BUNDLE_ARG_ALERT_TYPE);
        mAlertType = (alertType == null || alertType.equals(""))
                ? MediaUtil.AUDIO_TYPE.None : MediaUtil.AUDIO_TYPE.valueOf(alertType);

        FirebaseAuth mFbAuth = FirebaseAuth.getInstance();
        String email = (mFbAuth.getCurrentUser() == null
                ? "" : mFbAuth.getCurrentUser().getEmail());
        eventLogger = new FbaEventLogger(this, email);
        eventLogger.broadcastReceived(AlarmAlertReceiver.class, intent.getAction(), mAlarmGUID);

        FirebaseConnector.getAlarmByGuid(mAlarmGUID, result -> {
            mAlarm = (Alarm)result;
            assert(mAlarm != null);
            respondToAlarmClock(this, intent);
        },
                (error) -> Log.e(BaseActivity.LOG_TAG, "Something went wrong while attempting to "
                        + "retrieve alarms by GUID: " + mAlarmGUID, error));
    }

    private void respondToAlarmClock(Context context, Intent intent) {
        Log.i(BaseActivity.LOG_TAG, "ALARM ALERT BROADCAST RECEIVED FOR GUID: " + mAlarmGUID
                + " \t WITH ORIG ALARM TIME: " + mAlarm.getAlarmDateTimeString()
                + " \t AND STATUS: " + mAlarm.getStatus());

        SmartPrompter.playAlertMedia(context, true, mAlertType);

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
            if (mAlarm.getStatus().equals(Alarm.STATUS.Active))
                SpController.markUnacknowledged(mAlarm);

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