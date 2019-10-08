package edu.temple.sp_admin.activities;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import edu.temple.sp_admin.R;
import edu.temple.sp_admin.SpAdmin;
import edu.temple.sp_admin.fragments.AlarmDetailsFragment;
import edu.temple.sp_admin.fragments.AlarmListFragment;
import edu.temple.sp_admin.fragments.DatePickerFragment;
import edu.temple.sp_admin.fragments.EmptyAlarmListFragment;
import edu.temple.sp_admin.fragments.TimePickerFragment;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.utils.AlarmUtil;
import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.Log;

import static edu.temple.sp_admin.SpAdmin.LOG_TAG;

public class CurrentAlarmsActivity extends BaseActivity
        implements AlarmListFragment.OnListItemSelectionListener, AlarmDetailsFragment.OnButtonClickListener,
        DatePickerFragment.DatePickerListener, TimePickerFragment.TimePickerListener {

    private ArrayList<Alarm> mCurrentAlarms;

    private AlarmDetailsFragment detailsFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_alarms);
    }

    @Override
    public void onResume() {
        super.onResume();
        getCurrentAlarms();
        showDefaultFragment();
    }

    @Override
    public void OnListItemSelected(Alarm alarm) {
        Log.ui(LOG_TAG, this,"List item selected for alarm with GUID: " + alarm.getGuid()
            + " \t Description: " + alarm.getDesc()
            + " \t Original Date-Time: " + alarm.getAlarmDateTimeString()
            + " \t Status: " + alarm.getStatus());
        showDetailsFragment(alarm.getGuid());
    }

    @Override
    public void OnButtonClicked(AlarmDetailsFragment.ACTION_BUTTON button, Alarm alarm) {
        Log.ui(LOG_TAG, this, "Button clicked: " + button.toString());

        switch(button) {
            case Save:
                Log.ui(LOG_TAG, this,"Saved alarm with GUID: " + alarm.getGuid()
                        + " \t Description: " + alarm.getDesc()
                        + " \t Original Date-Time: " + alarm.getAlarmDateTimeString()
                        + " \t Status: " + alarm.getStatus());

                Alarm oldAlarm = ((SpAdmin)getApplicationContext()).getAlarm(alarm.getGuid());
                if (alarm.getAlarmTimeMillis() != oldAlarm.getAlarmTimeMillis()) {
                    if (alarm.getAlarmTimeMillis() < Calendar.getInstance().getTimeInMillis()) {
                        Log.e(LOG_TAG, "Cannot set alarm for time in the past!");
                        Toast.makeText(CurrentAlarmsActivity.this, "Cannot set alarm "
                                + " for time in the past!", Toast.LENGTH_LONG).show();
                        break;
                    }

                    Log.e(LOG_TAG, "User has updated alarm time!  Resetting alarm status to ACTIVE.");
                    AlarmUtil.updateStatus(CurrentAlarmsActivity.this, alarm, Alarm.STATUS.Active);
                }

                ((SpAdmin)getApplicationContext()).saveAlarm(alarm);
                getSupportFragmentManager().popBackStack();
                getCurrentAlarms();
                showDefaultFragment();
                break;
            case Cancel:
                Log.ui(LOG_TAG, this,"Canceled changes to alarm with GUID: " + alarm.getGuid()
                        + " \t Description: " + alarm.getDesc()
                        + " \t Original Date-Time: " + alarm.getAlarmDateTimeString()
                        + " \t Status: " + alarm.getStatus());

                // have to explicitly pop the back stack before refreshing fragment
                getSupportFragmentManager().popBackStack();
                showDetailsFragment(alarm.getGuid());
                break;
            case Delete:
                Log.ui(LOG_TAG, this,"Deleted alarm with GUID: " + alarm.getGuid()
                        + " \t Description: " + alarm.getDesc()
                        + " \t Original Date-Time: " + alarm.getAlarmDateTimeString()
                        + " \t Status: " + alarm.getStatus());

                ((SpAdmin)getApplicationContext()).deleteAlarm(alarm);
                getSupportFragmentManager().popBackStack();
                getCurrentAlarms();
                showDefaultFragment();
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

    private void showDetailsFragment(String alarmGUID) {
        Log.i(LOG_TAG, "List item selected!  Item GUID: " + alarmGUID);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        detailsFrag = AlarmDetailsFragment.newInstance(alarmGUID);

        // put this fragment on the backstack so we can return to the default view if necessary
        ft.replace(R.id.alarm_container, detailsFrag)
                .addToBackStack(null)
                .commit();
    }
}