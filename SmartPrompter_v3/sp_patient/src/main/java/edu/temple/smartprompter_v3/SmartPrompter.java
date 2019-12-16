package edu.temple.smartprompter_v3;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;

import java.util.concurrent.TimeUnit;

import edu.temple.smartprompter_v3.res_lib.utils.MediaUtil;

import static edu.temple.smartprompter_v3.res_lib.utils.Constants.LOG_TAG;

public class SmartPrompter extends Application {

    private static final String WAKELOCK_TAG = "smartprompter:wakelock";

    private static final long WAKELOCK_TIMEOUT = TimeUnit.SECONDS.toMillis(30);

    private static final int ALERT_FLAGS =
            (WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

    private static PowerManager.WakeLock wakeLock;

    public static void wakeup(Activity context, boolean playAlerts, MediaUtil.AUDIO_TYPE audioType) {
        if (playAlerts) {
            Log.i(LOG_TAG, "Playing alarm alerts...");
            MediaUtil.playAlarmAlerts(context, audioType, null);
        } else {
            Log.i(LOG_TAG, "Stopping alarm alerts...");
            MediaUtil.stopAlarmAlerts(context.getApplicationContext());
        }

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            Log.i(LOG_TAG, "Acquiring wake lock...");
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, WAKELOCK_TAG);
            wakeLock.acquire(WAKELOCK_TIMEOUT);
        }

        Log.i(LOG_TAG, "Turning on device screen...");
        context.getWindow().addFlags(ALERT_FLAGS);
    }

    public static void stopWakeup(Activity context) {
        if (wakeLock != null && wakeLock.isHeld()) {
            Log.i(LOG_TAG, "Releasing wake lock!");
            wakeLock.release();
        }

        MediaUtil.stopAlarmAlerts(context.getApplicationContext());
    }

}