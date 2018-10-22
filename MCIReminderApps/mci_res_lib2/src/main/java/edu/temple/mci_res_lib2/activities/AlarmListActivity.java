package edu.temple.mci_res_lib2.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import edu.temple.mci_res_lib2.utils.Constants;
import edu.temple.mci_res_lib2.alarms.MCIAlarmManager;
import edu.temple.mci_res_lib2.R;
import edu.temple.mci_res_lib2.utils.SimpleItemRecyclerViewAdapter;

import static edu.temple.mci_res_lib2.utils.Constants.CAN_READ_EXTERNAL_CODE;
import static edu.temple.mci_res_lib2.utils.Constants.CAN_USE_CAMERA_CODE;
import static edu.temple.mci_res_lib2.utils.Constants.CAN_USE_VIBRATE_CODE;
import static edu.temple.mci_res_lib2.utils.Constants.CAN_WRITE_EXTERNAL_CODE;

public class AlarmListActivity extends AppCompatActivity {

    private static boolean CAN_USE_CAMERA = false;
    private static boolean CAN_USE_VIBRATE = false;
    private static boolean CAN_READ_EXTERNAL = false;
    private static boolean CAN_WRITE_EXTERNAL = false;

    private static View MAIN_LAYOUT_VIEW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeView();
    }

    private void initializeView() {
        setContentView(R.layout.activity_alarm_list);
        MAIN_LAYOUT_VIEW = findViewById(R.id.mainLayout);

        // if necessary, attempt to retrieve and store the current execution mode
        if (MCIAlarmManager.isDefaultExecMode()) {
            String execModeString = getIntent().getStringExtra(Constants.INTENT_PARAM_EXEC_MODE);
            if (execModeString != null && !execModeString.isEmpty()) {
                MCIAlarmManager.setExecMode(this, Constants.EXEC_MODES.valueOf(execModeString));
            } else MCIAlarmManager.getExecModeFromSharedPrefs();
        } else Log.i(Constants.LOG_TAG, "Launched Alarm List activity with existing exec mode: " + MCIAlarmManager.getExecMode().toString());

        // attempt to populate the activity view ...
        // if not all permissions available, issue the requests and try again
        if (!verifyAllPermissionsGranted()) {
            CAN_USE_CAMERA = requestAppPermissions(Manifest.permission.CAMERA, CAN_USE_CAMERA_CODE);
            CAN_USE_VIBRATE = requestAppPermissions(Manifest.permission.VIBRATE, CAN_USE_VIBRATE_CODE);
            CAN_READ_EXTERNAL = requestAppPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, CAN_READ_EXTERNAL_CODE);
            CAN_WRITE_EXTERNAL = requestAppPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, CAN_WRITE_EXTERNAL_CODE);
            verifyAllPermissionsGranted();
        }
    }

    private boolean requestAppPermissions(String permission, int permissionCode) {
        boolean permissionGranted = (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED);
        if (!permissionGranted) {
            Log.e(Constants.LOG_TAG, "FAILED TO OBTAIN PERMISSION: " + permissionCode);
            ActivityCompat.requestPermissions(this, new String[] { permission }, permissionCode);
        }
        return permissionGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAN_USE_CAMERA_CODE: {
                CAN_USE_CAMERA = checkRequestResult(grantResults, requestCode);
                break;
            }
            case CAN_USE_VIBRATE_CODE: {
                CAN_USE_VIBRATE = checkRequestResult(grantResults, requestCode);
                break;
            }
            case CAN_READ_EXTERNAL_CODE: {
                CAN_READ_EXTERNAL = checkRequestResult(grantResults, requestCode);
                break;
            }
            case CAN_WRITE_EXTERNAL_CODE: {
                CAN_WRITE_EXTERNAL = checkRequestResult(grantResults, requestCode);
                break;
            }
        }
        verifyAllPermissionsGranted();
    }

    private boolean verifyAllPermissionsGranted() {
        // only populate the rest of the view if *ALL* permissions have been satisfied
        if (CAN_USE_CAMERA && CAN_USE_VIBRATE && CAN_READ_EXTERNAL && CAN_WRITE_EXTERNAL) {
            Log.i(Constants.LOG_TAG, "All permissions granted!  Populating Alarm List activity view...");
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            toolbar.setTitle(R.string.app_name);

            final View recyclerView = findViewById(R.id.item_list);
            assert recyclerView != null;
            MCIAlarmManager.initAlarmList(this);
            populateAlarmList(recyclerView);

            final Button saveButton = findViewById(R.id.saveButton);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MCIAlarmManager.saveAlarmListToSharedPrefs(AlarmListActivity.this);
                    MCIAlarmManager.startAlarmsInList(AlarmListActivity.this);
                    showSnackbar("All alarms changes saved.  Click 'Close' to exit.");
                }
            });

            final Button closeButton = findViewById(R.id.closeButton);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            final Button resetButton = findViewById(R.id.resetButton);
            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MCIAlarmManager.clearAlarmListFromSharedPrefs(AlarmListActivity.this);
                    MCIAlarmManager.resetAlarmList(AlarmListActivity.this);
                    populateAlarmList(recyclerView);
                    showSnackbar("All alarms reset.  Click 'Save' to commit.");
                }
            });
            return true;
        } else {
            Log.e(Constants.LOG_TAG, "Missing permissions ... have you requested them from the user?");
            return false;
        }
    }

    private boolean checkRequestResult(int[] grantResults, int requestCode) {
        boolean result = (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
        if (result) Log.i(Constants.LOG_TAG, "Had to manually request permission for: " + requestCode + ", but we got there in the end.");
        else Log.e(Constants.LOG_TAG, "REQUESTED PERMISSION: " + requestCode + ", AND STILL COULD NOT OBTAIN.");
        return result;
    }

    private void populateAlarmList(View recyclerView) {
        SimpleItemRecyclerViewAdapter adapter = new SimpleItemRecyclerViewAdapter(MCIAlarmManager.getAlarmList());
        ((RecyclerView) recyclerView).setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void showSnackbar(String message) {
        Snackbar mySnackbar = Snackbar.make(MAIN_LAYOUT_VIEW, message, Snackbar.LENGTH_LONG);
        mySnackbar.show();
    }

}