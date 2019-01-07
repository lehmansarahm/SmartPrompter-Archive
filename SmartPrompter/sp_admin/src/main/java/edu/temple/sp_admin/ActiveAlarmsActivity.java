package edu.temple.sp_admin;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import edu.temple.sp_admin.adapters.SimpleAlarmListAdapter;
import edu.temple.sp_admin.fragments.ActiveAlarmDetailsFragment;
import edu.temple.sp_admin.fragments.ActiveAlarmListFragment;
import edu.temple.sp_admin.fragments.DatePickerFragment;
import edu.temple.sp_admin.fragments.TimePickerFragment;
import edu.temple.sp_admin.utils.BaseActivity;
import edu.temple.sp_admin.utils.Constants;

public class ActiveAlarmsActivity extends BaseActivity implements
        ActiveAlarmListFragment.AlarmCreationListener,
        SimpleAlarmListAdapter.AlarmSelectionListener,
        DatePickerFragment.DatePickerListener,
        TimePickerFragment.TimePickerListener,
        ActiveAlarmDetailsFragment.AlarmDetailChangeListener {

    private ActiveAlarmListFragment listFrag;
    private ActiveAlarmDetailsFragment detailsFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOG_TAG, "Active Alarms Activity created!");
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
        Log.i(Constants.LOG_TAG, "Active Alarms Activity paused!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(Constants.LOG_TAG, "Active Alarms Activity destroyed!");
    }

    protected void showDefaultFragment() {
        Log.i(Constants.LOG_TAG, "Populating Active Alarms Activity with list fragment.");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        listFrag = ActiveAlarmListFragment.newInstance();
        ft.replace(R.id.fragment_container, listFrag, DEFAULT_FRAGMENT_TAG);
        ft.commit();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------
    //      AlarmCreationListener methods
    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onAlarmCreated(int position) {
        onAlarmSelected(position);
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
        detailsFrag = ActiveAlarmDetailsFragment.newInstance(alarmID);
        ft.replace(R.id.fragment_container, detailsFrag);
        ft.addToBackStack(null);
        ft.commit();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------
    //      DatePickerListener methods
    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onDatePickerRequested(int alarmID, int[] date) {
        Log.i(Constants.LOG_TAG, "User wants to view a date picker dialog with default date: "
                + date[1] + "/" + date[2] + "/" + date[0]);
        DialogFragment newFragment = DatePickerFragment.newInstance(alarmID, date);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDatePicked(int alarmID, int year, int month, int day) {
        Log.i(Constants.LOG_TAG, "User selected the following date from the picker: "
                + month + "/" + day + "/" + year + " \t\t for current alarm: "
                + alarmID);
        detailsFrag.updateDate(year, month, day);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------
    //      TimePickerListener methods
    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onTimePickerRequested(int alarmID, int[] time) {
        Log.i(Constants.LOG_TAG, "User wants to view a time picker dialog with default time: "
                + time[0] + ":" + time[1]);
        DialogFragment newFragment = TimePickerFragment.newInstance(alarmID, time);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimePicked(int alarmID, int hourOfDay, int minute) {
        Log.i(Constants.LOG_TAG, "User selected the following time from the picker: "
                + hourOfDay + ":" + minute + " \t\t for current alarm: "
                + alarmID);
        detailsFrag.updateTime(hourOfDay, minute);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------
    //      AlarmDetailChangeListener methods
    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onAlarmDetailsChanged() {
        Log.i(Constants.LOG_TAG, "User has made changes to an alarm!  Reloading the "
                + "active alarm list ...");

        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(); // remove alarm details fragment from backstack
        fm.popBackStack(); // remove previous view of alarm list from backstack (keep current)

        FragmentTransaction ft = fm.beginTransaction();
        listFrag = ActiveAlarmListFragment.newInstance();
        ft.replace(R.id.fragment_container, listFrag);
        ft.addToBackStack(null);
        ft.commit();
    }

}