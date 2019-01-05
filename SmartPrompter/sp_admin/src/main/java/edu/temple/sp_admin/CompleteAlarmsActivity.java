package edu.temple.sp_admin;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import edu.temple.sp_admin.adapters.SimpleAlarmListAdapter;
import edu.temple.sp_admin.fragments.CompleteAlarmDetailsFragment;
import edu.temple.sp_admin.fragments.CompleteAlarmListFragment;
import edu.temple.sp_admin.utils.BaseActivity;
import edu.temple.sp_admin.utils.Constants;

public class CompleteAlarmsActivity extends BaseActivity implements
        SimpleAlarmListAdapter.AlarmSelectionListener {

    private CompleteAlarmListFragment listFrag;
    private CompleteAlarmDetailsFragment detailsFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOG_TAG, "Complete Alarms Activity created!");
        setContentView(R.layout.activity_active_alarms);
        super.onCreate(savedInstanceState);

        if (checkPermissions()) {
            initNavigation();
            showDefaultFragment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(Constants.LOG_TAG, "Complete Alarms Activity paused!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(Constants.LOG_TAG, "Complete Alarms Activity destroyed!");
    }

    protected void showDefaultFragment() {
        Log.i(Constants.LOG_TAG, "Populating Complete Alarms Activity with list fragment.");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        listFrag = CompleteAlarmListFragment.newInstance();
        ft.replace(R.id.fragment_container, listFrag);
        ft.commit();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------
    //      AlarmSelectionListener methods
    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onAlarmSelected(int alarmID) {
        Log.i(Constants.LOG_TAG, "User wants to view details of alarm ID: " + alarmID);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        detailsFrag = CompleteAlarmDetailsFragment.newInstance(alarmID);
        ft.replace(R.id.fragment_container, detailsFrag);
        ft.addToBackStack(null);
        ft.commit();
    }

}