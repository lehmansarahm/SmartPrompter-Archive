package edu.temple.smartprompter_v2.activities;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import java.util.concurrent.TimeUnit;

import edu.temple.smartprompter_v2.SmartPrompter;
import edu.temple.sp_res_lib.utils.Constants;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onPause() {
        Log.i(LOG_TAG, this.getLocalClassName() + " paused!");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(LOG_TAG, this.getLocalClassName() + " stopped!");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, this.getLocalClassName() + " destroyed!");
        super.onDestroy();
    }

    protected void wakeup(Context context) {
        Log.i(LOG_TAG, "Playing alarm alerts...");
        // TODO - cancel alarm alerts when user interacts with screen
        playAlarmAlerts(context);

        Log.i(LOG_TAG, "Waking up the device screen...");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
    }

    private void playAlarmAlerts(Context context) {
        if (Constants.PLAY_ALARM_TONE) {
            try {
                final MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(context, Constants.ALARM_ALERT_TONE);

                final AudioManager audioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (audioMgr.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.prepare();

                    Log.i(LOG_TAG, "Playing alarm alert tone!");
                    mediaPlayer.start();
                }

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer.isPlaying()) {
                            Log.i(LOG_TAG, "Stopping alarm alert tone!");
                            mediaPlayer.stop();
                        }
                    }
                }, Constants.ALARM_ALERT_DURATION);
            } catch(Exception e) {
                Log.e(LOG_TAG, "Something went wrong when trying to launch the alarm "
                        + "ALARM_ALERT_TONE ringtone!", e);
            }
        }

        if (Constants.PLAY_ALARM_VIBRATE) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                Log.i(LOG_TAG, "Starting alarm alert vibrate!");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    vibrator.vibrate(VibrationEffect.createOneShot(Constants.ALARM_ALERT_DURATION,
                            VibrationEffect.DEFAULT_AMPLITUDE));
                else vibrator.vibrate(Constants.ALARM_ALERT_DURATION);
            }
        }
    }

}
