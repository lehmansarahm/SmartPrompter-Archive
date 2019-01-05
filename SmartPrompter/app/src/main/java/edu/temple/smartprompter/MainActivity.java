package edu.temple.smartprompter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import edu.temple.smartprompter.adapters.SimpleAlarmListAdapter;
import edu.temple.smartprompter.utils.Constants;
import edu.temple.sp_res_lib.SpAlarmManager;

public class MainActivity extends AppCompatActivity implements
        SimpleAlarmListAdapter.AlarmDetailsListener {

    private static final int PERMISSION_REQUEST_CODE = 201;
    private static final String[] PERMISSIONS = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mEmptyView;

    private SpAlarmManager mAlarmMgr;

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOG_TAG, "Main Activity created!");
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        checkPermissions();
    }

    @Override
    public void onDestroy() {
        Log.i(Constants.LOG_TAG, "Main Activity destroyed!");
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showDefaultView();
                } else {
                    Log.i(Constants.LOG_TAG, "Insufficient permissions.  "
                            + "Redirecting to Missing Permissions view.");
                    Intent intent = new Intent(this,
                            MissingPermissionsActivity.class);
                    startActivity(intent);
                }
            }
        }
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    protected void showDefaultView() {
        mAlarmMgr = new SpAlarmManager(this);

        mEmptyView = findViewById(R.id.empty_view);
        mRecyclerView = findViewById(R.id.active_alarm_recycler);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new SimpleAlarmListAdapter(mAlarmMgr.getAll(), this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        checkDatasetVisibility();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onAlarmSelected(int alarmID) {
        Intent intent = new Intent(this, AlarmDetailsActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_ALARM_ID, alarmID);
        startActivity(intent);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void checkPermissions() {
        boolean permissionsGranted = true;
        for (String permission : PERMISSIONS) {
            permissionsGranted &=
                    (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }

        if (permissionsGranted) {
            showDefaultView();
        } else {
            ActivityCompat.requestPermissions(this,
                    PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }

    private void checkDatasetVisibility() {
        if (!mAlarmMgr.areAlarmsAvailable()) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}