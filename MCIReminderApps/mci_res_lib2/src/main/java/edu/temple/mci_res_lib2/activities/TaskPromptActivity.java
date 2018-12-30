package edu.temple.mci_res_lib2.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

import edu.temple.mci_res_lib2.alarms.Alarm;
import edu.temple.mci_res_lib2.alarms.MCINotificationManager;
import edu.temple.mci_res_lib2.utils.Constants;
import edu.temple.mci_res_lib2.alarms.MCIAlarmManager;
import edu.temple.mci_res_lib2.R;

import static edu.temple.mci_res_lib2.utils.Constants.INTENT_PARAM_ALARM_ID;
import static edu.temple.mci_res_lib2.utils.Constants.DEFAULT_ALARM_ID;
import static edu.temple.mci_res_lib2.utils.Constants.NOTIFICATION_PLAY_TIME;

public class TaskPromptActivity extends AppCompatActivity {

    private static TextView taskText;
    private static Button acknowledgeButton;
    private static int alarmID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_prompt);
        // getWindow().addFlags(Constants.ALARM_WINDOW_FLAGS);

        taskText = findViewById(R.id.taskText);
        acknowledgeButton = findViewById(R.id.acknowledgeButton);
        Log.i(Constants.LOG_TAG, "Task prompt activity activated!");

        // attempt to retrieve initialization info from intent
        if (getIntent() != null) {
            // retrieve the alarm ID
            alarmID = getIntent().getIntExtra(INTENT_PARAM_ALARM_ID, DEFAULT_ALARM_ID);
            TextView alarmText = findViewById(R.id.alarmText);
            alarmText.setText("Alarm #" + (alarmID + 1));

            // verify the alarm ID
            if (alarmID != DEFAULT_ALARM_ID) {
                // if this is only a simple alarm ... display static reminder screen
                if (MCIAlarmManager.getExecMode().equals(Constants.EXEC_MODES.Simple)) {
                    populateSimpleView(alarmID);
                }

                // if this is an advanced alarm, set up a reminder automatically ...
                // will cancel if the user acknowledges
                else if (MCIAlarmManager.getExecMode().equals(Constants.EXEC_MODES.Advanced)) {
                    if (MCIAlarmManager.setNewAcknowledgementReminder(this, alarmID))
                        populateTaskView(alarmID);
                    else {
                        // if user has reached their limit for acknowledgement reminders, then close down this window
                        // (alarm manager has already dumped alarm details to file)
                        // getWindow().clearFlags(Constants.ALARM_WINDOW_FLAGS);
                        finish();
                    }
                }

                // if neither of above conditions are met, display error screen
                else {
                    Log.e(Constants.LOG_TAG, "UNKNOWN EXEC MODE: "
                            + MCIAlarmManager.getExecMode().toString() + ".  CANNOT CONTINUE.");
                    populateErrorView();
                }
            }

            // alarm ID retrieved from it wasn't one we could use ... display error screen
            else {
                Log.e(Constants.LOG_TAG, "RECEIVED ALARM ID FROM BROADCAST RECEIVER, BUT WAS INVALID.  CANNOT CONTINUE.");
                populateErrorView();
            }
        }

        // no intent received ... display error screen
        else {
            Log.e(Constants.LOG_TAG, "RECEIVED NULL INTENT FROM BROADCAST RECEIVER.  CANNOT CONTINUE.");
            populateErrorView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(Constants.LOG_TAG, "Task acknowledgement prompt activity resumed with Alarm ID: " + alarmID);

        // a little extra logic to make sure the screen wakes up when an alarm is received
        // WindowManager.LayoutParams params = getWindow().getAttributes();
        // params.screenBrightness = 1;
        // getWindow().setAttributes(params);
    }

    private void populateSimpleView(final int alarmID) {
        // we have received a simple alarm ... display a simplified task prompt view
        Log.i(Constants.LOG_TAG, "Processing a simple alarm.  No need to do anything else.");
        taskText.setText("Time to drink water!");
        acknowledgeButton.setText("All done!");
        acknowledgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MCIAlarmManager.updateAlarmStatus(TaskPromptActivity.this, alarmID, Alarm.STATUS.Complete);
                Intent newIntent = new Intent(TaskPromptActivity.this, CompletionConfirmationActivity.class);
                startActivity(newIntent);
                finish();
            }
        });

        // only play the notification tone if prompt display conditions are met
        MCINotificationManager.playNotificationTone(this, NOTIFICATION_PLAY_TIME);
    }

    private void populateTaskView(final int alarmID) {
        // we have received an advanced alarm ... display the full task prompt view
        acknowledgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MCIAlarmManager.cancelAcknowledgementReminder(TaskPromptActivity.this, alarmID);
                MCIAlarmManager.updateAlarmStatus(TaskPromptActivity.this, alarmID, Alarm.STATUS.Incomplete);
                MCIAlarmManager.saveAlarmListToSharedPrefs(TaskPromptActivity.this);
                Log.i(Constants.LOG_TAG, "Alarm with ID: " + alarmID + " has been marked as 'acknowledged' (but incomplete).");

                Intent newIntent = new Intent(TaskPromptActivity.this, CompletionPromptActivity.class);
                newIntent.putExtra(INTENT_PARAM_ALARM_ID, alarmID);
                newIntent.putExtra(Constants.INTENT_PARAM_PLAY_TONE, false);
                startActivity(newIntent);
                finish();
            }
        });

        // only play the notification tone if prompt display conditions are met
        MCINotificationManager.playNotificationTone(this, NOTIFICATION_PLAY_TIME);
    }

    private void populateErrorView() {
        taskText.setText("Could not retrieve alarm details.  Cannot continue with task.");
        acknowledgeButton.setText("Close");
        acknowledgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}