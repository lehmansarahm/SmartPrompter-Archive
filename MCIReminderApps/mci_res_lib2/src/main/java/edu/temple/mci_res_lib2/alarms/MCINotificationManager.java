package edu.temple.mci_res_lib2.alarms;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import edu.temple.mci_res_lib2.R;
import edu.temple.mci_res_lib2.utils.Constants;

public class MCINotificationManager {

    private static MediaPlayer mp;

    public static void playNotificationTone(Context context, long playTimeMs) {
        try {
            mp = MediaPlayer.create(context, R.raw.drink_water_now);
            mp.setLooping(true);
            mp.start();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() { cancelNotificationTone(); }
            }, playTimeMs);
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "Something went wrong while attempting to play the notification tone.", e);
        }
    }


    public static void cancelNotificationTone() {
        mp.setLooping(false);
        mp.stop();
    }
}