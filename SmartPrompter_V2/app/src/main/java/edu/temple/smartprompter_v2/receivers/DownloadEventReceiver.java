package edu.temple.smartprompter_v2.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import edu.temple.sp_res_lib.utils.Constants;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class DownloadEventReceiver extends BroadcastReceiver {

    private static final int REQUEST_CODE = 998;
    private static final long JOB_INTERVAL = TimeUnit.SECONDS.toMillis(15);

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "Download broadcast received.");
        scheduleNextDownload(context);
    }

    public static void scheduleNextDownload(Context context) {
        Intent dialogIntent = new Intent(context, DownloadEventReceiver.class);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                REQUEST_CODE, dialogIntent,PendingIntent.FLAG_CANCEL_CURRENT);

        long nextWakeupTime = (System.currentTimeMillis() + JOB_INTERVAL);
        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextWakeupTime, pendingIntent);
        Log.e(LOG_TAG, "Next download scheduled for: "
                + Constants.formatTimeInMillis(nextWakeupTime, Constants.DATE_TIME_FORMAT));
    }

}