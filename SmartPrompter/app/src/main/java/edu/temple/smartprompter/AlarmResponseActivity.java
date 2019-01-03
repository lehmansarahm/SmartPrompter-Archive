package edu.temple.smartprompter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import edu.temple.smartprompter.utils.Constants;
import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.SpAlarmManager;

public class AlarmResponseActivity extends AppCompatActivity {

    private SpAlarmManager mAlarmMgr;
    private Alarm mAlarm;
    private int mAlarmID;
    private String mAlarmStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_response);

        if (!getIntent().hasExtra(Constants.INTENT_EXTRA_ALARM_ID)) {
            Log.e(Constants.LOG_TAG, "Alarm response has been initiated, "
                    + "but intent was missing the alarm ID.");
            return;
        }

        if (!getIntent().hasExtra(Constants.INTENT_EXTRA_ALARM_CURRENT_STATUS)) {
            Log.e(Constants.LOG_TAG, "Alarm response has been initiated, "
                    + "but intent was missing the alarm's current status.");
            return;
        }

        // parse out the intent extras
        mAlarmID = getIntent().getIntExtra(Constants.INTENT_EXTRA_ALARM_ID,
                Constants.DEFAULT_ALARM_ID);
        mAlarmStatus = getIntent().getStringExtra(Constants.INTENT_EXTRA_ALARM_CURRENT_STATUS);
        Log.i(Constants.LOG_TAG, "Received alarm response request for alarm with ID: "
                + mAlarmID + " \t\t and original alarm status: " + mAlarmStatus);

        // retrieve alarm record
        mAlarmMgr = new SpAlarmManager(this);
        mAlarm = mAlarmMgr.get(mAlarmID);
        mAlarm.updateStatus(Alarm.STATUS.Unacknowledged);
        mAlarmMgr.update(mAlarm);

        // just for sanity's sake ...
        mAlarm = mAlarmMgr.get(mAlarmID);
        Log.i(Constants.LOG_TAG, "Received and acknowledged alarm response for alarm ID: "
                + mAlarm.getID() + ".  \t\t and updated alarm status: " + mAlarm.getStatus());

        // set up the acknowledgement logic
        Button acknowledgeButton = findViewById(R.id.acknowledge_button);
        acknowledgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update the alarm with the next status
                mAlarm.updateStatus(Alarm.STATUS.Incomplete);
                mAlarmMgr.update(mAlarm);
                Toast.makeText(AlarmResponseActivity.this,
                        "Haven't coded the rest of the task logic yet.",
                        Toast.LENGTH_SHORT).show();
                Log.i(Constants.LOG_TAG, "Received and acknowledged alarm response for alarm ID: "
                        + mAlarm.getID() + ".  \t\t and updated alarm status: " + mAlarm.getStatus());
            }
        });

        // set up the reminder logic
        Button reminderButton = findViewById(R.id.remind_button);
        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AlarmResponseActivity.this,
                        "Haven't coded the reminder logic yet.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}