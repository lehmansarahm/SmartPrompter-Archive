package edu.temple.smartprompter_v3.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import edu.temple.smartprompter_v3.res_lib.adapters.AlarmRecyclerViewAdapter;
import edu.temple.smartprompter_v3.res_lib.data.Alarm;
import edu.temple.smartprompter_v3.res_lib.data.FirebaseConnector;
import edu.temple.smartprompter_v3.res_lib.utils.Constants;

public class AlarmListActivity extends BaseActivity implements AlarmRecyclerViewAdapter.AlarmSelectionListener {

    private final static List<Enum> FILTER_STATUSES = Arrays.asList(new Alarm.STATUS[] {
            Alarm.STATUS.Active,
            Alarm.STATUS.Unacknowledged,
            Alarm.STATUS.Incomplete,
            Alarm.STATUS.Complete
    });

    private List<Alarm> filteredAlarms;
    private Spinner filterSpinner;
    private RecyclerView recyclerView;

    private AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                   int position, long id) {
            mFbaEventLogger.spinnerSelection(AlarmListActivity.class, "StatusFilter");
            String email = mFbAuth.getCurrentUser().getEmail();
            Alarm.STATUS filterStatus =
                    Alarm.STATUS.valueOf(parentView.getItemAtPosition(position).toString());

            FirebaseConnector.getAlarmsByStatus(email, filterStatus, results -> {
                filteredAlarms = (List<Alarm>)(Object)results;
                recyclerView.setAdapter(new AlarmRecyclerViewAdapter(filteredAlarms,
                        AlarmListActivity.this));
                Log.i(Constants.LOG_TAG, "Displaying " + filteredAlarms.size()
                        + " alarms for status: " + filterStatus.toString());
            },
                    (error) -> Log.e(BaseActivity.LOG_TAG, "Something went wrong while attempting to "
                            + "retrieve alarm records by status: " + filterStatus, error));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
            // your code heres
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        recyclerView = findViewById(R.id.alarm_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayAdapter<Enum> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, FILTER_STATUSES);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner = findViewById(R.id.status_filter_spinner);
        filterSpinner.setAdapter(dataAdapter);
        filterSpinner.setOnItemSelectedListener(listener);
    }

    @Override
    public void OnAlarmSelected(Alarm alarm) {
        Log.e(Constants.LOG_TAG, "Alarm selected with GUID: " + alarm.getGuid());
        Class targetClass = (alarm.getStatus().equals(Alarm.STATUS.Complete))
                ? AlarmLogActivity.class : AlarmDetailsActivity.class;
        Intent intent = new Intent(AlarmListActivity.this, targetClass);
        intent.putExtra(Constants.BUNDLE_ARG_ALARM_GUID, alarm.getGuid());
        startActivity(intent);
    }

}