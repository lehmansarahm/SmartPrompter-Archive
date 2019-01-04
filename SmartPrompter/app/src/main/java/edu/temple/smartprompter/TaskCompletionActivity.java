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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_completion);

        if (!verifyIntentExtras()) return;
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