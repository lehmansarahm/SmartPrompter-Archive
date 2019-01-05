package edu.temple.smartprompter;

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
        super.onPause();
        Log.i(Constants.LOG_TAG, "Task Completion Activity paused!");
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
    public void onImageCaptured(int alarmID) {
        // TODO - forward image to review fragment
        // TODO - load review fragment
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------
    //      ImageReviewListener methods
    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onImageAccepted(int alarmID) {
        // TODO - save image to public directory
        // TODO - update alarm record (time completed, completion file name, status)
        // TODO - close this fragment ... reload active alarms list
    }

    @Override
    public void onImageRejected(int alarmID) {
        // TODO - pop review fragment ... return to preview fragment
    }

}