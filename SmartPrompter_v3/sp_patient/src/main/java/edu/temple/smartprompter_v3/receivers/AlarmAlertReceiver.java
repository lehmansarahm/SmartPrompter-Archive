package edu.temple.smartprompter_v3.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import edu.temple.smartprompter_v3.activities.BaseActivity;
import edu.temple.smartprompter_v3.services.AlarmNotificationService;

public class AlarmAlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.i(BaseActivity.LOG_TAG, "ALARM BROADCAST RECEIVED!!  Starting "
                + "AlarmNotificationService ...");
        intent.setClass(context, AlarmNotificationService.class);
        context.startForegroundService(intent);
    }

}