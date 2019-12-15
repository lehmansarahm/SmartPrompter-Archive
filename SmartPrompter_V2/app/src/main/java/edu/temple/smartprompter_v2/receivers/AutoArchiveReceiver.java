package edu.temple.smartprompter_v2.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import edu.temple.smartprompter_v2.SmartPrompter;
import edu.temple.sp_res_lib.utils.DateTimeUtil;
import edu.temple.sp_res_lib.utils.Log;
import edu.temple.sp_res_lib.utils.StorageUtil;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class AutoArchiveReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        StorageUtil.archiveTodaysAlarms(context);
        long alarmTime = SmartPrompter.getNextAutoArchiveTime();
        Log.e(LOG_TAG, "Auto-archive alarm received, and contents of logs directory "
                + "have been archived!  ETA for next archive event: "
                + DateTimeUtil.formatTimeInMillis(alarmTime, DateTimeUtil.FORMAT.DateTime));
    }

}