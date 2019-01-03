package edu.temple.sp_admin.fragments;

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

import edu.temple.sp_admin.R;
import edu.temple.sp_admin.adapters.ActiveAlarmsAdapter;

import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.SpAlarmManager;
import edu.temple.sp_res_lib.utils.Constants;

public class ActiveAlarmListFragment extends Fragment {

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

    private SpAlarmManager mAlarmMgr;

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
        mAlarmMgr = new SpAlarmManager(getActivity());
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

        mAdapter = new ActiveAlarmsAdapter(mAlarmMgr.getAll(), mDetailsListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        checkDatasetVisibility();

        FloatingActionButton addAlarmButton = rootView.findViewById(R.id.add_alarm_button);
        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Alarm alarm = mAlarmMgr.create();
                mAdapter.notifyDataSetChanged(); // mAdapter.notifyItemInserted(newAlarmPosition);

                // force the list view to return to the top
                LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView
                        .getLayoutManager();
                layoutManager.scrollToPositionWithOffset(0, 0);

                // let the parent activity know that an alarm was created
                mCreationListener.onAlarmCreated(alarm.getID());
            }
        });

        /* FloatingActionButton saveCloseButton = rootView.findViewById(R.id.save_close_button);
        saveCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Save-Close Button clicked!",
                        Toast.LENGTH_SHORT).show();
            }
        }); */

        return rootView;
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