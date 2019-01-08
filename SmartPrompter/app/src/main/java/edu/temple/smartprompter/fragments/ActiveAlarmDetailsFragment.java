package edu.temple.smartprompter.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.temple.smartprompter.R;
import edu.temple.smartprompter.TaskAcknowledgementActivity;
import edu.temple.smartprompter.TaskCompletionActivity;
import edu.temple.smartprompter.utils.Constants;

import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.Reminder;
import edu.temple.sp_res_lib.SpAlarmManager;
import edu.temple.sp_res_lib.SpReminderManager;

public class ActiveAlarmDetailsFragment extends Fragment {

    private SpAlarmManager mAlarmMgr;
    private Alarm mAlarm;
    private Reminder mLatestReminder;

    private Intent taskCompletionIntent = null;

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
            mLatestReminder = (new SpReminderManager(getContext())).getLatest(alarmID);
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

        initStatus(rootView);
        initTaskCompletion(rootView);

        return rootView;
    }

    private void initStatus(final View rootView) {
        TextView statusText = rootView.findViewById(R.id.status_text);
        statusText.setText(mAlarm.getStatusString());

        switch (mAlarm.getStatus()) {
            case Active:
            case Complete:
                // do nothing
                break;
            case Unacknowledged:
                Log.i(Constants.LOG_TAG, "Alarm is currently unacknowledged.  Setting "
                        + "task completion intent to point to TaskAcknowledgementActivity.");
                taskCompletionIntent = new Intent(getContext(),
                        TaskAcknowledgementActivity.class);
                statusText.setBackgroundColor(Color.GREEN);
                break;
            case Incomplete:
                Log.i(Constants.LOG_TAG, "Alarm is currently incomplete.  Setting "
                        + "task completion intent to point to TaskCompletionActivity.");
                taskCompletionIntent = new Intent(getContext(),
                        TaskCompletionActivity.class);
                statusText.setBackgroundColor(Color.RED);
                break;
            default:
                Log.e(edu.temple.sp_res_lib.utils.Constants.LOG_TAG,
                        "Unrecognized alarm status: "
                                + mAlarm.getStatusString());
        }
    }

    private void initTaskCompletion(final View rootView) {
        if (taskCompletionIntent == null)
            return;

        taskCompletionIntent.putExtra(Constants.INTENT_EXTRA_REMINDER_ID,
                (mLatestReminder != null
                        ? mLatestReminder.getID()
                        : Constants.DEFAULT_REMINDER_ID));
        taskCompletionIntent.putExtra(Constants.INTENT_EXTRA_ALARM_ID,
                mAlarm.getID());
        taskCompletionIntent.putExtra(Constants.INTENT_EXTRA_ALARM_CURRENT_STATUS,
                mAlarm.getStatusString());

        final Context context = getContext();
        final DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                context.startActivity(taskCompletionIntent);
            }
        };

        LinearLayout statusLayout = rootView.findViewById(R.id.status_layout);
        statusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Constants.LOG_TAG, "User clicked ALARM_STATUS field for alarm ID: "
                        + mAlarm.getID());
                new AlertDialog.Builder(context)
                        .setTitle("Initiate Alarm Task")
                        .setMessage("This alarm task is incomplete.  Would you like to "
                                + "resume task completion?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, clickListener)
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

}