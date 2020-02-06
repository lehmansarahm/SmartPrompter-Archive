package edu.temple.smartprompter_v3.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import edu.temple.smartprompter_v3.activities.BaseActivity;
import edu.temple.smartprompter_v3.services.RebootInitializationService;

public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.e(BaseActivity.LOG_TAG, "BROADCAST RECEIVED FOR UNKNOWN ACTION: "
                    + intent.getAction());
            return;
        }

        Log.i(BaseActivity.LOG_TAG, "REBOOT BROADCAST RECEIVED!!  Starting "
                + "RebootInitializationService and resetting any local alarms...");
        intent.setClass(context, RebootInitializationService.class);
        context.startForegroundService(intent);
    }

}