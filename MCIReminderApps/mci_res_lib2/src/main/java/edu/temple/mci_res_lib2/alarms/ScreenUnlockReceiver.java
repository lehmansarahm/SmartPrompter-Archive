package edu.temple.mci_res_lib2.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import edu.temple.mci_res_lib2.utils.Constants;

public class ScreenUnlockReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constants.LOG_TAG, "UNLOCK EVENT RECEIVED.");
        MCINotificationManager.stopNotificationTone();
    }

}