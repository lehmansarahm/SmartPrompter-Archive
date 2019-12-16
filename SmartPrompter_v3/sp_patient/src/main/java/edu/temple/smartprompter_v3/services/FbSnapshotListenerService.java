package edu.temple.smartprompter_v3.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import edu.temple.smartprompter_v3.activities.BaseActivity;
import edu.temple.smartprompter_v3.activities.MainActivity;
import edu.temple.smartprompter_v3.R;
import edu.temple.smartprompter_v3.res_lib.data.Alarm;

public class FbSnapshotListenerService extends Service {

    private static final int ONGOING_NOTIFICATION_ID = 333;

    private static final String NOTIFICATION_CHANNEL_ID = "SpNotifs";
    private static final String NOTIFICATION_CHANNEL_NAME = "SmartPrompter Notifications";
    private static final String NOTIFICATION_TITLE = "SmartPrompter Monitor Service";
    private static final String NOTIFICATION_MESSAGE = "SmartPrompter is monitoring for alarm updates.";

    private static final long JOB_INTERVAL = TimeUnit.SECONDS.toMillis(60);

    private FirebaseFirestore mFbFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mFbAuth = FirebaseAuth.getInstance();
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

        createNotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME);
        Notification notification = new Notification.Builder(this, NOTIFICATION_CHANNEL_NAME)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(NOTIFICATION_MESSAGE)
                .setSmallIcon(R.drawable.ic_actionbar)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .build();

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
        mFbFirestore.collection(Alarm.COLLECTION)
                .whereEqualTo(Alarm.FIELD_USER_EMAIL, mFbAuth.getCurrentUser().getEmail())
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    // TODO - determine if this is even necessary with both apps being on the same phone ...
                });

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.i(BaseActivity.LOG_TAG, FbSnapshotListenerService.class.getSimpleName()
                        + " is listening for updates... ");
            }
        }, 0, JOB_INTERVAL);
    }

}