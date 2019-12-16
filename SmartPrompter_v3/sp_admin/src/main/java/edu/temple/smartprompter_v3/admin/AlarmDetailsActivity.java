package edu.temple.smartprompter_v3.admin;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import edu.temple.smartprompter_v3.res_lib.data.Alarm;
import edu.temple.smartprompter_v3.res_lib.data.FirebaseConnector;
import edu.temple.smartprompter_v3.res_lib.fragments.DatePickerFragment;
import edu.temple.smartprompter_v3.res_lib.fragments.TimePickerFragment;
import edu.temple.smartprompter_v3.res_lib.utils.Constants;

public class AlarmDetailsActivity extends BaseActivity implements
        DatePickerFragment.DatePickerListener, TimePickerFragment.TimePickerListener {

    private Alarm mAlarm;
    private String mAlarmGUID;
    private TextView mDateText, mTimeText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_details);
        showAlarmDetails();
    }


    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------
    //      DatePickerListener, TimePickerListener methods
    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onDatePicked(String alarmGuid, int year, int month, int day) {
        mFbaEventLogger.fieldUpdate(this.getClass(), "AlarmDate", 0,
                month + "/" + day + "/" + year, alarmGuid);
        mAlarm.updateAlarmDate(year, month, day);
        mDateText.setText(mAlarm.getAlarmDateString());
    }

    @Override
    public void onTimePicked(String alarmGuid, int hourOfDay, int minute) {
        mFbaEventLogger.fieldUpdate(this.getClass(), "AlarmTime", 0,
                hourOfDay + ":" + minute, alarmGuid);
        mAlarm.updateAlarmTime(hourOfDay, minute);
        mTimeText.setText(mAlarm.getAlarmTimeString());
    }


    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------


    private void showAlarmDetails() {
        mAlarmGUID = getIntent().getStringExtra(Constants.BUNDLE_ARG_ALARM_GUID);
        if (mAlarmGUID == null || mAlarmGUID.equals("")) {
            Log.e(Constants.LOG_TAG, "SHOW DEFAULT DETAILS FOR NEW ALARM.");
            TextView headerTv = findViewById(R.id.alarm_details_header);
            headerTv.setText("New Alarm Details");
            mAlarm = new Alarm();
            mAlarmGUID = mAlarm.getGuid();
            initialize();
        } else {
            Log.i(BaseActivity.LOG_TAG, "Show current details for existing alarm with GUID: " + mAlarmGUID);
            FirebaseConnector.getAlarmByGuid(mAlarmGUID, result -> {
                if (result != null) mAlarm = (Alarm)result;
                else {
                    Log.e(BaseActivity.LOG_TAG, "Something went wrong while attempting to "
                            + "retrieve alarm with GUID: " + mAlarmGUID
                            + ".  Displaying default record.");
                    mAlarm = new Alarm();
                    mAlarmGUID = mAlarm.getGuid();
                }
                initialize();
            });
        }
    }

    private void initialize() {
        initLabel();
        initSaveButton();
        initCancelButton();
        initDeleteButton();

        mDateText = findViewById(R.id.date_text);
        mDateText.setText(mAlarm.getAlarmDateString());

        LinearLayout dateLayout = findViewById(R.id.date_layout);
        dateLayout.setOnClickListener(view -> {
            int[] date = mAlarm.getAlarmDate();
            String dateString = date[1] + "/" + date[2] + "/" + date[0];

            Log.i(BaseActivity.LOG_TAG, "Attempting to launch date picker for alarm guid: " + mAlarmGUID);
            mFbaEventLogger.fieldClick(this.getClass(), "AlarmDate", 0,
                    dateString, mAlarmGUID);

            DialogFragment newFragment = DatePickerFragment.newInstance(mAlarmGUID, date);
            newFragment.show(getSupportFragmentManager(), "datePicker");
        });

        mTimeText = findViewById(R.id.time_text);
        mTimeText.setText(mAlarm.getAlarmTimeString());

        LinearLayout timeLayout = findViewById(R.id.time_layout);
        timeLayout.setOnClickListener(view -> {
            int[] time = mAlarm.getAlarmTime();
            mFbaEventLogger.fieldClick(this.getClass(), "AlarmTime", 0,
                    time[0] + ":" + time[1], mAlarmGUID);
            DialogFragment newFragment = TimePickerFragment.newInstance(mAlarmGUID, time);
            newFragment.show(getSupportFragmentManager(), "timePicker");
        });

        TextView statusText = findViewById(R.id.status_text);
        statusText.setText(mAlarm.getStatus().toString());

    }

    private void initLabel() {
        final TextView labelText = findViewById(R.id.label_text);
        labelText.setText(mAlarm.getDesc());

        LinearLayout labelLayout = findViewById(R.id.label_layout);
        labelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFbaEventLogger.fieldClick(this.getClass(), "AlarmDescLabel",
                        view.getId(), mAlarm.getDesc(), mAlarm);

                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmDetailsActivity.this);
                builder.setTitle("Alarm Label");

                final EditText input = new EditText(AlarmDetailsActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFbaEventLogger.buttonClick(this.getClass(), "DialogOk", which);
                        String label = input.getText().toString();
                        mAlarm.updateDesc(label);
                        labelText.setText(label);
                        Log.i(BaseActivity.LOG_TAG, "Updated alarm label: " + label
                                + " \t for guid: " + mAlarm.getGuid());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFbaEventLogger.buttonClick(this.getClass(), "DialogCancel", which);
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    private void initSaveButton() {
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFbaEventLogger.buttonClick(this.getClass(), "Save", mAlarm);
                if (mAlarm.hasAlarmTimePassed()) {
                    Log.e(Constants.LOG_TAG, "Cannot setAlarm alarm for time in the past!");
                    Toast.makeText(AlarmDetailsActivity.this, "Cannot setAlarm alarm for time "
                            + "in the past!", Toast.LENGTH_LONG).show();
                } else {
                    Log.i(LOG_TAG, "Saving updates to record with GUID: " + mAlarm.getGuid());
                    mAlarm.updateUserEmail(mFbAuth.getCurrentUser().getEmail());
                    FirebaseConnector.saveAlarm(mAlarm);

                    Log.i(LOG_TAG, "Sending broadcast to patient application.");
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(getString(R.string.event_alarms_ready));
                    broadcastIntent.putExtra(Constants.BUNDLE_ARG_ALARM_GUID, mAlarm.getGuid());
                    broadcastIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    broadcastIntent.setComponent(
                            new ComponentName("edu.temple.smartprompter_v3",
                                    "edu.temple.smartprompter_v3.receivers.AlarmAlertReceiver"));
                    sendBroadcast(broadcastIntent);

                    Log.i(LOG_TAG, "Returning to alarm list activity.");
                    startActivity(new Intent(AlarmDetailsActivity.this,
                            AlarmListActivity.class));
                    finish();
                }
            }
        });
    }

    private void initCancelButton() {
        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFbaEventLogger.buttonClick(this.getClass(), "Cancel", mAlarm);
                showAlarmDetails();
            }
        });
    }

    private void initDeleteButton() {
        Button deleteButton = findViewById(R.id.delete_button);
        if (mAlarmGUID.equals(Constants.DEFAULT_ALARM_GUID))
            deleteButton.setEnabled(false);
        else {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFbaEventLogger.buttonClick(this.getClass(), "Delete", mAlarm);
                    FirebaseConnector.deleteAlarm(mAlarm.getGuid());
                    startActivity(new Intent(AlarmDetailsActivity.this,
                            AlarmListActivity.class));
                    finish();
                }
            });
        }
    }

}