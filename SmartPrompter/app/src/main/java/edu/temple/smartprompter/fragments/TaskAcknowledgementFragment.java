package edu.temple.smartprompter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.temple.smartprompter.R;
import edu.temple.smartprompter.utils.Constants;

public class TaskAcknowledgementFragment extends Fragment {

    public interface TaskAcknowledgementListener {
        void onAlarmAcknowledged(int alarmID);
        void onAlarmDeferred(int alarmID);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private TaskAcknowledgementListener mListener;
    private int mAlarmID;

    public TaskAcknowledgementFragment() {
        // required empty constructor
    }

    public static TaskAcknowledgementFragment newInstance(int alarmID) {
        TaskAcknowledgementFragment fragment = new TaskAcknowledgementFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.BUNDLE_ARG_ALARM_ID, alarmID);
        fragment.setArguments(args);
        return fragment;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (TaskAcknowledgementListener) context;
        } catch (ClassCastException e) {
            String error = context.toString() + " must implement TaskAcknowledgementListener";
            Log.e(edu.temple.sp_res_lib.utils.Constants.LOG_TAG, error, e);
            throw new ClassCastException();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAlarmID = getArguments().getInt(Constants.BUNDLE_ARG_ALARM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_task_acknowledgement,
                container, false);

        Button acknowledgeButton = rootView.findViewById(R.id.acknowledge_button);
        acknowledgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onAlarmAcknowledged(mAlarmID);
            }
        });

        Button reminderButton = rootView.findViewById(R.id.remind_button);
        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onAlarmDeferred(mAlarmID);
            }
        });

        return rootView;
    }

}