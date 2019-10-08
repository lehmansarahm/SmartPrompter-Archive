package edu.temple.smartprompter_v2.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import edu.temple.sp_res_lib.utils.DateTimeUtil;
import edu.temple.sp_res_lib.utils.EmailUtil;
import edu.temple.sp_res_lib.utils.Log;
import edu.temple.sp_res_lib.utils.StorageUtil;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class ExportReceiver extends BroadcastReceiver {

    private static final int EXPORT_RC = 9209;
    private static final int EXPORT_INTERVAL_MILLI =
            (int) TimeUnit.MINUTES.toMillis(15);

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "Export event received!");
        // EmailUtil emailUtil = new EmailUtil();
        // emailUtil.send(context, "Daily Logs",
        //        "\n \n \n SmartPrompter - a Temple University application");
        // scheduleExport(context);
    }

    public static void scheduleExport(Context ctx) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, EXPORT_INTERVAL_MILLI);
        // calendar.set(Calendar.HOUR_OF_DAY, 0);
        // calendar.set(Calendar.MINUTE, 0);

        Intent intent = new Intent(ctx, ExportReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,
                EXPORT_RC, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        StorageUtil.updateExportCheck(ctx, String.valueOf(calendar.getTimeInMillis()));
        Log.i(LOG_TAG, "Updated export check file with value: "
                + DateTimeUtil.formatTimeInMillis(calendar.getTimeInMillis(), DateTimeUtil.FORMAT.DateTime));

        AlarmManager mAlarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        // mAlarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
        //         AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
    }

}