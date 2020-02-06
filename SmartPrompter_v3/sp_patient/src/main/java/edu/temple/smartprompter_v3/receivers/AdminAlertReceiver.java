package edu.temple.smartprompter_v3.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import edu.temple.smartprompter_v3.activities.BaseActivity;
import edu.temple.smartprompter_v3.services.AdminDataChangeService;

public class AdminAlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.i(BaseActivity.LOG_TAG, "ADMIN DATA BROADCAST RECEIVED!!  Starting "
                + "AdminDataChangeService...");
        intent.setClass(context, AdminDataChangeService.class);
        context.startForegroundService(intent);
    }

}