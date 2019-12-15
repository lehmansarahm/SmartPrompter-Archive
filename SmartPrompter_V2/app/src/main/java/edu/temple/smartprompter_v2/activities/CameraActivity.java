package edu.temple.smartprompter_v2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import edu.temple.smartprompter_v2.R;
import edu.temple.smartprompter_v2.SmartPrompter;
import edu.temple.smartprompter_v2.fragments.CameraPreviewFragment;
import edu.temple.smartprompter_v2.fragments.CameraReviewFragment;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.utils.CameraUtil;
import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.Log;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class CameraActivity extends BaseActivity implements
        CameraUtil.ImageCaptureListener,
        CameraReviewFragment.ImageReviewListener {

    private CameraPreviewFragment previewFrag;
    private CameraReviewFragment reviewFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Log.i(LOG_TAG, "Launching camera preview for alarm GUID: " + mAlarmGUID);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        previewFrag = CameraPreviewFragment.newInstance(mAlarmGUID);
        ft.replace(R.id.camera_container, previewFrag).commit();
    }

    @Override
    public void onImageCaptured(String alarmGUID, byte[] imageBytes) {
        Log.i(LOG_TAG, "User has captured an image for alarm ID: " + alarmGUID);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();     // remove camera preview fragment to avoid conflicts

        FragmentTransaction ft = fragmentManager.beginTransaction();
        reviewFrag = CameraReviewFragment.newInstance(alarmGUID, imageBytes);
        ft.replace(R.id.camera_container, reviewFrag)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onImageAccepted(String alarmGUID, byte[] bytes) {
        Log.i(LOG_TAG, "User has successfully taken and approved a task "
                + "completion picture.  Updating alarm and saving image to storage.");
        SmartPrompter spApp = ((SmartPrompter)getApplication());
        Alarm alarm = spApp.getAlarm(alarmGUID);

        if (alarm == null) {
            Log.e(LOG_TAG, "Can't find matching alarm for GUID: " + alarmGUID);
            return;
        }

        spApp.updateAlarm(alarmGUID, Alarm.STATUS.Complete);
        String photoPath = alarm.getPhotoPath();

        Log.e(LOG_TAG, "Attempting to save photo to path: " + photoPath);
        spApp.saveTaskImage(photoPath, bytes);

        Log.i(LOG_TAG, "Task complete for GUID: " + alarmGUID
                + "  Showing confirmation screen...");

        Intent intent = new Intent(CameraActivity.this, ConfirmationActivity.class);
        intent.putExtra(Constants.BUNDLE_ARG_ALARM_WAKEUP, mWakeup);
        intent.putExtra(Constants.BUNDLE_ARG_ALARM_GUID, mAlarmGUID);
        startActivity(intent);
        finish();
    }

    @Override
    public void onImageRejected(String alarmGUID) {
        Log.i(LOG_TAG, "User has rejected the task completion picture "
                + "they took.  Returning to camera preview fragment ...");
        recreate();
    }

}