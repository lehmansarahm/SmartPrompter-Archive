package edu.temple.sp_admin.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import edu.temple.sp_admin.R;
import edu.temple.sp_admin.SpAdmin;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.utils.Constants;

import static edu.temple.sp_admin.SpAdmin.LOG_TAG;

public class AlarmDetailsFragment extends Fragment {

    public enum ACTION_BUTTON { Save, Cancel, Delete }

    private OnButtonClickListener mButtonListener;
    private DatePickerFragment.DatePickerListener mDateListener;
    private TimePickerFragment.TimePickerListener mTimeListener;

    private Alarm mAlarm;
    private TextView mDateText, mTimeText;

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public interface OnButtonClickListener {
        void OnButtonClicked(ACTION_BUTTON button, Alarm alarm);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public AlarmDetailsFragment() {
        // Required empty public constructor
    }

    public static AlarmDetailsFragment newInstance(String guid) {
        AlarmDetailsFragment fragment = new AlarmDetailsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_ARG_ALARM_GUID, guid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String alarmGUID = getArguments().getString(Constants.BUNDLE_ARG_ALARM_GUID);
            if (alarmGUID.equals(Constants.DEFAULT_ALARM_GUID)) {
                Calendar now = Calendar.getInstance();
                mAlarm = new Alarm(Constants.DEFAULT_ALARM_GUID, Constants.DEFAULT_ALARM_DESC,
                        now.getTimeInMillis(), Alarm.STATUS.New, false);
            } else {
                Alarm origAlarm = ((SpAdmin)getContext().getApplicationContext()).getAlarm(alarmGUID);
                mAlarm = new Alarm(origAlarm.getGuid(), origAlarm.getDesc(),
                        origAlarm.getTimeInMillis(), origAlarm.getStatus(), origAlarm.isArchived());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alarm_details, container, false);
        initLabel(rootView);
        initDate(rootView);
        initTime(rootView);
        initStatus(rootView);
        initActionButtons(rootView);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnButtonClickListener) {
            mButtonListener = (OnButtonClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnButtonClickListener!");
        }

        if (context instanceof DatePickerFragment.DatePickerListener) {
            mDateListener = (DatePickerFragment.DatePickerListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DatePickerListener!");
        }

        if (context instanceof TimePickerFragment.TimePickerListener) {
            mTimeListener = (TimePickerFragment.TimePickerListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TimePickerListener!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mButtonListener = null;
        mDateListener = null;
        mTimeListener = null;
    }

    public void updateDate(int year, int month, int day) {
        mAlarm.updateDate(year, month, day);
        mDateText.setText(mAlarm.getDateString());
    }

    public void updateTime(int hour, int minute) {
        mAlarm.updateTime(hour, minute);
        mTimeText.setText(mAlarm.getTimeString());
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void initLabel(final View rootView) {
        final TextView labelText = rootView.findViewById(R.id.label_text);
        labelText.setText(mAlarm.getDesc());

        final Context context = getContext();
        if (context == null) {
            Log.e(Constants.LOG_TAG, "Can't initialize label entry logic without a valid context.");
            return;
        }

        LinearLayout labelLayout = rootView.findViewById(R.id.label_layout);
        labelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Constants.LOG_TAG, "User clicked LABEL field for alarm GUID: " + mAlarm.getGuid());
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Alarm Label");

                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String label = input.getText().toString();
                        mAlarm.updateDesc(label);
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


    private void initDate(View rootView) {
        mDateText = rootView.findViewById(R.id.date_text);
        mDateText.setText(mAlarm.getDateString());

        LinearLayout dateLayout = rootView.findViewById(R.id.date_layout);
        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "User clicked DATE field for alarm GUID: " + mAlarm.getGuid());
                mDateListener.onDatePickerRequested(mAlarm.getGuid(), mAlarm.getDate());
            }
        });
    }

    private void initTime(final View rootView) {
        mTimeText = rootView.findViewById(R.id.time_text);
        mTimeText.setText(mAlarm.getTimeString());

        LinearLayout timeLayout = rootView.findViewById(R.id.time_layout);
        timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "User clicked TIME field for alarm GUID: " + mAlarm.getGuid());
                mTimeListener.onTimePickerRequested(mAlarm.getGuid(), mAlarm.getTime());
            }
        });
    }

    private void initStatus(final View rootView) {
        TextView statusText = rootView.findViewById(R.id.status_text);
        statusText.setText(mAlarm.getStatus().toString());
    }

    private void initActionButtons(View rootView) {
        Button saveButton = rootView.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButtonListener.OnButtonClicked(ACTION_BUTTON.Save, mAlarm);
            }
        });

        Button cancelButton = rootView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButtonListener.OnButtonClicked(ACTION_BUTTON.Cancel, mAlarm);
            }
        });

        Button deleteButton = rootView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButtonListener.OnButtonClicked(ACTION_BUTTON.Delete, mAlarm);
            }
        });
    }

}