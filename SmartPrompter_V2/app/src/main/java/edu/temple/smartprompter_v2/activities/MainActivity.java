package edu.temple.smartprompter_v2.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import edu.temple.smartprompter_v2.R;
import edu.temple.smartprompter_v2.SmartPrompter;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.smartprompter_v2.fragments.AlarmListFragment;
import edu.temple.smartprompter_v2.fragments.ClockFragment;
import edu.temple.smartprompter_v2.fragments.EmptyAlarmListFragment;
import edu.temple.smartprompter_v2.fragments.MissingPermissionsFragment;
import edu.temple.sp_res_lib.utils.Constants;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class MainActivity extends BaseActivity implements AlarmListFragment.OnListItemSelectionListener {

    private static final int PERMISSION_REQUEST_CODE = 327;
    private static final String[] PERMISSIONS = new String[] {
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.CAMERA,
            Manifest.permission.SET_ALARM,
            Manifest.permission.VIBRATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.FOREGROUND_SERVICE
    };

    ArrayList<Alarm> mActiveAlarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(LOG_TAG, "Main activity created!");

        showClockFragment();
        if (checkPermissions()) showDefaultFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "Main activity resumed!");
        Intent intent = getIntent();

        if (intent.hasExtra(Constants.BUNDLE_REMIND_ME_LATER_ACK)) {
            if (intent.getBooleanExtra(Constants.BUNDLE_REMIND_ME_LATER_ACK, false)) {
                Log.e(LOG_TAG, "User has chosen to 'snooze' acknowledgment phase of task alarm!");
                Toast.makeText(this, "Acknowledgment reminder set!",
                        Toast.LENGTH_LONG).show();
            }
        }

        if (intent.hasExtra(Constants.BUNDLE_REMIND_ME_LATER_COMP)) {
            if (intent.getBooleanExtra(Constants.BUNDLE_REMIND_ME_LATER_COMP, false)) {
                Log.e(LOG_TAG, "User has chosen to 'snooze' completion phase of task alarm!");
                Toast.makeText(this, "Completion reminder set!",
                        Toast.LENGTH_LONG).show();
            }
        }

        if (intent.hasExtra(Constants.BUNDLE_TASK_COMPLETE)) {
            if (intent.getBooleanExtra(Constants.BUNDLE_TASK_COMPLETE, false)) {
                Log.i(LOG_TAG, "User has completed an alarm task!");
                Toast.makeText(this, "Task complete! Great work!",
                        Toast.LENGTH_LONG).show();
                showDefaultFragment(true);
            }
        }
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    protected boolean checkPermissions() {
        boolean permissionsGranted = true;
        for (String permission : PERMISSIONS) {
            permissionsGranted &=
                    (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }

        if (permissionsGranted)
            return true;

        ActivityCompat.requestPermissions(this, PERMISSIONS,
                PERMISSION_REQUEST_CODE);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showDefaultFragment();
                } else {
                    showMissingPermissionsFragment();
                }
            }
        }
    }

    public void OnListItemSelected(Alarm item) {
        // cancel any latent alarms for this record
        ((SmartPrompter)getApplicationContext()).cancelAlarm(item);

        // select response activity according to the current record status
        Intent intent;
        if (item.getStatus().equals(Alarm.STATUS.Incomplete)) {
            Log.i(LOG_TAG, "List item selected!  Launching completion activity for alarm: "
                    + item.getGuid());
            intent = new Intent(MainActivity.this, CompletionActivity.class);
        } else {
            Log.i(LOG_TAG, "List item selected!  Launching acknowledgment activity for alarm: "
                    + item.getGuid());
            intent = new Intent(MainActivity.this, AcknowledgmentActivity.class);
        }

        // launch the response activity
        intent.putExtra(Constants.BUNDLE_ARG_ALARM_GUID, item.getGuid());
        startActivity(intent);
        finish();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void showClockFragment() {
        Log.i(LOG_TAG, "Populating current activity with clock fragment.");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ClockFragment fragment = new ClockFragment();
        ft.replace(R.id.clock_container, fragment);
        ft.commit();
    }

    private void showDefaultFragment() {
        showDefaultFragment(false);
    }

    private void showDefaultFragment(boolean refreshFromStorage) {
        Log.i(LOG_TAG, "We have all required permissions!  Determining "
                + "whether there are alarms to show ...");

        mActiveAlarms = ((SmartPrompter) getApplication()).getAlarms(refreshFromStorage);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (mActiveAlarms != null && mActiveAlarms.size() > 0) {
            Log.i(LOG_TAG, "Populating current activity with alarm-list fragment.");
            AlarmListFragment fragment = AlarmListFragment.newInstance(mActiveAlarms);
            ft.replace(R.id.alarm_container, fragment);
        } else {
            Log.i(LOG_TAG, "Populating current activity with empty-list fragment.");
            EmptyAlarmListFragment fragment = new EmptyAlarmListFragment();
            ft.replace(R.id.alarm_container, fragment);
        }

        ft.commit();
    }

    private void showMissingPermissionsFragment() {
        Log.i(LOG_TAG, "Populating current activity with missing-permissions fragment.");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        MissingPermissionsFragment fragment = new MissingPermissionsFragment();
        ft.replace(R.id.alarm_container, fragment);
        ft.commit();
    }

}