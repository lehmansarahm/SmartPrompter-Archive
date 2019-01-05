package edu.temple.smartprompter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import edu.temple.smartprompter.utils.BaseActivity;
import edu.temple.smartprompter.utils.Constants;
import edu.temple.sp_res_lib.Alarm;

public class TaskCompletionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOG_TAG, "Task Completion Activity created!");
        setContentView(R.layout.activity_task_completion);
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
        Log.i(Constants.LOG_TAG, "Task Completion Activity paused!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(Constants.LOG_TAG, "Task Completion Activity destroyed!");
    }

    protected void showDefaultFragment() {
        // No fragment for this activity ... just updating the alarm status
        // and displaying a static view
        updateAlarmStatus(Alarm.STATUS.Incomplete);

        Button completeButton = findViewById(R.id.complete_button);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(Constants.LOG_TAG, "REPLACE THIS WITH *ACTUAL* TASK COMPLETION LOGIC.");
                Toast.makeText(TaskCompletionActivity.this,
                        "REPLACE THIS WITH *ACTUAL* TASK COMPLETION LOGIC.",
                        Toast.LENGTH_SHORT).show();
                updateAlarmStatus(Alarm.STATUS.Complete);
            }
        });
    }

}