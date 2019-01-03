package edu.temple.sp_admin.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import edu.temple.sp_admin.R;
import edu.temple.sp_res_lib.alarms.Alarm;
import edu.temple.sp_res_lib.alarms.SpAlarmManager;
import edu.temple.sp_res_lib.utils.Constants;

public class ActiveAlarmDetailsFragment extends Fragment {

    public interface AlarmDetailChangeListener {
        void onAlarmDetailsChanged();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private static final String BUNDLE_ARG_POSITION = "bundle_arg_position";

    private int mPosition;
    private Alarm mAlarm;

    private AlarmDetailChangeListener mChangeListener;
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
            mChangeListener = (AlarmDetailChangeListener) context;
        } catch (ClassCastException e) {
            String error = context.toString() + " must implement AlarmDetailChangeListener";
            Log.e(Constants.LOG_TAG, error, e);
            throw new ClassCastException();
        }

        try {
            mDateListener = (DatePickerFragment.DatePickerListener) context;
        } catch (ClassCastException e) {
            String error = context.toString() + " must implement DatePickerListener";
            Log.e(Constants.LOG_TAG, error, e);
            throw new ClassCastException();
        }

        try {
            mTimeListener = (TimePickerFragment.TimePickerListener) context;
        } catch (ClassCastException e) {
            String error = context.toString() + " must implement TimePickerListener";
            Log.e(Constants.LOG_TAG, error, e);
            throw new ClassCastException();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mChangeListener = null;
        mDateListener = null;
        mTimeListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(BUNDLE_ARG_POSITION);
            mAlarm = SpAlarmManager.mAlarmDataset.get(mPosition);
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
                Log.i(Constants.LOG_TAG, "User clicked LABEL field for alarm: " + mPosition);
                Toast.makeText(rootView.getContext(),
                        "LABEL CLICKED", Toast.LENGTH_SHORT).show();
                // TODO - grab the label provided by the user, update the current alarm

                // TODO - lock down editing privileges if alarm is already active
            }
        });

        // ----------------------------------------------------------------------------
        // ----------------------------------------------------------------------------

        TextView dateText = rootView.findViewById(R.id.date_text);
        dateText.setText(mAlarm.getDateString());

        LinearLayout dateLayout = rootView.findViewById(R.id.date_layout);
        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Constants.LOG_TAG, "User clicked DATE field for alarm: " + mPosition);
                mDateListener.onDatePickerRequested(mAlarm.getDate());

                // TODO - lock down editing privileges if alarm is already active
            }
        });

        // ----------------------------------------------------------------------------
        // ----------------------------------------------------------------------------

        TextView timeText = rootView.findViewById(R.id.time_text);
        timeText.setText(mAlarm.getTimeString());

        LinearLayout timeLayout = rootView.findViewById(R.id.time_layout);
        timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Constants.LOG_TAG, "User clicked TIME field for alarm: " + mPosition);
                mTimeListener.onTimePickerRequested(mAlarm.getTime());

                // TODO - lock down editing privileges if alarm is already active
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
                Log.i(Constants.LOG_TAG, "User clicked STATUS field for alarm: " + mPosition);
                Toast.makeText(rootView.getContext(),
                        "STATUS CLICKED", Toast.LENGTH_SHORT).show();
                // TODO - show a dialog with more info about this status code
                // (NOTE - status codes are not editable by the user)

                // TODO - lock down editing privileges if alarm is already active
            }
        });

        // ----------------------------------------------------------------------------
        // ----------------------------------------------------------------------------

        Button activateDeactivateButton = rootView.findViewById(R.id.activate_deactivate_button);
        if (!mAlarm.getStatus().equals(Alarm.STATUS.New.toString())) {
            Log.i(Constants.LOG_TAG, "Alarm is active!  Toggle activate / deactivate button text.");
            activateDeactivateButton.setText(R.string.button_deactivate);
        }

        activateDeactivateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Constants.LOG_TAG, "User clicked ACTIVATE, DEACTIVATE button "
                        + "for alarm: " + mPosition);
                Log.d(Constants.LOG_TAG, "Current alarm status: " + mAlarm.getStatus());

                if (mAlarm.getStatus().equals(Alarm.STATUS.New.toString())) {
                    Log.i(Constants.LOG_TAG, "Alarm status is currently 'New'.  "
                            + "Activating alarm and scheduling reminders.");
                    mAlarm.scheduleReminder(getActivity(), getPatientAlarmReceiverIntent());
                    mAlarm.setStatus(Alarm.STATUS.Active);
                } else {
                    Log.i(Constants.LOG_TAG, "Alarm is already active.  Cancelling "
                            + "currently scheduled reminders and resetting alarm status.");
                    mAlarm.cancelAllReminders();
                    mAlarm.setStatus(Alarm.STATUS.New);
                }

                Log.d(Constants.LOG_TAG, "New alarm status: " + mAlarm.getStatus());
                SpAlarmManager.mAlarmDataset.set(mPosition, mAlarm);
                mChangeListener.onAlarmDetailsChanged();
            }
        });

        Button deleteButton = rootView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlarm.cancelAllReminders();
                SpAlarmManager.mAlarmDataset.remove(mAlarm);
                mChangeListener.onAlarmDetailsChanged();
            }
        });

        return rootView;
    }

    private Intent getPatientAlarmReceiverIntent() {
        String namespace = getResources().getString(R.string.patient_app_namespace);
        String receiverName = getResources().getString(R.string.patient_app_alarm_receiver);

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(namespace, receiverName));
        return intent;
    }

}