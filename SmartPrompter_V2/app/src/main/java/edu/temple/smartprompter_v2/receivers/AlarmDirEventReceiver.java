package edu.temple.smartprompter_v2.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.concurrent.TimeUnit;

import edu.temple.smartprompter_v2.SmartPrompter;
import edu.temple.sp_res_lib.utils.DateTimeUtil;
import edu.temple.sp_res_lib.utils.Log;
import edu.temple.sp_res_lib.utils.StorageUtil;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class AlarmDirEventReceiver extends BroadcastReceiver {

    private static final int REQUEST_CODE = 999;
    private static final long JOB_INTERVAL = TimeUnit.SECONDS.toMillis(15);

    @Override
    public void onReceive(Context context, Intent intent) {
        scheduleNextDirectoryCheck(context);
        boolean isAlarmsFolderDirty = StorageUtil.getDirtyStatus(context);
        Log.i(LOG_TAG, "Is Alarms folder dirty:  " + isAlarmsFolderDirty);
        if (isAlarmsFolderDirty)
            ((SmartPrompter)context.getApplicationContext()).cleanupDirtyAlarms();
    }

    public static void scheduleNextDirectoryCheck(Context context) {
        Intent dialogIntent = getDialogIntent(context);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                REQUEST_CODE, dialogIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long nextWakeupTime = (System.currentTimeMillis() + JOB_INTERVAL);
        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextWakeupTime, pendingIntent);
        Log.e(LOG_TAG, "Next alarm directory check scheduled for: "
                + DateTimeUtil.formatTimeInMillis(nextWakeupTime, DateTimeUtil.FORMAT.DateTime));
    }

    public static boolean isDirectoryCheckScheduled(Context context) {
        Intent dialogIntent = getDialogIntent(context);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE,
                dialogIntent, PendingIntent.FLAG_NO_CREATE);
        return (pendingIntent != null);
    }

    private static Intent getDialogIntent(Context context) {
        Intent dialogIntent = new Intent(context, AlarmDirEventReceiver.class);
        return dialogIntent;
    }

}