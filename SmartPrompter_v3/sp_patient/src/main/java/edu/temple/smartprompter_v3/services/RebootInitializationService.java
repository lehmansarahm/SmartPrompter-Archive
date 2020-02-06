package edu.temple.smartprompter_v3.services;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import edu.temple.smartprompter_v3.activities.BaseActivity;
import edu.temple.smartprompter_v3.receivers.RebootReceiver;
import edu.temple.smartprompter_v3.res_lib.SpController;
import edu.temple.smartprompter_v3.res_lib.data.Alarm;
import edu.temple.smartprompter_v3.res_lib.data.FirebaseConnector;
import edu.temple.smartprompter_v3.res_lib.utils.FbaEventLogger;

public class RebootInitializationService extends BaseService {

    @Override
    protected String getNotificationTitle() {
        return "SmartPrompter Reboot Service";
    }

    @Override
    protected String getNotificationText() {
        return "SmartPrompter is recovering from device reboot.";
    }

    @Override
    protected void doWork(Intent intent) {
        FirebaseAuth fbAuth = FirebaseAuth.getInstance();
        String email = (fbAuth.getCurrentUser() == null
                ? "" : fbAuth.getCurrentUser().getEmail());
        FbaEventLogger eventLogger = new FbaEventLogger(this, email);
        eventLogger.broadcastReceived(RebootReceiver.class, intent.getAction(), "N/A");

        if (email.equals("")) {
            Log.e(BaseActivity.LOG_TAG, "No user currently logged in!");
            return;
        } else {
            //  set alarm clocks for all alarm records that match my email address
            FirebaseConnector.getActiveAlarmTasks(email, results -> {
                List<Alarm> alarms = (List<Alarm>)(Object)results;
                for (Alarm alarm : alarms) {
                    if (SpController.isAlarmLive(alarm))
                        SpController.setAlarm(this, alarm,
                                BaseActivity.ALARM_NOTIFICATION_CLASS,
                                BaseActivity.ALARM_RECEIVER_CLASS);
                }
            },
                    (error) -> Log.e(BaseActivity.LOG_TAG, "Something went wrong while "
                            + " attempting to retrieve alarms by email: " + email, error));
        }
    }

}