package edu.temple.sp_admin.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import edu.temple.sp_admin.R;
import edu.temple.sp_admin.utils.Constants;

import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.Reminder;
import edu.temple.sp_res_lib.SpAlarmManager;
import edu.temple.sp_res_lib.SpReminderManager;
import edu.temple.sp_res_lib.utils.Constants.REMINDER_TYPE;

public class ActiveAlarmDetailsFragment extends Fragment {

    public interface AlarmDetailChangeListener {
        void onAlarmDetailsChanged();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private SpAlarmManager mAlarmMgr;
    private Alarm mAlarm;

    private AlarmDetailChangeListener mChangeListener;
    private DatePickerFragment.DatePickerListener mDateListener;
    private TimePickerFragment.TimePickerListener mTimeListener;

    private String alarmAction;
    private String receiverNamespace;
    private String receiverClassName;

    private TextView dateText, timeText, statusText;

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
        alarmAction = getResources().getString(R.string.action_alarms);
        receiverNamespace = getResources().getString(R.string.patient_app_namespace);
        receiverClassName = getResources().getString(R.string.patient_app_alarm_receiver);

        if (getArguments() != null) {
            int alarmID = getArguments().getInt(Constants.BUNDLE_ARG_ALARM_ID);
            mAlarmMgr = new SpAlarmManager(getActivity());
            mAlarm = mAlarmMgr.get(alarmID);
            mAlarm.updateIntentSettings(
                    alarmAction,
                    receiverNamespace,
                    receiverClassName
            );
            mAlarmMgr.update(mAlarm);
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
    }

    public void updateTime(int hour, int minute) {
        mAlarm.updateTime(hour, minute);
        timeText.setText(mAlarm.getTimeString());
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void initLabel(final View rootView) {
        final TextView labelText = rootView.findViewById(R.id.label_text);
        labelText.setText(mAlarm.getLabel());

        final Context context = getContext();
        if (context == null) {
            Log.e(Constants.LOG_TAG, "Can't initialize label entry logic without a valid context.");
            return;
        }

        LinearLayout labelLayout = rootView.findViewById(R.id.label_layout);
        labelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO - lock down editing privileges if alarm is already active
                Log.i(Constants.LOG_TAG, "User clicked LABEL field for alarm ID: " + mAlarm.getID());
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Alarm Label");

                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String label = input.getText().toString();
                        mAlarm.updateLabel(label);
                        labelText.setText(label);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
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
                        Log.i(Constants.LOG_TAG, "User clicked ALARM_STATUS field for alarm ID: " + mAlarm.getID());
                        Log.d(Constants.LOG_TAG, "Current alarm status: " + mAlarm.getStatusString());

                        if (mAlarm.isActive()) {
                            Log.i(Constants.LOG_TAG, "Alarm is already active.  Cancelling "
                                    + "currently scheduled reminders and resetting alarm status.");
                            mAlarmMgr.cancelAlarm(mAlarm);
                        } else {
                            Log.i(Constants.LOG_TAG, "Alarm status is currently inactive.  "
                                    + "Activating alarm and scheduling reminders.");
                            boolean success = mAlarmMgr.scheduleAlarm(mAlarm);
                            if (!success) {
                                Toast.makeText(rootView.getContext(), "Please schedule an alarm in the future!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        Log.d(Constants.LOG_TAG, "Inactive alarm status: " + mAlarm.getStatusString());
                        toggleOnOffMode();
                        mAlarmMgr.update(mAlarm);
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
        final Context context = getContext();
        FloatingActionButton saveButton = rootView.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlarmMgr.update(mAlarm);
                Toast.makeText(rootView.getContext(), "Alarm changes saved!",
                        Toast.LENGTH_SHORT).show();

                if (mAlarm.isActive()) {
                    DialogInterface.OnClickListener rescheduleListener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Log.i(Constants.LOG_TAG, "Cancelling and rescheduling all "
                                    + "reminders for alarm ID: " + mAlarm.getID());
                            mAlarmMgr.cancelAlarm(mAlarm);
                            boolean success = mAlarmMgr.scheduleAlarm(mAlarm);
                            if (!success) {
                                Toast.makeText(rootView.getContext(), "Please schedule an alarm in the future!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }};

                    new AlertDialog.Builder(context)
                            .setTitle("Update Scheduled Alarm")
                            .setMessage("You are saving changes to an alarm that has already "
                                    + "been scheduled.  Would you like to reschedule the existing alarm?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, rescheduleListener)
                            .setNegativeButton(android.R.string.no, null).show();
                }
            }
        });
    }

    private void initDelete(final View rootView) {
        FloatingActionButton deleteButton = rootView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlarmMgr.cancelAlarm(mAlarm);
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
        statusText.setText(mAlarm.getStatusString());
    }

}