package edu.temple.smartprompter;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import edu.temple.smartprompter.fragments.CameraInstructionFragment;
import edu.temple.smartprompter.utils.BaseActivity;
import edu.temple.smartprompter.utils.Constants;

public class TaskCompletionActivity extends BaseActivity {

    private CameraInstructionFragment defaultFrag;

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
        Log.i(Constants.LOG_TAG, "Populating Task Completion Activity with default fragment.");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        defaultFrag = CameraInstructionFragment.newInstance(mAlarmID);
        ft.replace(R.id.fragment_container, defaultFrag);
        ft.commit();
    }

}