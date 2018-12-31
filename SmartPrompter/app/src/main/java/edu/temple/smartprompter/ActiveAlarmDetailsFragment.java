package edu.temple.smartprompter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActiveAlarmDetailsFragment extends Fragment {

    private static final String BUNDLE_ARG_POSITION = "bundle_arg_position";

    private int mPosition;
    private Alarm mAlarm;

    private DatePickerFragment.DatePickerListener mDateListener;
    private TimePickerFragment.TimePickerListener mTimeListener;

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
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mDateListener = (DatePickerFragment.DatePickerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DatePickerFragment.DatePickerListener");
        }

        try {
            mTimeListener = (TimePickerFragment.TimePickerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TimePickerFragment.TimePickerListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDateListener = null;
        mTimeListener = null;
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
        final View rootView = inflater.inflate(R.layout.fragment_active_alarm_detail, container,
                false);

        // ----------------------------------------------------------------------------
        // ----------------------------------------------------------------------------

        TextView labelText = rootView.findViewById(R.id.label_text);
        labelText.setText(mAlarm.getLabel());

        LinearLayout labelLayout = rootView.findViewById(R.id.label_layout);
        labelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(rootView.getContext(),
                        "LABEL CLICKED", Toast.LENGTH_SHORT).show();
                // TODO - grab the label provided by the user, update the current alarm
            }
        });

        // ----------------------------------------------------------------------------
        // ----------------------------------------------------------------------------

        TextView dateText = rootView.findViewById(R.id.date_text);
        dateText.setText(mAlarm.getDate());

        LinearLayout dateLayout = rootView.findViewById(R.id.date_layout);
        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDateListener.onDatePickerRequested();
                // TODO - grab the date the user selects, update the current alarm
            }
        });

        // ----------------------------------------------------------------------------
        // ----------------------------------------------------------------------------

        TextView timeText = rootView.findViewById(R.id.time_text);
        timeText.setText(mAlarm.getTime());

        LinearLayout timeLayout = rootView.findViewById(R.id.time_layout);
        timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimeListener.onTimePickerRequested();
                // TODO - grab the time the user selects, update the current alarm
            }
        });

        // ----------------------------------------------------------------------------
        // ----------------------------------------------------------------------------

        TextView statusText = rootView.findViewById(R.id.status_text);
        statusText.setText(mAlarm.getStatus());

        LinearLayout statusLayout = rootView.findViewById(R.id.status_layout);
        statusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(rootView.getContext(),
                        "STATUS CLICKED", Toast.LENGTH_SHORT).show();
                // TODO - show a dialog with more info about this status code
                // (NOTE - status codes are not editable by the user)
            }
        });

        return rootView;
    }

}