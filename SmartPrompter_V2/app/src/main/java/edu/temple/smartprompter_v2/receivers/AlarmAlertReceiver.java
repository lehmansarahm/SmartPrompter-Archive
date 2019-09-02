package edu.temple.smartprompter_v2.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import edu.temple.smartprompter_v2.SmartPrompter;
import edu.temple.smartprompter_v2.activities.AcknowledgmentActivity;
import edu.temple.smartprompter_v2.activities.CompletionActivity;
import edu.temple.smartprompter_v2.activities.MainActivity;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.utils.Constants;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class AlarmAlertReceiver extends BroadcastReceiver {

    private static final long ALARM_ALERT_DURATION = TimeUnit.SECONDS.toMillis(15);
    private static final Uri ALARM_ALERT_TONE =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

    private static final boolean PLAY_ALARM_TONE = true;
    private static final boolean PLAY_ALARM_VIBRATE = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        // confirm the received alarm details ...
        String guid = intent.getStringExtra(Constants.BUNDLE_ARG_ALARM_GUID);
        Alarm alarm = ((SmartPrompter)context.getApplicationContext()).getAlarmForAlert(guid);
        Log.e(LOG_TAG, "ALARM ALERT BROADCAST RECEIVED FOR GUID: " + guid
                + " \t AND GUID-INT: " + alarm.getGuidInt()
                + " \t WITH ORIG ALARM TIME: " + alarm.getAlarmDateTimeString()
                + " \t AND STATUS: " + alarm.getStatus());

        // play alarm alerts ...
        playAlarmAlerts(context);

        // TODO - make full screen ... figure out how to wake up the screen as well as
        //  the device itself

        // TODO - figure out why alarm reminders aren't playing (the app is waking up, but the
        //  alarm alert receiver isn't firing...)

        // select the appropriate response activity ...
        Intent newIntent;
        if (alarm.getStatus().equals(Alarm.STATUS.Incomplete)) {
            Log.i(LOG_TAG, "Launching completion activity for alarm: " + alarm.getGuid());
            newIntent = new Intent(context, CompletionActivity.class);
        } else {
            Log.i(LOG_TAG, "Launching acknowledgment activity for alarm: " + alarm.getGuid());
            newIntent = new Intent(context, AcknowledgmentActivity.class);
        }

        // start up the response activity ...
        newIntent.putExtra(Constants.BUNDLE_ARG_ALARM_GUID, guid);
        newIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);
    }

    private void playAlarmAlerts(Context context) {
        if (PLAY_ALARM_TONE) {
            try {
                final MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(context, ALARM_ALERT_TONE);

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
                }, ALARM_ALERT_DURATION);
            } catch(Exception e) {
                Log.e(LOG_TAG, "Something went wrong when trying to launch the alarm "
                        + "ALARM_ALERT_TONE ringtone!", e);
            }
        }

        if (PLAY_ALARM_VIBRATE) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                Log.i(LOG_TAG, "Starting alarm alert vibrate!");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    vibrator.vibrate(VibrationEffect.createOneShot(ALARM_ALERT_DURATION,
                            VibrationEffect.DEFAULT_AMPLITUDE));
                else vibrator.vibrate(ALARM_ALERT_DURATION);
            }
        }
    }

}