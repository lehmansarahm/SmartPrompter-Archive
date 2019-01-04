package edu.temple.sp_admin;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import edu.temple.sp_admin.adapters.SimpleAlarmAdapter;
import edu.temple.sp_admin.fragments.CompleteAlarmListFragment;
import edu.temple.sp_admin.utils.BaseActivity;
import edu.temple.sp_admin.utils.Constants;

public class CompleteAlarmsActivity extends BaseActivity implements
        SimpleAlarmAdapter.AlarmSelectionListener {

    private CompleteAlarmListFragment listFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOG_TAG, "Complete Alarms Activity created!");
        setContentView(R.layout.activity_active_alarms);
        super.onCreate(savedInstanceState);

        Log.i(Constants.LOG_TAG, "Populating Complete Alarms Activity with list fragment.");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        listFrag = CompleteAlarmListFragment.newInstance();
        ft.replace(R.id.fragment_container, listFrag);
        ft.commit();
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

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------
    //      AlarmSelectionListener methods
    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onAlarmSelected(int alarmID) {
        Log.e(Constants.LOG_TAG, "COMPLETED ALARM SELECTED");
        Toast.makeText(this, "Haven't programmed this path yet ...",
                Toast.LENGTH_SHORT).show();
    }

}