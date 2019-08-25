package edu.temple.sp_admin.activities;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import edu.temple.sp_admin.R;
import edu.temple.sp_admin.SpAdmin;
import edu.temple.sp_admin.fragments.AlarmListFragment;
import edu.temple.sp_admin.fragments.AlarmLogFragment;
import edu.temple.sp_admin.fragments.EmptyAlarmListFragment;
import edu.temple.sp_res_lib.obj.Alarm;

import static edu.temple.sp_admin.SpAdmin.LOG_TAG;

public class PastAlarmsActivity extends BaseActivity
        implements AlarmListFragment.OnListItemSelectionListener {

    ArrayList<Alarm> mPastAlarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_alarms);

        getPastAlarms();
        showDefaultFragment();
    }

    @Override
    public void OnListItemSelected(Alarm item) {
        Log.i(LOG_TAG, "List item selected!  Item Number: " + item.getID());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        AlarmLogFragment fragment = new AlarmLogFragment();

        // put this fragment on the backstack so we can return to the default view if necessary
        ft.replace(R.id.alarm_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void getPastAlarms() {
        SpAdmin spa = (SpAdmin) getApplicationContext();
        mPastAlarms = spa.getArchivedAlarms();
    }

    private void showDefaultFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mPastAlarms != null && mPastAlarms.size() > 0) {
            Log.i(LOG_TAG, "Populating " + this.getLocalClassName()
                    + " with alarm-list fragment.");
            AlarmListFragment fragment = AlarmListFragment.newInstance(mPastAlarms,
                    "Past Alarms:");
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

}