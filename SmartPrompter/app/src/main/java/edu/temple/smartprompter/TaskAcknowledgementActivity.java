package edu.temple.smartprompter;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import edu.temple.smartprompter.fragments.CameraInstructionFragment;
import edu.temple.smartprompter.fragments.TaskAcknowledgementFragment;
import edu.temple.smartprompter.utils.BaseActivity;
import edu.temple.smartprompter.utils.Constants;
import edu.temple.sp_res_lib.Alarm;

public class TaskAcknowledgementActivity extends BaseActivity implements
        TaskAcknowledgementFragment.TaskAcknowledgementListener {

    private TaskAcknowledgementFragment defaultFrag;

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOG_TAG, "Task Acknowledgement Activity created!");
        setContentView(R.layout.activity_task_acknowledgement);
        super.onCreate(savedInstanceState);

        // make sure we were passed the correct intent extras
        if (!verifyIntentExtras())
            return;

        // cancel any lingering notifications associated with this alarm
        NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        nm.cancel(mAlarmID);

        // proceed with displaying the activity view
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

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    protected void showDefaultFragment() {
        Log.i(Constants.LOG_TAG, "Populating Task Completion Activity with default fragment.");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        defaultFrag = TaskAcknowledgementFragment.newInstance(mAlarmID);
        ft.replace(R.id.fragment_container, defaultFrag);
        ft.commit();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onAlarmAcknowledged(int alarmID) {
        updateAlarmStatus(Alarm.STATUS.Incomplete);
        startNextActivity(this, TaskCompletionActivity.class);
        Log.i(Constants.LOG_TAG, "Received and acknowledged alarm response for alarm ID: "
                + mAlarm.getID() + ".  \t\t and updated alarm status: " + mAlarm.getStatusString());
        finish();
    }

    @Override
    public void onAlarmDeferred(int alarmID) {
        // TODO - finish alarm acknowledgement deferral logic
        Toast.makeText(this,
                "Haven't coded the reminder deferral logic yet.",
                Toast.LENGTH_SHORT).show();
    }

}