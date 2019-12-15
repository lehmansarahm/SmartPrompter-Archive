package edu.temple.sp_admin.activities;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;

import edu.temple.sp_admin.R;
import edu.temple.sp_admin.SpAdmin;
import edu.temple.sp_admin.fragments.AlarmDetailsFragment;
import edu.temple.sp_admin.fragments.DatePickerFragment;
import edu.temple.sp_admin.fragments.TimePickerFragment;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.Log;

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
        Log.ui(LOG_TAG, this, "Button clicked: " + button.toString());

        switch(button) {
            case Save:
                Log.ui(LOG_TAG, this,
                        "Saved new alarm with description: " + alarm.getDesc()
                        + " \t and time: " + alarm.getAlarmDateTimeString());

                if (alarm.getAlarmTimeMillis() < Calendar.getInstance().getTimeInMillis()) {
                    Log.e(LOG_TAG, "Cannot set alarm for time in the past!");
                    Toast.makeText(NewAlarmActivity.this, "Cannot set alarm for time "
                            + "in the past!", Toast.LENGTH_LONG).show();
                    break;
                }

                ((SpAdmin)getApplicationContext()).saveAlarm(alarm);
                startActivity(new Intent(NewAlarmActivity.this,
                        ActiveAlarmsActivity.class));
                finish();
                break;
            case Cancel:
                Log.ui(LOG_TAG, this,
                        "Canceled changes to alarm with description: " + alarm.getDesc()
                        + " \t and time: " + alarm.getAlarmDateTimeString());
                // no need to explicitly pop back stack for single-fragment activity
                showDefaultFragment();
                break;
            case Delete:
                Log.ui(LOG_TAG, this,
                        "Deleted new alarm with description: " + alarm.getDesc()
                                + " \t and time: " + alarm.getAlarmDateTimeString());
                ((SpAdmin)getApplicationContext()).deleteAlarm(alarm);
                startActivity(new Intent(NewAlarmActivity.this,
                        ActiveAlarmsActivity.class));
                finish();
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
        Log.ui(Constants.LOG_TAG, this, "DatePickerDialog requested with default date: "
                + date[1] + "/" + date[2] + "/" + date[0]);
        DialogFragment newFragment = DatePickerFragment.newInstance(alarmGUID, date);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDatePicked(String alarmGUID, int year, int month, int day) {
        Log.ui(Constants.LOG_TAG, this,"Date picked: " + month + "/" + day + "/" + year
                + " \t\t for current alarm: " + alarmGUID);
        detailsFrag.updateDate(year, month, day);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------
    //      TimePickerListener methods
    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onTimePickerRequested(String alarmGUID, int[] time) {
        Log.ui(Constants.LOG_TAG, this,"TimePickerDialog requested with default time: "
                + time[0] + ":" + time[1]);
        DialogFragment newFragment = TimePickerFragment.newInstance(alarmGUID, time);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimePicked(String alarmGUID, int hourOfDay, int minute) {
        Log.ui(Constants.LOG_TAG, this,"Time picked: " + hourOfDay + ":" + minute
                + " \t\t for current alarm: " + alarmGUID);
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