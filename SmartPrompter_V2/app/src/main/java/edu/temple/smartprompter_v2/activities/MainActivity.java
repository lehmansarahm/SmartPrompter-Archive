package edu.temple.smartprompter_v2.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import edu.temple.smartprompter_v2.R;
import edu.temple.smartprompter_v2.SmartPrompter;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.smartprompter_v2.fragments.AlarmListFragment;
import edu.temple.smartprompter_v2.fragments.ClockFragment;
import edu.temple.smartprompter_v2.fragments.EmptyAlarmListFragment;
import edu.temple.smartprompter_v2.fragments.MissingPermissionsFragment;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class MainActivity extends AppCompatActivity implements AlarmListFragment.OnListItemSelectionListener {

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

        showClockFragment();
        if (checkPermissions()) showDefaultFragment();
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

    protected void showClockFragment() {
        Log.i(LOG_TAG, "Populating current activity with clock fragment.");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ClockFragment fragment = new ClockFragment();
        ft.replace(R.id.clock_container, fragment);
        ft.commit();
    }

    protected void showDefaultFragment() {
        Log.i(LOG_TAG, "We have all required permissions!  Determining "
                + "whether there are alarms to show ...");

        mActiveAlarms = ((SmartPrompter) getApplication()).getAlarms();
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

    protected void showMissingPermissionsFragment() {
        Log.i(LOG_TAG, "Populating current activity with missing-permissions fragment.");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        MissingPermissionsFragment fragment = new MissingPermissionsFragment();
        ft.replace(R.id.alarm_container, fragment);
        ft.commit();
    }

    public void OnListItemSelected(Alarm item) {
        Log.i(LOG_TAG, "List item selected!  Item Number: " + item.getID());
        startActivity(new Intent(MainActivity.this,
                AcknowledgmentActivity.class));
    }

}