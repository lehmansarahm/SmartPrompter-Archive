package edu.temple.smartprompter.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import edu.temple.smartprompter.R;
import edu.temple.smartprompter.TaskAcknowledgementActivity;
import edu.temple.smartprompter.TaskCompletionActivity;
import edu.temple.smartprompter.utils.Constants;

import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.Reminder;
import edu.temple.sp_res_lib.SpAlarmManager;
import edu.temple.sp_res_lib.SpReminderManager;
import edu.temple.sp_res_lib.utils.Constants.ALARM_STATUS;

import static android.app.Notification.VISIBILITY_PUBLIC;

public class ReminderReceiver extends BroadcastReceiver {

    private int mReminderID;
    private Reminder mReminder;
    private SpReminderManager mRemMgr;
    private String mOrigReminderTime;

    private int mAlarmID;
    private Alarm mAlarm;
    private SpAlarmManager mAlarmMgr;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (verifyIntentExtras(intent)) {
            // cancel any lingering notifications
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(mAlarmID);
            nm.cancel(mReminderID);

            // retrieve the current reminder
            mRemMgr = new SpReminderManager(context);
            mReminder = mRemMgr.get(mReminderID);
            if (mReminder == null) {
                Log.e(Constants.LOG_TAG, "NO MATCHING RECORD FOR PROVIDED REMINDER ID.");
                return;
            }

            // retrieve the current alarm
            mAlarmMgr = new SpAlarmManager(context);
            mAlarm = mAlarmMgr.get(mAlarmID);
            if (mAlarm == null) {
                Log.e(Constants.LOG_TAG, "NO MATCHING RECORD FOR PROVIDED ALARM ID.");
                return;
            }

            // Get ready to handle the next alarm reminder ..
            if (mReminder.hasExceededCountLimit()) {
                Log.e(Constants.LOG_TAG, "REMINDER LIMIT FOR ALARM ID: " + mAlarmID
                        + " HAS BEEN EXCEEDED. CANCELLING ALL NOTIFICATIONS FOR THIS "
                        + "ALARM, AND SETTING ALARM STATUS TO 'TIMED OUT'.");
                mAlarmMgr.cancelAlarm(mAlarm);
                mAlarm.updateStatus(ALARM_STATUS.TimedOut);
                mAlarmMgr.update(mAlarm);
            } else if (mReminder.hasReachedCountLimit()) {
                Log.e(Constants.LOG_TAG, "REMINDER LIMIT FOR ALARM ID: " + mAlarmID
                        + " HAS BEEN REACHED. SCHEDULING FINAL NOTIFICATION-LESS "
                        + "GRACE PERIOD REMINDER.  \n\t\t (When this reminder goes off, "
                        + "app will auto-close parent alarm with status TimedOut.)");
                scheduleReminder();
            } else {
                Log.i(Constants.LOG_TAG, "Alarm ID: " + mAlarmID + " still has "
                        + "reminders available.  Scheduling next reminder.");
                scheduleReminder();
                generateNotification(context);
            }
        }
    }

    private boolean verifyIntentExtras(Intent intent) {
        if (!intent.hasExtra(Constants.INTENT_EXTRA_REMINDER_ID)) {
            Log.e(Constants.LOG_TAG, "Reminder broadcast has been received, "
                    + "but is missing the reminder ID.");
            return false;
        }

        if (!intent.hasExtra(Constants.INTENT_EXTRA_ALARM_ID)) {
            Log.e(Constants.LOG_TAG, "Reminder broadcast has been received, "
                    + "but is missing the alarm ID.");
            return false;
        }

        if (!intent.hasExtra(Constants.INTENT_EXTRA_ORIG_TIME)) {
            Log.e(Constants.LOG_TAG, "Reminder broadcast has been received, "
                    + "but is missing the original reminder time.");
            return false;
        }

        mOrigReminderTime = intent.getStringExtra(Constants.INTENT_EXTRA_ORIG_TIME);
        mReminderID = intent.getIntExtra(Constants.INTENT_EXTRA_REMINDER_ID, -1);
        mAlarmID = intent.getIntExtra(Constants.INTENT_EXTRA_ALARM_ID, -1);

        Log.e(Constants.LOG_TAG, "Reminder broadcast has been received "
                + "for reminder ID: " + mReminderID
                + " and alarm ID: " + mAlarmID
                + ", scheduled at original time: " + mOrigReminderTime);
        return true;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void scheduleReminder() {

        // NOTE - THERE WILL ONLY EVER BE ONE REMINDER OF EACH TYPE IN THE DB
        // AFTER THIS POINT IN THE PROGRAM FLOW, RETRIEVE THE EXISTING ACKNOWLEDGEMENT
        // REMINDER AND KEEP RESCHEDULING IT UNTIL WE HIT THE LIMIT OR THE USER ACKNOWLEDGES

        // ... IF WE MADE IT HERE, THE REMINDER ALREADY EXISTS ... NO NEED TO CREATE A NEW ONE

        // ... RINSE AND REPEAT FOR THE COMPLETION REMINDERS

        // --------------------------------------------------------------------------------------
        // --------------------------------------------------------------------------------------

        // calculate the appropriate reminder interval and commit changes to database
        mReminder.calculateNewReminder();
        mRemMgr.update(mReminder);

        // schedule the reminder (ReminderManager will increment remCount for us)
        mRemMgr.scheduleReminder(mReminder);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void generateNotification(Context context) {
        // NO NEED TO CREATE NOTIFICATION CHANNEL ...
        // original Alarm Receiver took care of that for us ...

        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_alarm_on_white_18dp)
                .setContentTitle("SmartPrompter")
                .setContentText("Please complete your task! (reminder)")
                .setSound(ringtoneUri)
                .setContentIntent(createNotificationIntent(context))
                .setVisibility(VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        Log.i(Constants.LOG_TAG, "Building notification for reminder ID: "
                + mReminderID + " using request code: " + mReminder.getRequestCode());
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(mReminder.getRequestCode(), mBuilder.build());
    }

    private PendingIntent createNotificationIntent(Context context) {
        Log.i(Constants.LOG_TAG, "Creating intent to launch reminder response activity "
                + "for reminder ID: " + mReminderID + " \t\t and alarm ID: " + mAlarmID);

        Intent intent = new Intent();
        if (mAlarm.hasStatus(ALARM_STATUS.Unacknowledged))
            intent.setClass(context, TaskAcknowledgementActivity.class);
        else if (mAlarm.hasStatus(ALARM_STATUS.Incomplete))
            intent.setClass(context, TaskCompletionActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.INTENT_EXTRA_REMINDER_ID, mReminderID);
        intent.putExtra(Constants.INTENT_EXTRA_ALARM_ID, mAlarmID);
        intent.putExtra(Constants.INTENT_EXTRA_ALARM_CURRENT_STATUS, mAlarm.getStatusString());

        Log.i(Constants.LOG_TAG, "Returning notification intent for reminder ID: "
                + mReminderID + " using request code: " + mReminder.getRequestCode());
        return PendingIntent.getActivity(context, mReminder.getRequestCode(),
                intent, Constants.PENDING_INTENT_FLAGS);
    }

}
