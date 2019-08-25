package edu.temple.smartprompter_v2.activities;

import android.content.Intent;
import android.graphics.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import edu.temple.smartprompter_v2.R;
import edu.temple.smartprompter_v2.fragments.CameraPreviewFragment;
import edu.temple.smartprompter_v2.fragments.CameraReviewFragment;
import edu.temple.sp_res_lib.utils.Constants;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class CameraActivity extends AppCompatActivity implements
        CameraPreviewFragment.ImageCaptureListener,
        CameraReviewFragment.ImageReviewListener {

    private CameraPreviewFragment previewFrag;
    private CameraReviewFragment reviewFrag;
    private String mAlarmGUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mAlarmGUID = getIntent().getStringExtra(Constants.BUNDLE_ARG_ALARM_GUID);

        Log.i(LOG_TAG, "Populating camera fragment...");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        previewFrag = CameraPreviewFragment.newInstance(mAlarmGUID);
        ft.replace(R.id.camera_container, previewFrag)
                .addToBackStack(null)
                .commit();
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
        // TODO - save image for real
        // TODO - finalize alarm for real, redirect to home screen
        startActivity(new Intent(CameraActivity.this,
                MainActivity.class));
    }

    @Override
    public void onImageRejected(String alarmGUID) {
        Log.i(LOG_TAG, "User has rejected the task completion picture "
                + "they took.  Returning to camera preview fragment ...");

        // TODO - debug 'reject image' logic ... doesn't seem to be working
        getSupportFragmentManager().popBackStack(); // return to camera preview screen
    }

}