package edu.temple.sp_admin.activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import edu.temple.sp_admin.R;
import edu.temple.sp_admin.SpAdmin;
import edu.temple.sp_admin.fragments.AlarmListFragment;
import edu.temple.sp_admin.fragments.EmptyAlarmListFragment;
import edu.temple.sp_res_lib.obj.Alarm;

import static edu.temple.sp_admin.SpAdmin.LOG_TAG;

public class PastAlarmsActivity extends AppCompatActivity implements AlarmListFragment.OnListItemSelectionListener {

    ArrayList<Alarm> mPastAlarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_alarms);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        getPastAlarms();

        if (mPastAlarms != null && mPastAlarms.size() > 0) {
            Log.i(LOG_TAG, "Populating current activity with alarm-list fragment.");
            AlarmListFragment fragment = AlarmListFragment.newInstance(mPastAlarms,
                    "Past Alarms:");
            ft.replace(R.id.alarm_container, fragment);
        } else {
            Log.i(LOG_TAG, "Populating current activity with empty-list fragment.");
            EmptyAlarmListFragment fragment = new EmptyAlarmListFragment();
            ft.replace(R.id.alarm_container, fragment);
        }

        ft.commit();
    }

    private void getPastAlarms() {
        SpAdmin spa = (SpAdmin) getApplicationContext();
        mPastAlarms = spa.getPastAlarms();
    }

    @Override
    public void OnListItemSelected(Alarm item) {
        Log.i(LOG_TAG, "List item selected!  Item Number: " + item.getID());
    }
}