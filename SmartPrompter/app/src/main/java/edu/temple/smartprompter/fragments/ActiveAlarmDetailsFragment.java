package edu.temple.smartprompter.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.temple.smartprompter.R;
import edu.temple.smartprompter.utils.Constants;
import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.SpAlarmManager;

public class ActiveAlarmDetailsFragment extends Fragment {

    private SpAlarmManager mAlarmMgr;
    private Alarm mAlarm;

    public ActiveAlarmDetailsFragment() {
        // required empty constructor
    }

    public static ActiveAlarmDetailsFragment newInstance(int alarmID) {
        ActiveAlarmDetailsFragment fragment = new ActiveAlarmDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.BUNDLE_ARG_ALARM_ID, alarmID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int alarmID = getArguments().getInt(Constants.BUNDLE_ARG_ALARM_ID);
            mAlarmMgr = new SpAlarmManager(getActivity());
            mAlarm = mAlarmMgr.get(alarmID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_active_alarm_detail,
                container, false);

        TextView labelText = rootView.findViewById(R.id.label_text);
        labelText.setText(mAlarm.getLabel());

        TextView dateText = rootView.findViewById(R.id.date_text);
        dateText.setText(mAlarm.getDateString());

        TextView timeText = rootView.findViewById(R.id.time_text);
        timeText.setText(mAlarm.getTimeString());

        TextView statusText = rootView.findViewById(R.id.status_text);
        statusText.setText(mAlarm.getStatusString());

        switch (mAlarm.getStatus()) {
            case Active:
            case Complete:
                // do nothing
                break;
            case Unacknowledged:
                statusText.setBackgroundColor(Color.GREEN);
                break;
            case Incomplete:
                statusText.setBackgroundColor(Color.RED);
                break;
            default:
                Log.e(edu.temple.sp_res_lib.utils.Constants.LOG_TAG,
                        "Unrecognized alarm status: "
                        + mAlarm.getStatusString());
        }

        return rootView;
    }

}