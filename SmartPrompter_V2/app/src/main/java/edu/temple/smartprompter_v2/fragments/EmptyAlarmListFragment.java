package edu.temple.smartprompter_v2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.temple.smartprompter_v2.R;

public class EmptyAlarmListFragment extends Fragment {

    public EmptyAlarmListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_empty_alarm_list,
                container, false);
    }

}