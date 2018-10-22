package edu.temple.mci_res_lib2.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import edu.temple.mci_res_lib2.alarms.MCINotificationManager;
import edu.temple.mci_res_lib2.utils.Constants;
import edu.temple.mci_res_lib2.alarms.MCIAlarmManager;
import edu.temple.mci_res_lib2.R;

import static edu.temple.mci_res_lib2.utils.Constants.INTENT_PARAM_ALARM_ID;
import static edu.temple.mci_res_lib2.utils.Constants.DEFAULT_ALARM_ID;
import static edu.temple.mci_res_lib2.utils.Constants.NOTIFICATION_PLAY_TIME;

public class CompletionPromptActivity extends AppCompatActivity {

    private static TextView completionText;
    private static Button takePictureButton;
    private static int alarmID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completion_prompt);
        getWindow().addFlags(Constants.ALARM_WINDOW_FLAGS);

        completionText = findViewById(R.id.completionText);
        takePictureButton = findViewById(R.id.takePictureButton);
        Log.i(Constants.LOG_TAG, "Completion prompt activity activated!");

        // attempt to retrieve initialization info from intent
        if (getIntent() != null) {
            // retrieve the alarm ID
            alarmID = getIntent().getIntExtra(INTENT_PARAM_ALARM_ID, DEFAULT_ALARM_ID);
            TextView alarmText = findViewById(R.id.alarmText);
            alarmText.setText("Alarm #" + (alarmID + 1));

            // verify the alarm ID
            if (alarmID != DEFAULT_ALARM_ID) {
                // set up a reminder automatically ... will cancel when the user takes / saves a picture
                if (MCIAlarmManager.setNewCompletionReminder(this, alarmID))
                    populateTaskView(alarmID);
                else {
                    // if user has reached their limit for completion reminders, then close down this window
                    // (alarm manager has already dumped alarm details to file)
                    getWindow().clearFlags(Constants.ALARM_WINDOW_FLAGS);
                    finish();
                }
            }

            // alarm ID retrieved from it wasn't one we could use ... display error screen
            else populateErrorView();
        }

        // no usable intent received ... display error screen
        else populateErrorView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(Constants.LOG_TAG, "Completion prompt activity resumed with Alarm ID: " + alarmID);

        // a little extra logic to make sure the screen wakes up when an alarm is received
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = 1;
        getWindow().setAttributes(params);
    }

    private void populateTaskView(final int alarmID) {
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(CompletionPromptActivity.this, CompletionCameraActivity.class);
                newIntent.putExtra(INTENT_PARAM_ALARM_ID, alarmID);
                startActivity(newIntent);
                finish();
            }
        });

        // only play the notification tone if prompt display conditions are met
        if (getIntent().getBooleanExtra(Constants.INTENT_PARAM_PLAY_TONE, Constants.DEFAULT_PLAY_TONE))
            MCINotificationManager.playNotificationTone(this, NOTIFICATION_PLAY_TIME);
    }

    private void populateErrorView() {
        Log.e(Constants.LOG_TAG, "DID NOT RECEIVE ALARM ID FROM BROADCAST INTENT.  CANNOT CONTINUE.");
        completionText.setText("Could not retrieve alarm details.  Cannot continue with task.");
        takePictureButton.setText("Close");
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}