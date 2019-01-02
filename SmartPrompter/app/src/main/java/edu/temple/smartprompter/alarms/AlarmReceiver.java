package edu.temple.smartprompter.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import edu.temple.smartprompter.utils.Constants;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(Alarm.INTENT_EXTRA_REQUEST_CODE) &&
                intent.hasExtra(Alarm.INTENT_EXTRA_ORIG_TIME)) {
            int requestCode = intent.getIntExtra(Alarm.INTENT_EXTRA_REQUEST_CODE, -1);
            String timeString = intent.getStringExtra(Alarm.INTENT_EXTRA_ORIG_TIME);
            Log.e(Constants.LOG_TAG, "Alarm broadcast has been received "
                    + "for request code: " + requestCode + " at original time: " + timeString);
        } else {
            Log.e(Constants.LOG_TAG, "Alarm broadcast has been received, "
                    + "but is missing parameters.");
        }

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
    }

}