package edu.temple.smartprompter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import edu.temple.smartprompter.adapters.ActiveAlarmsAdapter;
import edu.temple.smartprompter.alarms.Alarm;
import edu.temple.smartprompter.alarms.SpAlarmManager;
import edu.temple.smartprompter.R;
import edu.temple.smartprompter.utils.Constants;

public class ActiveAlarmListFragment extends Fragment {

    public static final int NEW_ALARM_INSERTION_INDEX = 0;

    public interface AlarmCreationListener {
        void onAlarmCreated(int position);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mEmptyView;

    private AlarmCreationListener mCreationListener;
    private ActiveAlarmsAdapter.AlarmDetailsListener mDetailsListener;

    public ActiveAlarmListFragment() {
        // Required empty public constructor
    }

    public static ActiveAlarmListFragment newInstance(/* any parameters we require */) {
        ActiveAlarmListFragment fragment = new ActiveAlarmListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCreationListener = (AlarmCreationListener) context;
        } catch (ClassCastException e) {
            String error = context.toString() + " must implement AlarmCreationListener";
            Log.e(Constants.LOG_TAG, error, e);
            throw new ClassCastException();
        }

        try {
            mDetailsListener = (ActiveAlarmsAdapter.AlarmDetailsListener) context;
        } catch (ClassCastException e) {
            String error = context.toString() + " must implement AlarmDetailsListener";
            Log.e(Constants.LOG_TAG, error, e);
            throw new ClassCastException();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDetailsListener = null;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_active_alarm_list, container,
                false);

        mEmptyView = rootView.findViewById(R.id.empty_view);
        mRecyclerView = rootView.findViewById(R.id.active_alarm_recycler);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ActiveAlarmsAdapter(SpAlarmManager.mAlarmDataset, mDetailsListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        checkDatasetVisibility();

        FloatingActionButton addAlarmButton = rootView.findViewById(R.id.add_alarm_button);
        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // insert a new alarm record at the top of the list
                SpAlarmManager.mAlarmDataset.add(NEW_ALARM_INSERTION_INDEX, Alarm.getNewAlarm());
                mAdapter.notifyItemInserted(NEW_ALARM_INSERTION_INDEX);

                // force the list view to return to the top
                LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView
                        .getLayoutManager();
                layoutManager.scrollToPositionWithOffset(0, 0);

                // let the parent activity know that an alarm was created
                mCreationListener.onAlarmCreated(NEW_ALARM_INSERTION_INDEX);
            }
        });

        FloatingActionButton saveCloseButton = rootView.findViewById(R.id.save_close_button);
        saveCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Save-Close Button clicked!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void checkDatasetVisibility() {
        if (SpAlarmManager.mAlarmDataset.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

}