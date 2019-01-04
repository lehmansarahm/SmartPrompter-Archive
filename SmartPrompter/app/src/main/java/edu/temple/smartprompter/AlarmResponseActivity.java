package edu.temple.smartprompter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import edu.temple.smartprompter.utils.Constants;
import edu.temple.sp_res_lib.Alarm;

public class AlarmResponseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_response);

        if (!verifyIntentExtras()) return;
        updateAlarmStatus(Alarm.STATUS.Unacknowledged);

        initTaskCompletion();
        initTaskDeferral();
    }

    private void initTaskCompletion() {
        Button acknowledgeButton = findViewById(R.id.acknowledge_button);
        acknowledgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNextActivity(AlarmResponseActivity.this, TaskCompletionActivity.class);
                Log.i(Constants.LOG_TAG, "Received and acknowledged alarm response for alarm ID: "
                        + mAlarm.getID() + ".  \t\t and updated alarm status: " + mAlarm.getStatus());
            }
        });
    }

    private void initTaskDeferral() {
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