package edu.temple.sp_admin.activities;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import edu.temple.sp_admin.R;
import edu.temple.sp_admin.SpAdmin;
import edu.temple.sp_admin.fragments.AlarmDetailsFragment;
import edu.temple.sp_admin.fragments.AlarmListFragment;
import edu.temple.sp_admin.fragments.DatePickerFragment;
import edu.temple.sp_admin.fragments.EmptyAlarmListFragment;
import edu.temple.sp_admin.fragments.TimePickerFragment;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.utils.Constants;

import static edu.temple.sp_admin.SpAdmin.LOG_TAG;

public class CurrentAlarmsActivity extends AppCompatActivity
        implements AlarmListFragment.OnListItemSelectionListener, AlarmDetailsFragment.OnButtonClickListener,
        DatePickerFragment.DatePickerListener, TimePickerFragment.TimePickerListener {

    private ArrayList<Alarm> mCurrentAlarms;

    private AlarmDetailsFragment detailsFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_alarms);

        getCurrentAlarms();
        showDefaultFragment();
    }

    @Override
    public void OnListItemSelected(Alarm alarm) {
        showDetailsFragment(alarm.getID());
    }

    @Override
    public void OnButtonClicked(AlarmDetailsFragment.ACTION_BUTTON button, Alarm alarm) {
        Log.i(LOG_TAG, "Button Clicked: " + button.toString()
                + " for alarm with ID: " + alarm.getID());
        switch(button) {
            case Save:
                ((SpAdmin)getApplicationContext()).saveAlarm(alarm);
                break;
            case Cancel:
                // have to explicitly pop the back stack before refreshing fragment
                getSupportFragmentManager().popBackStack();
                showDetailsFragment(alarm.getID());
                break;
            case Delete:
                ((SpAdmin)getApplicationContext()).deleteAlarm(alarm);
                break;
        }
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

    private void getCurrentAlarms() {
        SpAdmin spa = (SpAdmin) getApplicationContext();
        mCurrentAlarms = spa.getCurrentAlarms();
    }

    private void showDefaultFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mCurrentAlarms != null && mCurrentAlarms.size() > 0) {
            Log.i(LOG_TAG, "Populating " + this.getLocalClassName()
                    + " with alarm-list fragment.");
            AlarmListFragment fragment = AlarmListFragment.newInstance(mCurrentAlarms,
                    "Current Alarms:");
            ft.replace(R.id.alarm_container, fragment);
        } else {
            Log.i(LOG_TAG, "Populating " + this.getLocalClassName()
                    + " with empty-list fragment.");
            EmptyAlarmListFragment fragment = new EmptyAlarmListFragment();
            ft.replace(R.id.alarm_container, fragment);
        }

        // DO NOT put this fragment on the backstack ... this is the default view of the activity
        ft.commit();
    }

    private void showDetailsFragment(int alarmID) {
        Log.i(LOG_TAG, "List item selected!  Item Number: " + alarmID);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        detailsFrag = AlarmDetailsFragment.newInstance(alarmID);

        // put this fragment on the backstack so we can return to the default view if necessary
        ft.replace(R.id.alarm_container, detailsFrag)
                .addToBackStack(null)
                .commit();
    }

}