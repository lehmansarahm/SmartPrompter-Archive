package edu.temple.smartprompter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import edu.temple.smartprompter.utils.Constants;
import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.SpAlarmManager;

public class BaseActivity extends AppCompatActivity {

    protected SpAlarmManager mAlarmMgr;
    protected Alarm mAlarm;
    protected int mAlarmID;
    protected String mAlarmStatus;

    protected boolean verifyIntentExtras() {
        if (!getIntent().hasExtra(Constants.INTENT_EXTRA_ALARM_ID)) {
            Log.e(Constants.LOG_TAG, "Alarm response has been initiated, "
                    + "but intent was missing the alarm ID.");
            return false;
        }

        if (!getIntent().hasExtra(Constants.INTENT_EXTRA_ALARM_CURRENT_STATUS)) {
            Log.e(Constants.LOG_TAG, "Alarm response has been initiated, "
                    + "but intent was missing the alarm's current status.");
            return false;
        }

        // parse out the intent extras
        mAlarmID = getIntent().getIntExtra(Constants.INTENT_EXTRA_ALARM_ID,
                Constants.DEFAULT_ALARM_ID);
        mAlarmStatus = getIntent().getStringExtra(Constants.INTENT_EXTRA_ALARM_CURRENT_STATUS);
        Log.i(Constants.LOG_TAG, "Received alarm response request for alarm with ID: "
                + mAlarmID + " \t\t and original alarm status: " + mAlarmStatus);

        return true;
    }

    protected void updateAlarmStatus(Alarm.STATUS newStatus) {
        // retrieve alarm record
        mAlarmMgr = new SpAlarmManager(this);
        mAlarm = mAlarmMgr.get(mAlarmID);
        mAlarm.updateStatus(newStatus);
        mAlarmMgr.update(mAlarm);

        // just for sanity's sake ...
        mAlarm = mAlarmMgr.get(mAlarmID);
        Log.i(Constants.LOG_TAG, "Received and acknowledged alarm response for alarm ID: "
                + mAlarm.getID() + ".  \t\t and updated alarm status: " + mAlarm.getStatus());
    }

    protected void startNextActivity(Context origContext, Class nextActClass) {
        Intent intent = new Intent(origContext, nextActClass);
        intent.putExtra(Constants.INTENT_EXTRA_ALARM_ID, mAlarmID);
        intent.putExtra(Constants.INTENT_EXTRA_ALARM_CURRENT_STATUS, mAlarmStatus);
        startActivity(intent);
    }

}