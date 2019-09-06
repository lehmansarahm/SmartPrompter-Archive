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
import android.view.WindowManager;

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

    @Override
    public void onReceive(Context context, Intent intent) {
        // confirm the received alarm details ...
        String guid = intent.getStringExtra(Constants.BUNDLE_ARG_ALARM_GUID);
        Alarm alarm = ((SmartPrompter)context.getApplicationContext()).getAlarmForAlert(guid);
        assert(alarm != null);

        Log.e(LOG_TAG, "ALARM ALERT BROADCAST RECEIVED FOR GUID: " + guid
                + " \t AND GUID-INT: " + alarm.getGuidInt()
                + " \t WITH ORIG ALARM TIME: " + alarm.getAlarmDateTimeString()
                + " \t AND STATUS: " + alarm.getStatus());

        // select the appropriate response activity ...
        Intent newIntent;
        if (alarm.getStatus().equals(Alarm.STATUS.Incomplete)) {
            Log.i(LOG_TAG, "Launching completion activity for alarm: " + alarm.getGuid());
            newIntent = new Intent(context, CompletionActivity.class);
        } else {
            Log.i(LOG_TAG, "Launching acknowledgment activity for alarm: " + alarm.getGuid());
            newIntent = new Intent(context, AcknowledgmentActivity.class);
        }

        // TODO - find out if we still want to auto-remind patients X number of times per stage
        //  (right now, they have to manually select the snooze option...)

        // start up the response activity ...
        newIntent.putExtra(Constants.BUNDLE_ARG_ALARM_GUID, guid);
        newIntent.putExtra(Constants.BUNDLE_ARG_ALARM_WAKEUP, true);
        newIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);
    }

}