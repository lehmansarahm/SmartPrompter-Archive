package edu.temple.smartprompter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.temple.smartprompter.R;
import edu.temple.smartprompter.adapters.SimpleAlarmListAdapter;
import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.SpAlarmManager;
import edu.temple.sp_res_lib.utils.Constants;

public class CompleteAlarmListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mEmptyView;

    private SimpleAlarmListAdapter.AlarmSelectionListener mSelectionListener;

    private SpAlarmManager mAlarmMgr;

    public CompleteAlarmListFragment() {
        // Required empty public constructor
    }

    public static CompleteAlarmListFragment newInstance() {
        CompleteAlarmListFragment fragment = new CompleteAlarmListFragment();
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
            mSelectionListener = (SimpleAlarmListAdapter.AlarmSelectionListener) context;
        } catch (ClassCastException e) {
            String error = context.toString() + " must implement AlarmSelectionListener";
            Log.e(Constants.LOG_TAG, error, e);
            throw new ClassCastException();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSelectionListener = null;
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
        View rootView = inflater.inflate(R.layout.fragment_complete_alarm_list, container,
                false);

        mEmptyView = rootView.findViewById(R.id.empty_view);
        mRecyclerView = rootView.findViewById(R.id.complete_alarm_recycler);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        Alarm.STATUS[] statuses = new Alarm.STATUS[] { Alarm.STATUS.Complete };

        mAdapter = new SimpleAlarmListAdapter(mAlarmMgr.get(statuses),
                mSelectionListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        checkDatasetVisibility();
        return rootView;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void checkDatasetVisibility() {
        if (!mAlarmMgr.areCompleteAlarmsAvailable()) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

}