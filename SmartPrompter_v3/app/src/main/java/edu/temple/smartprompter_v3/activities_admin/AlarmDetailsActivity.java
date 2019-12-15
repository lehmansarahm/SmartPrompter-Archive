package edu.temple.smartprompter_v3.activities_admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import edu.temple.smartprompter_v3.BaseActivity;
import edu.temple.smartprompter_v3.R;
import edu.temple.smartprompter_v3.data.Alarm;
import edu.temple.smartprompter_v3.data.FirebaseConnector;
import edu.temple.smartprompter_v3.fragments.DatePickerFragment;
import edu.temple.smartprompter_v3.fragments.TimePickerFragment;
import edu.temple.smartprompter_v3.utils.Constants;

import static edu.temple.smartprompter_v3.utils.Constants.LOG_TAG;

public class AlarmDetailsActivity extends BaseActivity implements
        DatePickerFragment.DatePickerListener, TimePickerFragment.TimePickerListener {

    private Alarm mAlarm;
    private String mAlarmGUID;
    private TextView mDateText, mTimeText;

    @Override
    protected void showLoggedInView() {
        Log.i(Constants.LOG_TAG, "showLoggedInView method called for class: "
                + this.getClass().getSimpleName());
        setContentView(R.layout.activity_alarm_details);

        mAlarmGUID = getIntent().getStringExtra(Constants.BUNDLE_ARG_ALARM_GUID);
        if (mAlarmGUID == null || mAlarmGUID.equals("")) {
            Log.e(Constants.LOG_TAG, "SHOW DEFAULT DETAILS FOR NEW ALARM.");
            TextView headerTv = findViewById(R.id.alarm_details_header);
            headerTv.setText("New " + headerTv.getText());
            mAlarm = new Alarm();
            mAlarmGUID = mAlarm.getGuid();
            initialize();
        } else {
            Log.i(Constants.LOG_TAG, "Show current details for existing alarm with GUID: " + mAlarmGUID);
            FirebaseConnector.getAlarmByGuid(mAlarmGUID, result -> {
                if (result != null) mAlarm = (Alarm)result;
                else {
                    Log.e(LOG_TAG, "Something went wrong while attempting to "
                            + "retrieve alarm with GUID: " + mAlarmGUID
                            + ".  Displaying default record.");
                    mAlarm = new Alarm();
                    mAlarmGUID = mAlarm.getGuid();
                }
                initialize();
            });
        }
    }


    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------
    //      DatePickerListener, TimePickerListener methods
    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onDatePicked(String alarmGuid, int year, int month, int day) {
        fbaEventLogger.fieldUpdate(this.getClass(), "AlarmDate", 0,
                month + "/" + day + "/" + year, alarmGuid);
        mAlarm.updateAlarmDate(year, month, day);
        mDateText.setText(mAlarm.getAlarmDateString());
    }

    @Override
    public void onTimePicked(String alarmGuid, int hourOfDay, int minute) {
        fbaEventLogger.fieldUpdate(this.getClass(), "AlarmTime", 0,
                hourOfDay + ":" + minute, alarmGuid);
        mAlarm.updateAlarmTime(hourOfDay, minute);
        mTimeText.setText(mAlarm.getAlarmTimeString());
    }


    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------


    private void initialize() {
        initLabel();
        initDate();
        initTime();
        initStatus();
        initActionButtons();
    }

    private void initLabel() {
        final TextView labelText = findViewById(R.id.label_text);
        labelText.setText(mAlarm.getDesc());

        LinearLayout labelLayout = findViewById(R.id.label_layout);
        labelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fbaEventLogger.fieldClick(this.getClass(), "AlarmDescLabel",
                        view.getId(), mAlarm.getDesc(), mAlarm);

                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmDetailsActivity.this);
                builder.setTitle("Alarm Label");

                final EditText input = new EditText(AlarmDetailsActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fbaEventLogger.buttonClick(this.getClass(), "DialogOk", which);
                        String label = input.getText().toString();
                        mAlarm.updateDesc(label);
                        labelText.setText(label);
                        Log.i(LOG_TAG, "Updated alarm label: " + label
                                + " \t for guid: " + mAlarm.getGuid());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fbaEventLogger.buttonClick(this.getClass(), "DialogCancel", which);
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    private void initDate() {
        mDateText = findViewById(R.id.date_text);
        mDateText.setText(mAlarm.getAlarmDateString());

        LinearLayout dateLayout = findViewById(R.id.date_layout);
        dateLayout.setOnClickListener(view -> {
            int[] date = mAlarm.getAlarmDate();
            String dateString = date[1] + "/" + date[2] + "/" + date[0];

            Log.i(LOG_TAG, "Attempting to launch date picker for alarm guid: " + mAlarmGUID);
            fbaEventLogger.fieldClick(this.getClass(), "AlarmDate", 0,
                    dateString, mAlarmGUID);

            DialogFragment newFragment = DatePickerFragment.newInstance(mAlarmGUID, date);
            newFragment.show(getSupportFragmentManager(), "datePicker");
        });
    }

    private void initTime() {
        mTimeText = findViewById(R.id.time_text);
        mTimeText.setText(mAlarm.getAlarmTimeString());

        LinearLayout timeLayout = findViewById(R.id.time_layout);
        timeLayout.setOnClickListener(view -> {
            int[] time = mAlarm.getAlarmTime();
            fbaEventLogger.fieldClick(this.getClass(), "AlarmTime", 0,
                    time[0] + ":" + time[1], mAlarmGUID);
            DialogFragment newFragment = TimePickerFragment.newInstance(mAlarmGUID, time);
            newFragment.show(getSupportFragmentManager(), "timePicker");
        });
    }

    private void initStatus() {
        TextView statusText = findViewById(R.id.status_text);
        statusText.setText(mAlarm.getStatus().toString());
    }

    private void initActionButtons() {
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fbaEventLogger.buttonClick(this.getClass(), "Save", mAlarm);

                if (mAlarm.hasAlarmTimePassed()) {
                    Log.e(Constants.LOG_TAG, "Cannot set alarm for time in the past!");
                    Toast.makeText(AlarmDetailsActivity.this, "Cannot set alarm for time "
                            + "in the past!", Toast.LENGTH_LONG).show();
                } else {
                    mAlarm.updateUserEmail(mFbAuth.getCurrentUser().getEmail());
                    FirebaseConnector.saveAlarm(mAlarm.getFbProperties(), task -> {
                        if (task.isSuccessful()) {
                            Log.i(LOG_TAG, "Successfully saved new alarm with GUID: "
                                    + task.getResult().getId());
                            startActivity(new Intent(AlarmDetailsActivity.this,
                                    AlarmListActivity.class));
                            finish();
                        } else {
                            Toast.makeText(AlarmDetailsActivity.this,
                                    "Something went wrong.  Please try again",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fbaEventLogger.buttonClick(this.getClass(), "Cancel", mAlarm);
                showLoggedInView();
            }
        });

        Button deleteButton = findViewById(R.id.delete_button);
        if (mAlarmGUID.equals(Constants.DEFAULT_ALARM_GUID))
            deleteButton.setEnabled(false);
        else {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fbaEventLogger.buttonClick(this.getClass(), "Delete", mAlarm);
                    if (!mAlarm.getGuid().equals(Constants.DEFAULT_ALARM_GUID)) {
                        final String alarmGUID = mAlarm.getGuid();
                        FirebaseConnector.deleteAlarm(mAlarm.getGuid(), task -> {
                            if (task.isSuccessful()) {
                                Log.i(LOG_TAG, "Successfully deleted alarm with GUID: "
                                        + alarmGUID);
                                startActivity(new Intent(AlarmDetailsActivity.this,
                                        AlarmListActivity.class));
                                finish();
                            } else {
                                Toast.makeText(AlarmDetailsActivity.this,
                                        "Something went wrong.  Please try again",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else Log.e(LOG_TAG, "Can't delete a non-existent record!");
                }
            });
        }
    }

}