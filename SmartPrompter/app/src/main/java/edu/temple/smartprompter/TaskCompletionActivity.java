package edu.temple.smartprompter;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import edu.temple.smartprompter.fragments.CameraInstructionFragment;
import edu.temple.smartprompter.fragments.CameraPreviewFragment;
import edu.temple.smartprompter.fragments.CameraReviewFragment;
import edu.temple.smartprompter.utils.BaseActivity;
import edu.temple.smartprompter.utils.Constants;

import edu.temple.sp_res_lib.Reminder;
import edu.temple.sp_res_lib.SpAlarmManager;
import edu.temple.sp_res_lib.SpMediaManager;
import edu.temple.sp_res_lib.SpReminderManager;
import edu.temple.sp_res_lib.utils.Constants.ALARM_STATUS;
import edu.temple.sp_res_lib.utils.Constants.REMINDER_TYPE;

public class TaskCompletionActivity extends BaseActivity implements
        CameraInstructionFragment.ImageAcknowledgementListener,
        CameraPreviewFragment.ImageCaptureListener,
        CameraReviewFragment.ImageReviewListener {

    private CameraInstructionFragment defaultFrag;
    private CameraPreviewFragment previewFrag;
    private CameraReviewFragment reviewFrag;

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOG_TAG, "Task Completion Activity created!");
        setContentView(R.layout.activity_task_completion);
        super.onCreate(savedInstanceState);

        // make sure we were passed the correct intent extras
        if (!verifyIntentExtras())
            return;

        // retrieve the current alarm
        mAlarmMgr = new SpAlarmManager(this);
        mAlarm = mAlarmMgr.get(mAlarmID);
        if (mAlarm == null) {
            Log.e(Constants.LOG_TAG, "NO MATCHING RECORD FOR PROVIDED ALARM ID.");

            DialogInterface.OnClickListener missingAlarmListener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    TaskCompletionActivity.this.finishAndRemoveTask();
                }};

            new AlertDialog.Builder(this)
                    .setTitle("Missing Alarm")
                    .setMessage("The alarm you are trying to access no longer exists.  "
                            + "Please check with your caretaker to verify.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.ok, missingAlarmListener).show();
        } else {
            // cancel any lingering notifications associated with this alarm
            NotificationManager nm = getSystemService(NotificationManager.class);
            Log.e(Constants.LOG_TAG, "Cancelling notification with alarm request "
                    + "code: " + mAlarm.getRequestCode());
            nm.cancel(mAlarm.getRequestCode());

            // if reminder has been provided, cancel any lingering
            // notifications associated with it
            if (reminder != null) {
                Log.e(Constants.LOG_TAG, "Cancelling notification with reminder "
                        + "request code: " + reminder.getRequestCode());
                nm.cancel(reminder.getRequestCode());
            }

            // proceed with displaying the activity view
            if (checkPermissions()) {
                initNavigation();
                showDefaultFragment();
            }
        }
    }

    @Override
    public void onPause() {
        Log.i(Constants.LOG_TAG, "Task Completion Activity paused!");
        if (previewFrag != null) previewFrag.pausePreview();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (previewFrag != null) previewFrag.resumePreview();
        Log.i(Constants.LOG_TAG, "Task Completion Activity resumed!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(Constants.LOG_TAG, "Task Completion Activity destroyed!");
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    protected void showDefaultFragment() {
        Log.i(Constants.LOG_TAG, "Populating Task Completion Activity with default fragment.");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        defaultFrag = CameraInstructionFragment.newInstance(mAlarmID);
        ft.replace(R.id.fragment_container, defaultFrag, DEFAULT_FRAGMENT_TAG);
        ft.commit();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------
    //      ImageAcknowledgementListener methods
    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onImageAcknowledged(int alarmID) {
        Log.i(Constants.LOG_TAG, "User is ready to take a picture for alarm ID: " + alarmID);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        previewFrag = CameraPreviewFragment.newInstance(alarmID);
        ft.replace(R.id.fragment_container, previewFrag);
        ft.addToBackStack(null);
        ft.commit();

        Log.i(Constants.LOG_TAG, "Received and acknowledged camera preview "
                + "permission for alarm ID: " + mAlarmID
                + ".  \t\t and current alarm status: " + mAlarmStatus);
    }

    @Override
    public void onImageDeferred(int alarmID) {
        // TODO - finish picture-taking deferral logic
        Toast.makeText(this,
                "Haven't coded the picture-taking deferral logic yet.",
                Toast.LENGTH_SHORT).show();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------
    //      ImageCaptureListener methods
    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onImageCaptured(int alarmID, byte[] bytes) {
        Log.i(Constants.LOG_TAG, "User has captured an image for alarm ID: " + alarmID);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();     // remove camera preview fragment to avoid conflicts

        FragmentTransaction ft = fragmentManager.beginTransaction();
        reviewFrag = CameraReviewFragment.newInstance(alarmID, bytes);
        ft.replace(R.id.fragment_container, reviewFrag);
        ft.addToBackStack(null);
        ft.commit();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------
    //      ImageReviewListener methods
    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onImageAccepted(int alarmID, byte[] bytes) {
        Log.i(Constants.LOG_TAG, "User has successfully taken and approved a task "
                + "completion picture.  Updating alarm and saving image to storage.");

        SpReminderManager remMgr = new SpReminderManager(this);
        Reminder reminder = remMgr.get(alarmID, REMINDER_TYPE.Completion);
        remMgr.cancelReminder(reminder);

        mAlarmMgr = new SpAlarmManager(this);
        mAlarm = mAlarmMgr.get(alarmID);
        mAlarm.updateTimeCompleted();
        mAlarm.updateStatus(ALARM_STATUS.Complete);
        mAlarmMgr.update(mAlarm);

        String imageID = mAlarm.getCompletionMediaID();
        SpMediaManager mediaMgr = new SpMediaManager(this);
        mediaMgr.saveImage(imageID, bytes);

        Log.i(Constants.LOG_TAG, "Redirect to ActiveAlarmsActivity using new Android "
                + "task so that task completion fragments aren't on backstack..");
        Intent intent = new Intent(this, ActiveAlarmsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onImageRejected(int alarmID) {
        Log.i(Constants.LOG_TAG, "User has rejected the task completion picture "
                + "they took.  Returning to camera preview fragment ...");
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(); // return to camera preview screen
    }

}