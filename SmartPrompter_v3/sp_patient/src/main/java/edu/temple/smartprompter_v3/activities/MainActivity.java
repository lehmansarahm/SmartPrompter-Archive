package edu.temple.smartprompter_v3.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextClock;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.temple.smartprompter_v3.R;
import edu.temple.smartprompter_v3.res_lib.SpController;
import edu.temple.smartprompter_v3.res_lib.adapters.AlarmRecyclerViewAdapter;
import edu.temple.smartprompter_v3.res_lib.data.Alarm;
import edu.temple.smartprompter_v3.res_lib.data.FirebaseConnector;
import edu.temple.smartprompter_v3.res_lib.utils.Constants;

public class MainActivity extends BaseActivity implements AlarmRecyclerViewAdapter.AlarmSelectionListener {

    private static final boolean DEV_MODE = false;

    private List<Alarm> filteredAlarms;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextClock clock = findViewById(R.id.clock);
        clock.setFormat12Hour(DEV_MODE ? "hh:mm:ss a" : "h:mm a");

        String email = mFbAuth.getCurrentUser().getEmail();
        FirebaseConnector.getActiveAlarmTasks(email, results -> {
            filteredAlarms = (List<Alarm>)(Object)results;
            if (filteredAlarms.size() > 0) initAlarmList(email);
            else {
                LinearLayout alarmListLayout = findViewById(R.id.alarm_list_layout);
                alarmListLayout.setVisibility(View.INVISIBLE);
            }
        },
                (error) -> Log.e(BaseActivity.LOG_TAG, "Something went wrong while attempting to "
                        + "retrieve alarms by email: " + email, error));
    }

    @Override
    public void OnAlarmSelected(Alarm alarm) {
        Log.e(LOG_TAG, "Item selected: " + alarm.getGuid());

        // cancelAlarm any latent alarms for this record
        SpController.cancelAlarm(this, alarm, BaseActivity.ALARM_RECEIVER_CLASS);

        // select response activity according to the current record status
        Intent intent;
        if (alarm.getStatus().equals(Alarm.STATUS.Incomplete)) {
            mFbaEventLogger.alarmTaskResume(MainActivity.class, "Completion", alarm);
            intent = new Intent(MainActivity.this, CompletionActivity.class);
        } else {
            mFbaEventLogger.alarmTaskResume(MainActivity.class, "Acknowledgment", alarm);
            intent = new Intent(MainActivity.this, AcknowledgmentActivity.class);
        }

        // launch the response activity
        intent.putExtra(Constants.BUNDLE_ARG_ALARM_GUID, alarm.getGuid());
        startActivity(intent);
        finish();
    }

    private void initAlarmList(String email) {
        LinearLayout emptyListLayout = findViewById(R.id.empty_list_layout);
        emptyListLayout.setVisibility(View.INVISIBLE);
        Log.i(LOG_TAG, "Displaying " + filteredAlarms.size()
                + " active alarm tasks matching email: " + email);

        recyclerView = findViewById(R.id.tasks_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new AlarmRecyclerViewAdapter(filteredAlarms,
                MainActivity.this));
    }

}