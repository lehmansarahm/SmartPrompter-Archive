package edu.temple.smartprompter;

import android.os.Bundle;
import edu.temple.sp_res_lib.Alarm;

public class TaskCompletionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_completion);

        if (!verifyIntentExtras()) return;
        updateAlarmStatus(Alarm.STATUS.Incomplete);
    }

}