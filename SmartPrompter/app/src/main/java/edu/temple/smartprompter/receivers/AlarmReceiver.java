package edu.temple.smartprompter.receivers;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import edu.temple.smartprompter.TaskAcknowledgementActivity;
import edu.temple.smartprompter.utils.Constants;
import edu.temple.smartprompter.R;
import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.SpAlarmManager;

import static android.app.Notification.VISIBILITY_PUBLIC;

public class AlarmReceiver extends BroadcastReceiver {

    private int mAlarmID;
    private String mAlarmStatus;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (verifyIntentExtras(context, intent))
            generateNotification(context, mAlarmID, mAlarmStatus);
    }

    private boolean verifyIntentExtras(Context context, Intent intent) {
        if (!intent.hasExtra(Alarm.INTENT_EXTRA_ALARM_ID)) {
            Log.e(Constants.LOG_TAG, "Alarm broadcast has been received, "
                    + "but is missing the alarm ID.");
            return false;
        }

        if (!intent.hasExtra(Alarm.INTENT_EXTRA_ORIG_TIME)) {
            Log.e(Constants.LOG_TAG, "Alarm broadcast has been received, "
                    + "but is missing original alarm time.");
            return false;
        }

        mAlarmID = intent.getIntExtra(Alarm.INTENT_EXTRA_ALARM_ID, -1);
        String timeString = intent.getStringExtra(Alarm.INTENT_EXTRA_ORIG_TIME);
        Log.e(Constants.LOG_TAG, "Alarm broadcast has been received "
                + "for alarmID: " + mAlarmID + " at original time: " + timeString);

        SpAlarmManager alarmMgr = new SpAlarmManager(context);
        Alarm alarm = alarmMgr.get(mAlarmID);
        mAlarmStatus = alarm.getStatusString();

        return true;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private static void generateNotification(Context context, int alarmID, String alarmStatus) {
        createNotificationChannel(context);
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_alarm_on_white_18dp)
                .setContentTitle("SmartPrompter")
                .setContentText("Please complete your task!")
                .setSound(ringtoneUri)
                .setContentIntent(createNotificationIntent(context, alarmID, alarmStatus))
                .setVisibility(VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(alarmID, mBuilder.build());
    }

    @SuppressLint("WrongConstant")
    private static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_MAX;
            NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID,
                    Constants.CHANNEL_NAME, importance);
            channel.setDescription(Constants.CHANNEL_DESCRIPTION);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static PendingIntent createNotificationIntent(Context context, int alarmID,
                                                          String alarmStatus) {

        Log.i(Constants.LOG_TAG, "Creating intent to launch Task Acknowledgement activity "
                + "for alarm ID: " + alarmID + " \t\t with current status: " + alarmStatus);

        Intent intent = new Intent(context, TaskAcknowledgementActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.INTENT_EXTRA_ALARM_ID, alarmID);
        intent.putExtra(Constants.INTENT_EXTRA_ALARM_CURRENT_STATUS, alarmStatus);
        return PendingIntent.getActivity(context, alarmID, intent, Constants.PENDING_INTENT_FLAGS);
    }

}