package edu.temple.sp_admin.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.temple.sp_admin.R;
import edu.temple.sp_admin.utils.Constants;
import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.SpAlarmManager;

public class IncompleteAlarmDetailsFragment extends Fragment {

    private static final String BUNDLE_ARG_ALARM_ID = "bundle_arg_position";

    private SpAlarmManager mAlarmMgr;
    private Alarm mAlarm;

    private TextView dateText, timeText, statusText, ackText, compText;

    public IncompleteAlarmDetailsFragment() {
        // required empty constructor
    }

    public static IncompleteAlarmDetailsFragment newInstance(int alarmID) {
        IncompleteAlarmDetailsFragment fragment = new IncompleteAlarmDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_ARG_ALARM_ID, alarmID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int alarmID = getArguments().getInt(BUNDLE_ARG_ALARM_ID);
            mAlarmMgr = new SpAlarmManager(getActivity());
            mAlarm = mAlarmMgr.get(alarmID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_incomplete_alarm_detail,
                container, false);
        initLabel(rootView);
        initDate(rootView);
        initTime(rootView);
        initStatus(rootView);
        initAckTime(rootView);
        initCompTime(rootView);
        return rootView;
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
                Log.i(Constants.LOG_TAG, "User clicked DATE field for alarm ID: "
                        + mAlarm.getID());
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
                Log.i(Constants.LOG_TAG, "User clicked TIME field for alarm ID: "
                        + mAlarm.getID());
            }
        });
    }

    private void initStatus(final View rootView) {
        statusText = rootView.findViewById(R.id.status_text);
        toggleOnOffMode();

        LinearLayout statusLayout = rootView.findViewById(R.id.status_layout);
        statusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Constants.LOG_TAG, "User clicked STATUS field for alarm ID: "
                        + mAlarm.getID());
            }
        });
    }

    private void initAckTime(final View rootView) {
        ackText = rootView.findViewById(R.id.ack_text);
        String rawAckTime = mAlarm.getTimeAcknowledged();

        if (rawAckTime == null || rawAckTime.isEmpty())
            ackText.setText(R.string.not_applicable);
        else ackText.setText(mAlarm.getTimeAcknowledged());

        LinearLayout ackLayout = rootView.findViewById(R.id.ack_layout);
        ackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Constants.LOG_TAG, "User clicked TIME ACKNOWLEDGED field "
                        + "for alarm ID: " + mAlarm.getID());
            }
        });
    }

    private void initCompTime(final View rootView) {
        compText = rootView.findViewById(R.id.comp_text);
        String rawCompTime = mAlarm.getTimeCompleted();

        if (rawCompTime == null || rawCompTime.isEmpty())
            compText.setText(R.string.not_applicable);
        else compText.setText(mAlarm.getTimeCompleted());

        LinearLayout compLayout = rootView.findViewById(R.id.comp_layout);
        compLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Constants.LOG_TAG, "User clicked TIME COMPLETED field "
                        + "for alarm ID: " + mAlarm.getID());
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