package edu.temple.sp_admin;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import edu.temple.sp_admin.adapters.SimpleAlarmListAdapter;
import edu.temple.sp_admin.fragments.ActiveAlarmListFragment;
import edu.temple.sp_admin.fragments.IncompleteAlarmDetailsFragment;
import edu.temple.sp_admin.fragments.IncompleteAlarmListFragment;
import edu.temple.sp_admin.utils.BaseActivity;
import edu.temple.sp_admin.utils.Constants;

public class IncompleteAlarmsActivity extends BaseActivity implements
        SimpleAlarmListAdapter.AlarmSelectionListener,
        IncompleteAlarmDetailsFragment.AlarmDetailChangeListener {

    private IncompleteAlarmListFragment listFrag;
    private IncompleteAlarmDetailsFragment detailsFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOG_TAG, "Incomplete Alarms Activity created!");
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
        Log.i(Constants.LOG_TAG, "Incomplete Alarms Activity paused!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(Constants.LOG_TAG, "Incomplete Alarms Activity destroyed!");
    }

    protected void showDefaultFragment() {
        Log.i(Constants.LOG_TAG, "Populating Incomplete Alarms Activity with list fragment.");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        listFrag = IncompleteAlarmListFragment.newInstance();
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
        detailsFrag = IncompleteAlarmDetailsFragment.newInstance(alarmID);
        ft.replace(R.id.fragment_container, detailsFrag);
        ft.addToBackStack(null);
        ft.commit();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------
    //      AlarmDetailChangeListener methods
    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onAlarmDetailsChanged() {
        Log.i(Constants.LOG_TAG, "User has made changes to an alarm!  Reloading the "
                + "incomplete alarm list ...");

        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(); // remove alarm details fragment from backstack
        fm.popBackStack(); // remove previous view of alarm list from backstack (keep current)

        FragmentTransaction ft = fm.beginTransaction();
        listFrag = IncompleteAlarmListFragment.newInstance();
        ft.replace(R.id.fragment_container, listFrag);
        ft.addToBackStack(null);
        ft.commit();
    }
}