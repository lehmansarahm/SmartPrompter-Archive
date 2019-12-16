package edu.temple.smartprompter_v3.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import edu.temple.smartprompter_v3.R;
import edu.temple.smartprompter_v3.res_lib.SpController;
import edu.temple.smartprompter_v3.res_lib.data.Alarm;
import edu.temple.smartprompter_v3.res_lib.data.FirebaseConnector;
import edu.temple.smartprompter_v3.res_lib.fragments.CameraPreviewFragment;
import edu.temple.smartprompter_v3.res_lib.fragments.CameraReviewFragment;
import edu.temple.smartprompter_v3.res_lib.utils.CameraUtil;
import edu.temple.smartprompter_v3.res_lib.utils.Constants;

public class CameraActivity extends BaseActivity implements
        CameraUtil.ImageCaptureListener,
        CameraReviewFragment.ImageReviewListener {

    private CameraPreviewFragment previewFrag;
    private CameraReviewFragment reviewFrag;
    private Alarm mAlarm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

        FirebaseConnector.getAlarmByGuid(mAlarmGUID, result -> {
            mAlarm = (Alarm)result;
            if (mAlarm == null) {
                Toast.makeText(CameraActivity.this, "Something went wrong!  "
                        + "Please try again.", Toast.LENGTH_LONG).show();
                Log.e(LOG_TAG, "Can't find matching alarm for GUID: " + alarmGUID);
                recreate();
            } else {
                SpController.markCompleted(this, mAlarm,
                        BaseActivity.ALARM_RECEIVER_CLASS, bytes);
                Log.i(LOG_TAG, "Task complete for GUID: " + alarmGUID
                        + "  Showing confirmation screen...");

                Intent intent = new Intent(CameraActivity.this, ConfirmationActivity.class);
                intent.putExtra(Constants.BUNDLE_ARG_ALARM_WAKEUP, mWakeup);
                intent.putExtra(Constants.BUNDLE_ARG_ALARM_GUID, mAlarmGUID);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onImageRejected(String alarmGUID) {
        Log.i(LOG_TAG, "User has rejected the task completion picture "
                + "they took.  Returning to camera preview fragment ...");
        recreate();
    }

}