package edu.temple.smartprompter;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class ActiveAlarmListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ActiveAlarmsAdapter.AlarmDetailsListener mListener;

    public ActiveAlarmListFragment() {
        // Required empty public constructor
    }

    public static ActiveAlarmListFragment newInstance(/* any parameters we require */) {
        ActiveAlarmListFragment fragment = new ActiveAlarmListFragment();
        Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, param1);
        // args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (ActiveAlarmsAdapter.AlarmDetailsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ActiveAlarmsAdapter.AlarmDetailsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_active_alarm_list, container,
                false);

        mRecyclerView = rootView.findViewById(R.id.active_alarm_recycler);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ActiveAlarmsAdapter(AlarmManager.mAlarmDataset, mListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        FloatingActionButton fab = rootView.findViewById(R.id.add_alarm_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // insert a new alarm record at the top of the list
                AlarmManager.mAlarmDataset.add(0,
                        new Alarm("01/01/19", "12:00 AM", "New Alarm"));
                mAdapter.notifyItemInserted(0);

                // force the list view to return to the top
                LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView
                        .getLayoutManager();
                layoutManager.scrollToPositionWithOffset(0, 0);

                // show a toast so user knows to edit the record
                Toast.makeText(getActivity(), "New alarm created.  Click to edit.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

}