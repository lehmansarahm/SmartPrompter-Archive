package edu.temple.smartprompter.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import edu.temple.smartprompter.util.Constants;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(Alarm.INTENT_EXTRA_REQUEST_CODE)) {
            int requestCode = intent.getIntExtra(Alarm.INTENT_EXTRA_REQUEST_CODE, -1);
            Log.e(Constants.LOG_TAG, "Alarm broadcast has been received "
                    + "for request code: " + requestCode);
        } else {
            Log.e(Constants.LOG_TAG, "Alarm broadcast has been received, "
                    + "but has no request code.");
        }

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
    }

}