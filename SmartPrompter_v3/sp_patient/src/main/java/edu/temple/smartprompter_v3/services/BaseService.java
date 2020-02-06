package edu.temple.smartprompter_v3.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import edu.temple.smartprompter_v3.R;
import edu.temple.smartprompter_v3.activities.LoginActivity;

public abstract class BaseService extends Service {

    protected static final String CHANNEL_ID = "SmartPrompterNotificationChannel";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Notification notification = createNotification();
        startForeground(1, notification);
        doWork(intent);
        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID,
                "SmartPrompter Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) manager.createNotificationChannel(serviceChannel);
    }

    protected Notification createNotification() {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, LoginActivity.class), 0);
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getNotificationTitle())
                .setContentText(getNotificationText())
                .setSmallIcon(R.drawable.alarm_clock_black)
                .setContentIntent(pendingIntent)
                .build();
    }

    protected abstract String getNotificationTitle();
    protected abstract String getNotificationText();
    protected abstract void doWork(Intent intent);

}
