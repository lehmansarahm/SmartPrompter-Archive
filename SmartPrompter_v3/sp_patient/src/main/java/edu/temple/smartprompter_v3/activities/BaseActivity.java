package edu.temple.smartprompter_v3.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

import edu.temple.smartprompter_v3.SmartPrompter;
import edu.temple.smartprompter_v3.receivers.AlarmAlertReceiver;
import edu.temple.smartprompter_v3.res_lib.SpController;
import edu.temple.smartprompter_v3.res_lib.data.Alarm;
import edu.temple.smartprompter_v3.res_lib.data.FirebaseConnector;
import edu.temple.smartprompter_v3.res_lib.utils.Constants;
import edu.temple.smartprompter_v3.res_lib.utils.FbaEventLogger;
import edu.temple.smartprompter_v3.res_lib.utils.MediaUtil;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String LOG_TAG = "SmartPrompter_v3 Patient";

    public static final Class ALARM_NOTIFICATION_CLASS = LoginActivity.class;
    public static final Class ALARM_RECEIVER_CLASS = AlarmAlertReceiver.class;

    protected static final int PERMISSION_REQUEST_CODE = 429;
    protected static final String[] PERMISSIONS = new String[] {
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.VIBRATE,
            Manifest.permission.CAMERA
    };

    protected static final int REMIND_ME_LATER = 0;
    protected static final int READY_ON_MY_WAY = 2;

    protected FirebaseAuth mFbAuth;
    protected FirebaseAnalytics mFbAnalytics;
    protected FbaEventLogger mFbaEventLogger;

    protected String mAlarmGUID;
    protected boolean mWakeup, mPlayAlerts;
    protected MediaUtil.AUDIO_TYPE mAlertType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mFbAuth = FirebaseAuth.getInstance();
        mFbAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
        mFbaEventLogger = new FbaEventLogger(this);

        mAlarmGUID = getIntent().getStringExtra(Constants.BUNDLE_ARG_ALARM_GUID);
        mWakeup = getIntent().getBooleanExtra(Constants.BUNDLE_ARG_ALARM_WAKEUP, false);
        mPlayAlerts = getIntent().getBooleanExtra(Constants.BUNDLE_ARG_PLAY_ALERTS, false);

        String alertType = getIntent().getStringExtra(Constants.BUNDLE_ARG_ALERT_TYPE);
        mAlertType = (alertType == null || alertType.equals(""))
                ? MediaUtil.AUDIO_TYPE.None : MediaUtil.AUDIO_TYPE.valueOf(alertType);

        if (mWakeup) SmartPrompter.wakeup(this, mPlayAlerts, mAlertType);
        super.onCreate(savedInstanceState);
    }

    protected boolean checkPermissions() {
        boolean permissionsGranted = true;
        for (String permission : PERMISSIONS) {
            permissionsGranted &= ((checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED));
        }

        if (permissionsGranted)
            return true;
        else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
            return false;
        }
    }

}