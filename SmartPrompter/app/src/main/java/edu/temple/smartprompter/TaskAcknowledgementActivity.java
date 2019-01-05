package edu.temple.smartprompter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import edu.temple.smartprompter.utils.BaseActivity;
import edu.temple.smartprompter.utils.Constants;
import edu.temple.sp_res_lib.Alarm;

public class TaskAcknowledgementActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOG_TAG, "Task Acknowledgement Activity created!");
        setContentView(R.layout.activity_task_acknowledgement);
        super.onCreate(savedInstanceState);

        if (!verifyIntentExtras()) return;
        if (checkPermissions()) {
            initNavigation();
            showDefaultFragment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(Constants.LOG_TAG, "Task Acknowledgement Activity paused!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(Constants.LOG_TAG, "Task Acknowledgement Activity destroyed!");
    }

    protected void showDefaultFragment() {
        // No fragment for this activity ... just updating the alarm status
        // and displaying a static view
        updateAlarmStatus(Alarm.STATUS.Unacknowledged);

        Button acknowledgeButton = findViewById(R.id.acknowledge_button);
        acknowledgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNextActivity(TaskAcknowledgementActivity.this, TaskCompletionActivity.class);
                Log.i(Constants.LOG_TAG, "Received and acknowledged alarm response for alarm ID: "
                        + mAlarm.getID() + ".  \t\t and updated alarm status: " + mAlarm.getStatusString());
            }
        });

        Button reminderButton = findViewById(R.id.remind_button);
        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TaskAcknowledgementActivity.this,
                        "Haven't coded the reminder logic yet.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}