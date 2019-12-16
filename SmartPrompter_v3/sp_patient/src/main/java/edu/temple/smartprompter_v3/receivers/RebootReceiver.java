package edu.temple.smartprompter_v3.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import edu.temple.smartprompter_v3.activities.BaseActivity;
import edu.temple.smartprompter_v3.res_lib.SpController;
import edu.temple.smartprompter_v3.res_lib.data.Alarm;
import edu.temple.smartprompter_v3.res_lib.data.FirebaseConnector;
import edu.temple.smartprompter_v3.res_lib.utils.FbaEventLogger;

public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.e(BaseActivity.LOG_TAG, "BROADCAST RECEIVED FOR UNKNOWN ACTION: "
                    + intent.getAction());
            return;
        }

        FbaEventLogger eventLogger = new FbaEventLogger(context);
        eventLogger.broadcastReceived(AlarmAlertReceiver.class, intent.getAction(), "");

        Log.e(BaseActivity.LOG_TAG, "REBOOT RECEIVED!!");
        Log.i(BaseActivity.LOG_TAG, "Resetting any local alarms...");

        //  TODO - setAlarm alarm clocks for all alarm records that match my email address
        FirebaseAuth fbAuth = FirebaseAuth.getInstance();
        if (fbAuth.getCurrentUser() == null) {
            Log.e(BaseActivity.LOG_TAG, "No user currently logged in!");
            return;
        } else {
            String email = fbAuth.getCurrentUser().getEmail();
            FirebaseConnector.getActiveAlarmTasks(email, results -> {
                List<Alarm> alarms = (List<Alarm>)(Object)results;
                for (Alarm alarm : alarms) {
                    if (SpController.isAlarmLive(alarm))
                        SpController.setAlarm(context, alarm,
                                BaseActivity.ALARM_NOTIFICATION_CLASS,
                                BaseActivity.ALARM_RECEIVER_CLASS);
                }
            });
        }
    }

}