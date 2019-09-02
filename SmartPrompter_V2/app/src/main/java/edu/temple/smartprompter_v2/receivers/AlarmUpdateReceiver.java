package edu.temple.smartprompter_v2.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class AlarmUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(LOG_TAG, "ALARM UPDATE BROADCAST RECEIVED.");
    }

}