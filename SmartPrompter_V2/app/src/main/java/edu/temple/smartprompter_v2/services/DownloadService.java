package edu.temple.smartprompter_v2.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import edu.temple.smartprompter_v2.R;
import edu.temple.smartprompter_v2.activities.MainActivity;
import edu.temple.sp_res_lib.utils.Log;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class DownloadService extends Service {

    private static final int ONGOING_NOTIFICATION_ID = 334;
    private static final String NOTIFICATION_CHANNEL_ID = "SpNotifs";
    private static final String NOTIFICATION_CHANNEL_NAME = "SmartPrompter Notifications";
    private static final String NOTIFICATION_TITLE = "SmartPrompter Download Service";
    private static final String NOTIFICATION_MESSAGE = "SmartPrompter is monitoring for downloadable data.";

    private static final long JOB_INTERVAL = TimeUnit.SECONDS.toMillis(15);

    private boolean isJobRunning = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        super.onStartCommand(intent, flags, startID);
        Notification notification = createNotification();
        startForeground(ONGOING_NOTIFICATION_ID, notification);
        if (!isJobRunning) doWork();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME);
            notification = new Notification.Builder(this, NOTIFICATION_CHANNEL_NAME)
                    .setContentTitle(NOTIFICATION_TITLE)
                    .setContentText(NOTIFICATION_MESSAGE)
                    .setSmallIcon(R.drawable.ic_actionbar)
                    .setChannelId(NOTIFICATION_CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .build();
        } else {
            notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_NAME)
                    .setContentTitle(NOTIFICATION_TITLE)
                    .setContentText(NOTIFICATION_MESSAGE)
                    .setSmallIcon(R.drawable.ic_actionbar)
                    .setContentIntent(pendingIntent)
                    .build();
        }

        return notification;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId, channelName,
                NotificationManager.IMPORTANCE_NONE);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.createNotificationChannel(chan);
    }

    private void doWork() {
        isJobRunning = true;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.i(LOG_TAG, "DownloadService is working!");
            }
        }, 0, JOB_INTERVAL);
    }

}