package edu.temple.sp_res_lib;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;

/*
    WAKEFUL BROADCAST RECEIVER CLASS IS DEPRECATED ...
    COPYING THIS HERE FOR REFERENCE ...
    SOURCE:  https://stackoverflow.com/questions/47217345/wakefulbroadcastreceiver-is-deprecated

    ... MIGHT BE SOMETHING HERE?????
    https://stackoverflow.com/questions/27913169/alarmmanager-wont-go-off-when-set-to-10-minutes
 */

public class WakefulAlarmReceiver extends WakefulBroadcastReceiver {
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    @Override
    public void onReceive(Context context, Intent intent) {
        mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        turnOnScreen();
        Intent wakeIntent = new Intent();

        wakeIntent.setClassName("com.packagename", "com.packagename.activity.TaskFinished");
        wakeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(wakeIntent);
    }


    public void turnOnScreen(){
        // mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
        // mWakeLock.acquire();
    }
}