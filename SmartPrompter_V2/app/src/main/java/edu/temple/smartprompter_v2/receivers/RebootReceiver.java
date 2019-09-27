package edu.temple.smartprompter_v2.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import edu.temple.smartprompter_v2.SmartPrompter;
import edu.temple.sp_res_lib.utils.Log;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.e(LOG_TAG, "REBOOT RECEIVED!!");
            Log.i(LOG_TAG, "Resetting any local alarms...");
            SmartPrompter app = (SmartPrompter)context.getApplicationContext();
            app.initializeFromReboot();
        } else {
            Log.e(LOG_TAG, "BROADCAST RECEIVED FOR UNKNOWN ACTION: " + intent.getAction());
        }
    }

}