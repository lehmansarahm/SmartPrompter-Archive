package edu.temple.smartprompter.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import edu.temple.smartprompter.ReminderResponseActivity;
import edu.temple.smartprompter.utils.Constants;

public class ReminderReceiver extends BroadcastReceiver {

    private int mAlarmID;
    private String mAlarmStatus;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (verifyIntentExtras(intent))
            generateNotification(context, mAlarmID, mAlarmStatus);
    }

    private boolean verifyIntentExtras(Intent intent) {
        if (!intent.hasExtra(Constants.INTENT_EXTRA_ALARM_ID)) {
            Log.e(Constants.LOG_TAG, "Alarm broadcast has been received, "
                    + "but is missing the alarm ID.");
            return false;
        }

        if (!intent.hasExtra(Constants.INTENT_EXTRA_ALARM_CURRENT_STATUS)) {
            Log.e(Constants.LOG_TAG, "Alarm broadcast has been received, "
                    + "but is missing current alarm status.");
            return false;
        }

        mAlarmID = intent.getIntExtra(Constants.INTENT_EXTRA_ALARM_ID, -1);
        mAlarmStatus = intent.getStringExtra(Constants.INTENT_EXTRA_ALARM_CURRENT_STATUS);
        Log.e(Constants.LOG_TAG, "Alarm broadcast has been received "
                + "for alarmID: " + mAlarmID + " with current status: " + mAlarmStatus);
        return true;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private static void generateNotification(Context context, int alarmID, String alarmStatus) {
        // TODO - generate reminder notification !!

        // NO NEED TO CREATE NOTIFICATION CHANNEL ...
        // original Alarm Receiver took care of that for us ...

        /* Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_alarm_on_white_18dp)
                .setContentTitle("SmartPrompter")
                .setContentText("Please complete your task!")
                .setSound(ringtoneUri)
                .setContentIntent(createNotificationIntent(context, alarmID, alarmStatus))
                .setVisibility(VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(alarmID, mBuilder.build()); */
    }

    private static PendingIntent createNotificationIntent(Context context, int alarmID,
                                                          String alarmStatus) {

        Log.i(Constants.LOG_TAG, "Creating intent to launch Reminder Response activity "
                + "for alarm ID: " + alarmID + " \t\t with current status: " + alarmStatus);

        Intent intent = new Intent(context, ReminderResponseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.INTENT_EXTRA_ALARM_ID, alarmID);
        intent.putExtra(Constants.INTENT_EXTRA_ALARM_CURRENT_STATUS, alarmStatus);
        return PendingIntent.getActivity(context, alarmID, intent, Constants.PENDING_INTENT_FLAGS);
    }

}
