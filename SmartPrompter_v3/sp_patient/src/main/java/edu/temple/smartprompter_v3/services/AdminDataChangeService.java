package edu.temple.smartprompter_v3.services;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestoreException;

import edu.temple.smartprompter_v3.R;
import edu.temple.smartprompter_v3.activities.BaseActivity;
import edu.temple.smartprompter_v3.receivers.AdminAlertReceiver;
import edu.temple.smartprompter_v3.res_lib.SpController;
import edu.temple.smartprompter_v3.res_lib.data.Alarm;
import edu.temple.smartprompter_v3.res_lib.data.FirebaseConnector;
import edu.temple.smartprompter_v3.res_lib.utils.Constants;
import edu.temple.smartprompter_v3.res_lib.utils.FbaEventLogger;

public class AdminDataChangeService extends BaseService {

    private FbaEventLogger eventLogger;
    private boolean isAdminAppEvent;
    private String mAlarmGUID;
    private Alarm mAlarm;

    @Override
    protected String getNotificationTitle() {
        return "SmartPrompter Admin Response Service";
    }

    @Override
    protected String getNotificationText() {
        return "SmartPrompter is responding to a data change event from the Admin app!";
    }

    @Override
    protected void doWork(Intent intent) {
        mAlarmGUID = intent.getStringExtra(Constants.BUNDLE_ARG_ALARM_GUID);
        isAdminAppEvent = intent.getAction().equals(getString(R.string.event_alarms_ready));
        if (!isAdminAppEvent) {
            Log.e(BaseActivity.LOG_TAG, "BROADCAST RECEIVED FOR UNKNOWN EVENT: " + intent.getAction());
            return;
        }

        FirebaseAuth mFbAuth = FirebaseAuth.getInstance();
        String email = (mFbAuth.getCurrentUser() == null
                ? "" : mFbAuth.getCurrentUser().getEmail());
        eventLogger = new FbaEventLogger(this, email);
        eventLogger.broadcastReceived(AdminAlertReceiver.class, intent.getAction(), mAlarmGUID);

        FirebaseConnector.FbDocListener completionListener = (result) -> {
            if (result == null) {
                Log.e(BaseActivity.LOG_TAG, "Something went wrong while attempting to "
                        + "retrieve alarms by GUID: " + mAlarmGUID);
                return;
            }

            mAlarm = (Alarm)result;
            Log.i(BaseActivity.LOG_TAG, "Received data change broadcast for alarm with GUID: "
                    + mAlarm.getGuid() + " \t Canceling existing alarms and reminders.  Setting new alarm.");

            SpController.cancelAlarm(this, mAlarm, BaseActivity.ALARM_RECEIVER_CLASS);
            SpController.cancelReminder(this, mAlarm, BaseActivity.ALARM_RECEIVER_CLASS);
            SpController.setAlarm(this, mAlarm, BaseActivity.ALARM_NOTIFICATION_CLASS,
                    BaseActivity.ALARM_RECEIVER_CLASS);

            AdminDataChangeService.this.stopSelf();
        };

        FirebaseConnector.FbFailureListener failureListener = (e) -> {
            Log.e(BaseActivity.LOG_TAG, "Something went wrong while attempting to "
                    + "retrieve alarms by GUID: " + mAlarmGUID + " in response to data change "
                    + "event from the admin app", e);

            // if it failed due to an issue with Firebase, then try one more time ...
            // if it fails this time, just let it fail ...
            if (e instanceof FirebaseFirestoreException) {
                FirebaseConnector.getAlarmByGuid(mAlarmGUID, completionListener,
                        error -> Log.e(BaseActivity.LOG_TAG, "Second attempt to get alarm "
                                + "has failed.  Aborting operation...", error));
            }
        };

        Log.i(BaseActivity.LOG_TAG, "ALARM ALERT BROADCAST RECEIVED FOR GUID: " + mAlarmGUID);
        FirebaseConnector.getAlarmByGuid(mAlarmGUID, completionListener, failureListener);
    }

}