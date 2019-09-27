package edu.temple.smartprompter_v2.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.temple.smartprompter_v2.SmartPrompter;
import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.Log;
import edu.temple.sp_res_lib.utils.MediaUtil;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class BaseActivity extends AppCompatActivity {

    protected String mAlarmGUID;
    protected boolean mWakeup, mPlayAlerts;
    protected MediaUtil.AUDIO_TYPE mAlertType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.ui(LOG_TAG, this, "Created");

        mAlarmGUID = getIntent().getStringExtra(Constants.BUNDLE_ARG_ALARM_GUID);
        mWakeup = getIntent().getBooleanExtra(Constants.BUNDLE_ARG_ALARM_WAKEUP, false);
        mPlayAlerts = getIntent().getBooleanExtra(Constants.BUNDLE_ARG_PLAY_ALERTS, false);

        String alertType = getIntent().getStringExtra(Constants.BUNDLE_ARG_ALERT_TYPE);
        mAlertType = (alertType == null || alertType.equals(""))
                ? MediaUtil.AUDIO_TYPE.None : MediaUtil.AUDIO_TYPE.valueOf(alertType);

        if (mWakeup) ((SmartPrompter)getApplicationContext()).wakeup(this, mPlayAlerts, mAlertType);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        Log.ui(LOG_TAG, this, "Paused");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.ui(LOG_TAG, this, "Resumed");
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.ui(LOG_TAG, this, "Stopped");
        Log.dump(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.ui(LOG_TAG, this, "Destroyed");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.ui(LOG_TAG, this, "Back button pressed");
        super.onBackPressed();
    }

}
