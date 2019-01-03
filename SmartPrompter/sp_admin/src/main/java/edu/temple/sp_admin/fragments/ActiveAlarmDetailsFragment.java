package edu.temple.sp_admin.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import edu.temple.sp_admin.R;
import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.SpAlarmManager;
import edu.temple.sp_res_lib.utils.Constants;

public class ActiveAlarmDetailsFragment extends Fragment {

    public interface AlarmDetailChangeListener {
        void onAlarmDetailsChanged();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private static final String BUNDLE_ARG_ALARM_ID = "bundle_arg_position";

    private SpAlarmManager mAlarmMgr;
    private Alarm mAlarm;

    private AlarmDetailChangeListener mChangeListener;
    private DatePickerFragment.DatePickerListener mDateListener;
    private TimePickerFragment.TimePickerListener mTimeListener;

    private String receiverNamespace;
    private String receiverClassName;

    private TextView dateText, timeText, statusText;

    private boolean dirty = false;

    public ActiveAlarmDetailsFragment() {
        // required empty constructor
    }

    public static ActiveAlarmDetailsFragment newInstance(int alarmID) {
        ActiveAlarmDetailsFragment fragment = new ActiveAlarmDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_ARG_ALARM_ID, alarmID);
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
        receiverNamespace = getResources().getString(R.string.patient_app_namespace);
        receiverClassName = getResources().getString(R.string.patient_app_alarm_receiver);

        if (getArguments() != null) {
            int alarmID = getArguments().getInt(BUNDLE_ARG_ALARM_ID);
            mAlarmMgr = new SpAlarmManager(getActivity());
            mAlarm = mAlarmMgr.get(alarmID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_active_alarm_detail,
                container, false);
        initLabel(rootView);
        initDate(rootView);
        initTime(rootView);
        initStatus(rootView);
        initSave(rootView);
        initDelete(rootView);
        return rootView;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public void updateDate(int year, int month, int day) {
        mAlarm.updateDate(year, month, day);
        dateText.setText(mAlarm.getDateString());
        dirty = true;
    }

    public void updateTime(int hour, int minute) {
        mAlarm.updateTime(hour, minute);
        timeText.setText(mAlarm.getTimeString());
        dirty = true;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void initLabel(final View rootView) {
        TextView labelText = rootView.findViewById(R.id.label_text);
        labelText.setText(mAlarm.getLabel());

        LinearLayout labelLayout = rootView.findViewById(R.id.label_layout);
        labelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Constants.LOG_TAG, "User clicked LABEL field for alarm ID: " + mAlarm.getID());
                Toast.makeText(rootView.getContext(),
                        "LABEL CLICKED", Toast.LENGTH_SHORT).show();
                // TODO - grab the label provided by the user, update the current alarm
                // TODO - lock down editing privileges if alarm is already active
            }
        });
    }

    private void initDate(final View rootView) {
        dateText = rootView.findViewById(R.id.date_text);
        dateText.setText(mAlarm.getDateString());

        LinearLayout dateLayout = rootView.findViewById(R.id.date_layout);
        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Constants.LOG_TAG, "User clicked DATE field for alarm ID: " + mAlarm.getID());
                mDateListener.onDatePickerRequested(mAlarm.getID(), mAlarm.getDate());
                // TODO - lock down editing privileges if alarm is already active
            }
        });
    }

    private void initTime(final View rootView) {
        timeText = rootView.findViewById(R.id.time_text);
        timeText.setText(mAlarm.getTimeString());

        LinearLayout timeLayout = rootView.findViewById(R.id.time_layout);
        timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Constants.LOG_TAG, "User clicked TIME field for alarm ID: " + mAlarm.getID());
                mTimeListener.onTimePickerRequested(mAlarm.getID(), mAlarm.getTime());
                // TODO - lock down editing privileges if alarm is already active
            }
        });
    }

    private void initStatus(final View rootView) {
        statusText = rootView.findViewById(R.id.status_text);
        toggleOnOffMode();

        final Context context = getContext();
        if (context == null) {
            Log.e(Constants.LOG_TAG, "Can't initialize status toggle logic without a valid context.");
            return;
        }

        LinearLayout statusLayout = rootView.findViewById(R.id.status_layout);
        statusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener statusChangeListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Log.i(Constants.LOG_TAG, "User clicked STATUS field for alarm ID: " + mAlarm.getID());
                        Log.d(Constants.LOG_TAG, "Current alarm status: " + mAlarm.getStatus());

                        if (mAlarm.isActive()) {
                            Log.i(Constants.LOG_TAG, "Alarm is already active.  Cancelling "
                                    + "currently scheduled reminders and resetting alarm status.");
                            mAlarmMgr.cancelAllReminders(mAlarm);
                        } else {
                            Log.i(Constants.LOG_TAG, "Alarm status is currently inactive.  "
                                    + "Activating alarm and scheduling reminders.");
                            boolean success = mAlarmMgr.scheduleReminder(mAlarm,
                                    receiverNamespace, receiverClassName);
                            if (!success) {
                                Toast.makeText(rootView.getContext(), "Please schedule an alarm in the future!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        Log.d(Constants.LOG_TAG, "New alarm status: " + mAlarm.getStatus());
                        toggleOnOffMode();
                        mAlarmMgr.update(mAlarm);
                        // mChangeListener.onAlarmDetailsChanged();
                    }};

                new AlertDialog.Builder(context)
                        .setTitle("Toggle Alarm Status")
                        .setMessage("You have chosen to toggle the status of the current alarm.  "
                                + "Are you sure you want to continue?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, statusChangeListener)
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

    private void initSave(final View rootView) {
        FloatingActionButton saveButton = rootView.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlarmMgr.update(mAlarm);
                // mChangeListener.onAlarmDetailsChanged();
                Toast.makeText(rootView.getContext(), "Alarm changes saved!",
                        Toast.LENGTH_SHORT).show();
                dirty = false;
            }
        });
    }

    private void initDelete(final View rootView) {
        FloatingActionButton deleteButton = rootView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlarmMgr.cancelAllReminders(mAlarm);
                mAlarmMgr.delete(mAlarm);
                mChangeListener.onAlarmDetailsChanged();
            }
        });
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void toggleOnOffMode() {
        if (mAlarm.isActive()) {
            Log.i(Constants.LOG_TAG, "Alarm is active!  Setting button to 'off' mode.");
            statusText.setBackgroundColor(Color.GREEN);
        } else {
            Log.i(Constants.LOG_TAG, "Alarm is inactive!  Setting button to 'on' mode.");
            statusText.setBackgroundColor(Color.WHITE);
        }
        statusText.setText(mAlarm.getStatus());
    }

}