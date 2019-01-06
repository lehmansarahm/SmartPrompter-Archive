package edu.temple.smartprompter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import edu.temple.smartprompter.fragments.CameraInstructionFragment;
import edu.temple.smartprompter.fragments.CameraPreviewFragment;
import edu.temple.smartprompter.fragments.CameraReviewFragment;
import edu.temple.smartprompter.utils.BaseActivity;
import edu.temple.smartprompter.utils.Constants;
import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.SpAlarmManager;
import edu.temple.sp_res_lib.SpMediaManager;

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

        if (!verifyIntentExtras()) return;
        if (checkPermissions()) {
            initNavigation();
            showDefaultFragment();
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
        ft.replace(R.id.fragment_container, defaultFrag);
        ft.commit();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------
    //      ImageAcknowledgementListener methods
    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onImageAcknowledged(int alarmID) {
        Log.i(Constants.LOG_TAG, "User wants to view details of alarm ID: " + alarmID);
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
        Log.i(Constants.LOG_TAG, "User wants to view details of alarm ID: " + alarmID);
        FragmentManager fragmentManager = getSupportFragmentManager();
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
        mAlarmMgr = new SpAlarmManager(this);
        mAlarm = mAlarmMgr.get(alarmID);
        mAlarm.updateTimeCompleted();
        mAlarm.updateStatus(Alarm.STATUS.Complete);
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
        // TODO - pop review fragment ... return to preview fragment
        Log.i(Constants.LOG_TAG, "User has rejected the task completion picture "
                + "they took.  Returning to camera preview fragment ...");
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(); // return to camera preview screen
    }

}