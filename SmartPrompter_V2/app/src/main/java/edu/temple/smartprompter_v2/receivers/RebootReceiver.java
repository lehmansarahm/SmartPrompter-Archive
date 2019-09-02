package edu.temple.smartprompter_v2.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class RebootReceiver extends BroadcastReceiver {

    private static final int ALARM_MONITOR_SERVICE_JOB_ID = 999;
    private static final long SYS_CHECK_PERIOD = TimeUnit.SECONDS.toMillis(5);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.e(LOG_TAG, "REBOOT RECEIVED!!");
            // TODO - start up download service

            // TODO - start up local file observer (if any alarm-clocks are set that don't match
            //  the GUIDs we have available, cancel them...)

            // TODO - restart any alarms for local files that are still active
        } else {
            Log.e(LOG_TAG, "BROADCAST RECEIVED FOR UNKNOWN ACTION: " + intent.getAction());
        }
    }

}