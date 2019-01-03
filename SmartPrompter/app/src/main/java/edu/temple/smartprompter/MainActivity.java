package edu.temple.smartprompter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import edu.temple.smartprompter.adapters.AlarmListAdapter;
import edu.temple.smartprompter.utils.Constants;
import edu.temple.sp_res_lib.SpAlarmManager;

public class MainActivity extends AppCompatActivity implements AlarmListAdapter.AlarmDetailsListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mEmptyView;

    private SpAlarmManager mAlarmMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOG_TAG, "Main Activity created!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAlarmMgr = new SpAlarmManager(this);

        mEmptyView = findViewById(R.id.empty_view);
        mRecyclerView = findViewById(R.id.active_alarm_recycler);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new AlarmListAdapter(mAlarmMgr.getAll(), this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        checkDatasetVisibility();
    }

    @Override
    public void onDestroy() {
        Log.i(Constants.LOG_TAG, "Main Activity destroyed!");
        super.onDestroy();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onAlarmSelected(int alarmID) {
        Intent intent = new Intent(this, AlarmDetailsActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_ALARM_ID, alarmID);
        startActivity(intent);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void checkDatasetVisibility() {
        if (!mAlarmMgr.areAlarmsAvailable()) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}