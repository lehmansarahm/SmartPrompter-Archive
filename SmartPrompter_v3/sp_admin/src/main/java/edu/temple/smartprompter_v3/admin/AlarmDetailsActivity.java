package edu.temple.smartprompter_v3.admin;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.DocumentReference;

import edu.temple.smartprompter_v3.admin.fragments.AlarmDetailsFragment;
import edu.temple.smartprompter_v3.res_lib.SpController;
import edu.temple.smartprompter_v3.res_lib.data.Alarm;
import edu.temple.smartprompter_v3.res_lib.data.FirebaseConnector;
import edu.temple.smartprompter_v3.res_lib.fragments.CameraPreviewFragment;
import edu.temple.smartprompter_v3.res_lib.fragments.CameraReviewFragment;
import edu.temple.smartprompter_v3.res_lib.fragments.DatePickerFragment;
import edu.temple.smartprompter_v3.res_lib.fragments.TimePickerFragment;
import edu.temple.smartprompter_v3.res_lib.utils.CameraUtil;
import edu.temple.smartprompter_v3.res_lib.utils.Constants;
import edu.temple.smartprompter_v3.res_lib.utils.MediaUtil;
import edu.temple.smartprompter_v3.res_lib.utils.StorageUtil;

import static edu.temple.smartprompter_v3.res_lib.utils.Constants.LOG_TAG;

public class AlarmDetailsActivity extends BaseActivity implements
        DatePickerFragment.DatePickerListener,
        TimePickerFragment.TimePickerListener,
        AlarmDetailsFragment.AlarmDetailsListener,
        CameraUtil.ImageCaptureListener,
        CameraReviewFragment.ImageReviewListener {

    private AlarmDetailsFragment detailsFrag;
    private CameraPreviewFragment previewFrag;
    private CameraReviewFragment reviewFrag;

    private Alarm mAlarm;
    private String mAlarmGUID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_details);

        mAlarmGUID = getIntent().getStringExtra(Constants.BUNDLE_ARG_ALARM_GUID);
        Log.i(LOG_TAG, "Launching details fragment for alarm GUID: " + mAlarmGUID);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        detailsFrag = AlarmDetailsFragment.newInstance(mAlarmGUID);
        ft.replace(R.id.details_container, detailsFrag).commit();
    }


    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------


    @Override
    public void onDateRequested(String alarmGUID, int[] date) {
        DialogFragment newFragment = DatePickerFragment.newInstance(mAlarmGUID, date);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onTimeRequested(String alarmGUID, int[] time) {
        DialogFragment newFragment = TimePickerFragment.newInstance(mAlarmGUID, time);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onImageRequested(String alarmGUID) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        previewFrag = CameraPreviewFragment.newInstance(alarmGUID);
        ft.replace(R.id.details_container, previewFrag)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onAlarmSaved(Alarm alarm) {
        mAlarm = alarm;
        mFbaEventLogger.buttonClick(this.getClass(), "Save", mAlarm);
        if (mAlarm.hasAlarmTimePassed()) {
            Log.e(Constants.LOG_TAG, "Cannot set alarm for time in the past!");
            Toast.makeText(AlarmDetailsActivity.this, "Cannot set alarm for time "
                    + "in the past!", Toast.LENGTH_LONG).show();
        } else {
            Log.i(LOG_TAG, "Saving updates to record with GUID: " + mAlarm.getGuid());
            mAlarm.updateUserEmail(mFbAuth.getCurrentUser().getEmail());
            mAlarm.updateStatus(Alarm.STATUS.Active);

            if (mAlarm.getGuid().equals(Constants.DEFAULT_ALARM_GUID)) {
                FirebaseConnector.saveNewAlarm(mAlarm,
                        (result) -> ((DocumentReference)result).get().addOnCompleteListener(
                                task -> {
                                    if (task.isSuccessful())
                                        alertPatientApp(new Alarm(task.getResult()));
                                    else alertFailure(mAlarm, task.getException());
                                }), (error) -> alertFailure(mAlarm, error));
            } else {
                FirebaseConnector.saveAlarm(mAlarm,
                        (result) -> {
                            if (result.isSuccessful()) alertPatientApp(mAlarm);
                            else alertFailure(mAlarm, result.getException());
                        }, (error) -> alertFailure(mAlarm, error));
            }

        }
    }

    @Override
    public void onAlarmDeleted(Alarm alarm) {
        mAlarm = alarm;
        mFbaEventLogger.buttonClick(this.getClass(), "Delete", mAlarm);
        FirebaseConnector.deleteAlarm(mAlarm.getGuid(),
                (error) -> Log.e(BaseActivity.LOG_TAG, "Something went wrong while "
                        + "attempting to save alarm record for GUID: " + mAlarm.getGuid(), error));

        Intent intent = new Intent(AlarmDetailsActivity.this, AlarmListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------
    //      DatePickerListener, TimePickerListener methods
    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onDatePicked(String alarmGuid, int year, int month, int day) {
        detailsFrag.onDatePicked(alarmGuid, year, month, day);
    }

    @Override
    public void onTimePicked(String alarmGuid, int hourOfDay, int minute) {
        detailsFrag.onTimePicked(alarmGuid, hourOfDay, minute);
    }


    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------


    @Override
    public void onImageCaptured(String alarmGUID, byte[] imageBytes) {
        Log.i(LOG_TAG, "User has captured an image for alarm ID: " + alarmGUID);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();     // remove camera preview fragment to avoid conflicts

        FragmentTransaction ft = fragmentManager.beginTransaction();
        reviewFrag = CameraReviewFragment.newInstance(alarmGUID, imageBytes);
        ft.replace(R.id.details_container, reviewFrag)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onImageAccepted(String alarmGUID, byte[] bytes) {
        Log.i(LOG_TAG, "Updates saved to alarm with GUID: " + mAlarmGUID
                + " \t\t Popping fragment stack.");

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();     // remove camera review fragment

        detailsFrag.onImageAccepted(alarmGUID, bytes);
    }

    @Override
    public void onImageRejected(String alarmGUID) {
        Log.i(LOG_TAG, "User has rejected the task completion picture "
                + "they took.  Returning to camera preview fragment ...");
        recreate();
    }


    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------


    private void alertPatientApp(Alarm alarm) {
        Log.i(LOG_TAG, "Sending broadcast to patient application with "
                + "GUID: " + alarm.getGuid());

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(getString(R.string.event_alarms_ready));
        broadcastIntent.putExtra(Constants.BUNDLE_ARG_ALARM_GUID, alarm.getGuid());
        broadcastIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        broadcastIntent.setComponent(
                new ComponentName("edu.temple.smartprompter_v3",
                        "edu.temple.smartprompter_v3.receivers.AdminAlertReceiver"));
        sendBroadcast(broadcastIntent);

        Log.i(LOG_TAG, "Returning to alarm list activity.");
        startActivity(new Intent(AlarmDetailsActivity.this,
                AlarmListActivity.class));
        finish();
    }

    private void alertFailure(Alarm mAlarm, Exception ex) {
        Log.e(BaseActivity.LOG_TAG, "Something went wrong while attempting to "
                + "save alarm record for GUID: " + mAlarm.getGuid(), ex);
        Toast.makeText(AlarmDetailsActivity.this,
                "Something went wrong while attempting to save alarm "
                        + "record.  Please try again.", Toast.LENGTH_LONG).show();
    }

}