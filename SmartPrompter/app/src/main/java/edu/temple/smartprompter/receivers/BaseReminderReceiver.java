package edu.temple.smartprompter.receivers;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import edu.temple.smartprompter.R;
import edu.temple.smartprompter.utils.Constants;
import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.Reminder;
import edu.temple.sp_res_lib.SpAlarmManager;
import edu.temple.sp_res_lib.SpReminderManager;

import static android.app.Notification.VISIBILITY_PUBLIC;

public abstract class BaseReminderReceiver extends BroadcastReceiver {

    protected int mReminderID;
    protected Reminder mReminder;
    protected SpReminderManager mRemMgr;
    protected String mOrigReminderTime;

    protected int mAlarmID;
    protected Alarm mAlarm;
    protected SpAlarmManager mAlarmMgr;

    protected String CHANNEL_ID;
    protected CharSequence CHANNEL_NAME;
    protected String CHANNEL_DESCRIPTION;

    protected Intent intent;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (verifyIntentExtras(intent)) {
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

            // just in case there's a lingering alarm notification ...
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context);
            notificationManager.cancel(mAlarm.getRequestCode());

            // Get ready to handle the next alarm reminder ..
            if (mReminder.hasExceededCountLimit()) {
                Log.e(Constants.LOG_TAG, "REMINDER LIMIT FOR ALARM ID: " + mAlarmID
                        + " HAS BEEN EXCEEDED. CANCELLING ALL NOTIFICATIONS FOR THIS "
                        + "ALARM, AND SETTING ALARM STATUS TO 'TIMED OUT'.");
                mAlarmMgr.cancelAlarm(mAlarm);
                mAlarm.updateStatus(edu.temple.sp_res_lib.utils.Constants.ALARM_STATUS.TimedOut);
                mAlarmMgr.update(mAlarm);
            } else if (mReminder.hasReachedCountLimit()) {
                Log.e(Constants.LOG_TAG, "REMINDER LIMIT FOR ALARM ID: " + mAlarmID
                        + " HAS BEEN REACHED. SCHEDULING FINAL NOTIFICATION-LESS "
                        + "GRACE PERIOD REMINDER.  \n\t\t (When this reminder goes off, "
                        + "app will auto-close parent alarm with status TimedOut.)");
                scheduleFollowupReminder();
            } else {
                Log.i(Constants.LOG_TAG, "Alarm ID: " + mAlarmID + " still has "
                        + "reminders available.  Scheduling next reminder.");
                scheduleFollowupReminder();
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

        Log.e(Constants.LOG_TAG, "Reminder broadcast has been received, "
                + "for reminder ID: " + mReminderID
                + " and alarm ID: " + mAlarmID
                + ", scheduled at original time: " + mOrigReminderTime);
        return true;
    }

    private void scheduleFollowupReminder() {
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

    private void playNotificationSound(Context context) {
        Log.e(Constants.LOG_TAG, "PLAYING NOTIFICATION SOUND");
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, ringtoneUri);
        r.play();
    }

    @SuppressLint("WrongConstant")
    private void generateNotification(Context context) {
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        createNotificationChannel(context);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_alarm_on_white_18dp)
                .setContentTitle("SmartPrompter")
                .setContentText("Please complete your task! (reminder)")
                .setContentIntent(createNotificationIntent(context))
                .setSound(ringtoneUri)
                .setVisibility(VISIBILITY_PUBLIC)
                .setPriority(Notification.PRIORITY_MAX);

        Log.i(Constants.LOG_TAG, "Building notification for reminder ID: "
                + mReminderID + " using request code: " + mReminder.getRequestCode());
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(mReminder.getRequestCode(), mBuilder.build());
    }

    @SuppressLint("WrongConstant")
    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e(Constants.LOG_TAG, "Attempting to create a new notification "
                    + "channel using ID: " + CHANNEL_ID + " \t\t and name: " + CHANNEL_NAME);

            int importance = NotificationManager.IMPORTANCE_MAX;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);

            try {
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager =
                        context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            } catch (Exception ex) {
                Log.e(Constants.LOG_TAG, "WHY IS THIS HAPPENING?!", ex);
            }
        }
    }

    private PendingIntent createNotificationIntent(Context context) {
        Log.i(Constants.LOG_TAG, "Creating intent to launch response activity "
                + "for reminder ID: " + mReminderID + " \t\t and alarm ID: " + mAlarmID);

        intent.putExtra(Constants.INTENT_EXTRA_REMINDER_ID, mReminderID);
        intent.putExtra(Constants.INTENT_EXTRA_ALARM_ID, mAlarmID);
        intent.putExtra(Constants.INTENT_EXTRA_ALARM_CURRENT_STATUS, mAlarm.getStatusString());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Log.i(Constants.LOG_TAG, "Returning notification intent for reminder ID: "
                + mReminderID + " using request code: " + mReminder.getRequestCode());
        return PendingIntent.getActivity(context, mReminder.getRequestCode(),
                intent, Constants.PENDING_INTENT_FLAGS);
    }

}