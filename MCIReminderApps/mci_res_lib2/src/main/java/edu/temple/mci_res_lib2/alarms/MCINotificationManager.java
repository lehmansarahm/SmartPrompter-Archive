package edu.temple.mci_res_lib2.alarms;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import edu.temple.mci_res_lib2.R;
import edu.temple.mci_res_lib2.utils.Constants;

public class MCINotificationManager {

    private static long startTimeMs;
    private static Ringtone r;

    public static void playNotificationTone(Context context, long playTimeMs) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            r = RingtoneManager.getRingtone(context, notification);
            startTimeMs = System.currentTimeMillis();
            registerReceiver(context);

            long elapsedTimeMs = (System.currentTimeMillis() - startTimeMs);
            while (elapsedTimeMs < playTimeMs) {
                r.play();
                elapsedTimeMs = (System.currentTimeMillis() - startTimeMs);
            }
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "Something went wrong while attempting to play the notification tone.", e);
        }
    }

    public static void stopNotificationTone() {
        r.stop();
    }

    private static void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        ScreenUnlockReceiver mReceiver = new ScreenUnlockReceiver();
        context.registerReceiver(mReceiver, filter);
    }

}