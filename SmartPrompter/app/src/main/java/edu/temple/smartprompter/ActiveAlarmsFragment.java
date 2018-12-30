package edu.temple.smartprompter;

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

import java.util.List;

public class ActiveAlarmsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Alarm> mAlarmDataset = Alarm.getDefaults();

    public ActiveAlarmsFragment() {
        // Required empty public constructor
    }

    public static ActiveAlarmsFragment newInstance(/* any parameters we require */) {
        ActiveAlarmsFragment fragment = new ActiveAlarmsFragment();
        Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, param1);
        // args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View rootView = inflater.inflate(R.layout.fragment_active_alarms, container,
                false);

        mRecyclerView = rootView.findViewById(R.id.active_alarm_recycler);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ActiveAlarmsAdapter(mAlarmDataset);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        FloatingActionButton fab = rootView.findViewById(R.id.add_alarm_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),
                        "Time to add a new alarm!",
                        Toast.LENGTH_SHORT).show();
                mAlarmDataset.add(new Alarm("01/01/19", "12:00 AM", "New Alarm"));
                mAdapter.notifyItemInserted(mAlarmDataset.size() - 1);
            }
        });

        return rootView;
    }

}