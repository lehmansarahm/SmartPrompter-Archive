package edu.temple.sp_admin.activities;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import edu.temple.sp_admin.R;
import edu.temple.sp_admin.SpAdmin;
import edu.temple.sp_admin.fragments.AlarmDetailsFragment;
import edu.temple.sp_admin.fragments.DatePickerFragment;
import edu.temple.sp_admin.fragments.TimePickerFragment;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.utils.Constants;

import static edu.temple.sp_admin.SpAdmin.LOG_TAG;

public class NewAlarmActivity extends BaseActivity
        implements AlarmDetailsFragment.OnButtonClickListener,
        DatePickerFragment.DatePickerListener, TimePickerFragment.TimePickerListener {

    private AlarmDetailsFragment detailsFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);
        showDefaultFragment();
    }

    @Override
    public void OnButtonClicked(AlarmDetailsFragment.ACTION_BUTTON button, Alarm alarm) {
        Log.i(LOG_TAG, "Button Clicked: " + button.toString()
                + " for alarm with GUID: " + alarm.getGuid());
        switch(button) {
            case Save:
                ((SpAdmin)getApplicationContext()).saveAlarm(alarm);
                startActivity(new Intent(NewAlarmActivity.this,
                        CurrentAlarmsActivity.class));
                break;
            case Cancel:
                // no need to explicitly pop back stack for single-fragment activity
                showDefaultFragment();
                break;
            case Delete:
                ((SpAdmin)getApplicationContext()).deleteAlarm(alarm);
                startActivity(new Intent(NewAlarmActivity.this,
                        CurrentAlarmsActivity.class));
                break;
        }
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------
    //      DatePickerListener methods
    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onDatePickerRequested(String alarmGUID, int[] date) {
        Log.i(Constants.LOG_TAG, "User wants to view a date picker dialog with default date: "
                + date[1] + "/" + date[2] + "/" + date[0]);
        DialogFragment newFragment = DatePickerFragment.newInstance(alarmGUID, date);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDatePicked(String alarmGUID, int year, int month, int day) {
        Log.i(Constants.LOG_TAG, "User selected the following date from the picker: "
                + month + "/" + day + "/" + year + " \t\t for current alarm: "
                + alarmGUID);
        detailsFrag.updateDate(year, month, day);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------
    //      TimePickerListener methods
    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onTimePickerRequested(String alarmGUID, int[] time) {
        Log.i(Constants.LOG_TAG, "User wants to view a time picker dialog with default time: "
                + time[0] + ":" + time[1]);
        DialogFragment newFragment = TimePickerFragment.newInstance(alarmGUID, time);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimePicked(String alarmGUID, int hourOfDay, int minute) {
        Log.i(Constants.LOG_TAG, "User selected the following time from the picker: "
                + hourOfDay + ":" + minute + " \t\t for current alarm: "
                + alarmGUID);
        detailsFrag.updateTime(hourOfDay, minute);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void showDefaultFragment() {
        Log.i(LOG_TAG, "Populating " + this.getLocalClassName()
                + " with Alarm-Details fragment.");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        detailsFrag = AlarmDetailsFragment.newInstance(Constants.DEFAULT_ALARM_GUID);
        ft.replace(R.id.details_container, detailsFrag);
        ft.commit();
    }

}