package edu.temple.smartprompter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ActiveAlarmDetailsFragment extends Fragment {

    private static final String BUNDLE_ARG_POSITION = "bundle_arg_position";

    private int mPosition;
    private Alarm mAlarm;

    public ActiveAlarmDetailsFragment() {
        // Required empty public constructor
    }

    public static ActiveAlarmDetailsFragment newInstance(int position) {
        ActiveAlarmDetailsFragment fragment = new ActiveAlarmDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(BUNDLE_ARG_POSITION);
            mAlarm = AlarmManager.mAlarmDataset.get(mPosition);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_active_alarm_detail, container,
                false);

        TextView dateText = rootView.findViewById(R.id.date_text);
        dateText.setText("DATE: " + mAlarm.getDate());

        TextView timeText = rootView.findViewById(R.id.time_text);
        timeText.setText("TIME: " + mAlarm.getTime());

        TextView labelText = rootView.findViewById(R.id.label_text);
        labelText.setText("LABEL: " + mAlarm.getLabel());

        TextView statusText = rootView.findViewById(R.id.status_text);
        statusText.setText("STATUS: " + mAlarm.getStatus());

        return rootView;
    }

}