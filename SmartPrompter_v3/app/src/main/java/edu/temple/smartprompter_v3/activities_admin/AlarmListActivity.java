package edu.temple.smartprompter_v3.activities_admin;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import edu.temple.smartprompter_v3.BaseActivity;
import edu.temple.smartprompter_v3.R;
import edu.temple.smartprompter_v3.adapters.AlarmRecyclerViewAdapter;
import edu.temple.smartprompter_v3.data.Alarm;
import edu.temple.smartprompter_v3.data.FirebaseConnector;
import edu.temple.smartprompter_v3.utils.Constants;

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
            fbaEventLogger.spinnerSelection(AlarmListActivity.class, "StatusFilter");
            Alarm.STATUS filterStatus =
                    Alarm.STATUS.valueOf(parentView.getItemAtPosition(position).toString());
            FirebaseConnector.getAlarmsByStatus(filterStatus, results -> {
                filteredAlarms = (List<Alarm>)(Object)results;
                recyclerView.setAdapter(new AlarmRecyclerViewAdapter(filteredAlarms,
                        AlarmListActivity.this));
                Log.i(Constants.LOG_TAG, "Displaying " + filteredAlarms.size()
                        + " alarms for status: " + filterStatus.toString());
            });
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
            // your code heres
        }

    };

    @Override
    protected void showLoggedInView() {
        Log.i(Constants.LOG_TAG, "showLoggedInView method called for class: "
                + this.getClass().getSimpleName());
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
        Intent intent = new Intent(AlarmListActivity.this, AlarmDetailsActivity.class);
        intent.putExtra(Constants.BUNDLE_ARG_ALARM_GUID, alarm.getGuid());
        startActivity(intent);
    }

}