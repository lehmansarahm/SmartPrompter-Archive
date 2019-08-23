package edu.temple.sp_admin.activities;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import edu.temple.sp_admin.R;
import edu.temple.sp_admin.fragments.AlarmDetailsFragment;
import edu.temple.sp_admin.fragments.DatePickerFragment;
import edu.temple.sp_admin.fragments.TimePickerFragment;
import edu.temple.sp_res_lib.utils.Constants;

import static edu.temple.sp_admin.SpAdmin.LOG_TAG;

public class NewAlarmActivity extends AppCompatActivity
        implements AlarmDetailsFragment.OnButtonClickListener,
        DatePickerFragment.DatePickerListener, TimePickerFragment.TimePickerListener {

    private AlarmDetailsFragment detailsFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);

        Log.i(LOG_TAG, "Populating " + this.getLocalClassName()
                + " with Alarm-Details fragment.");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        detailsFrag = AlarmDetailsFragment.newInstance(Constants.DEFAULT_ALARM_ID);
        ft.replace(R.id.details_container, detailsFrag);
        ft.commit();
    }

    @Override
    public void OnButtonClicked(AlarmDetailsFragment.ACTION_BUTTON button) {
        Log.i(LOG_TAG, "Button Clicked: " + button.toString());
        switch(button) {
            case Save:
                // TODO - fill out save logic for "New Alarm" activity
                break;
            case Cancel:
                // TODO - fill out cancellation logic for "New Alarm" activity
                break;
            case Delete:
                // TODO - fill out delete logic for "New Alarm" activity
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

}