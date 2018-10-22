package edu.temple.mci_res_lib2.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import edu.temple.mci_res_lib2.R;
import edu.temple.mci_res_lib2.alarms.MCIAlarmManager;
import edu.temple.mci_res_lib2.utils.Constants;
import edu.temple.mci_res_lib2.utils.GifImageView;

public class CompletionConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completion_confirmation);
        Log.i(Constants.LOG_TAG, "Completion Confirmation activity activated!");

        // Task completion text (and other stuff) is dependent on the exec mode
        TextView confirmationText = findViewById(R.id.confirmationText);
        String displayString =
                MCIAlarmManager.getExecMode().equals(Constants.EXEC_MODES.Advanced)
                ? showAdvancedView()
                : "You have completed your task.  Well done!  Click 'Close' to exit.";
        confirmationText.setText(displayString);

        // Updating the "acknowledge" button is common, no matter the exec mode
        Button acknowledgeButton = findViewById(R.id.acknowledgeButton);
        acknowledgeButton.setText("Close");
        acknowledgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private String showAdvancedView() {
        double alarmCount = MCIAlarmManager.getAlarmList().size();
        double completedAlarmCount = MCIAlarmManager.getCompletedAlarmCount();
        double completionRate = (completedAlarmCount / alarmCount);
        Log.i(Constants.LOG_TAG, "Task completion rate: " + completionRate);

        int progress = (int)(completionRate * 100);
        Log.i(Constants.LOG_TAG, "Task completion progress: " + progress);

        if (progress == 100) {
            GifImageView gifImageView = findViewById(R.id.GifImageView);
            gifImageView.setVisibility(View.VISIBLE);
            return ("You have completed all assigned tasks!  High five!  Click 'Close' to exit.");
        } else {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setProgress(progress);
            progressBar.setVisibility(View.VISIBLE);

            TextView progressText = findViewById(R.id.progressText);
            progressText.setVisibility(View.VISIBLE);

            return ("You have completed " + (int)completedAlarmCount + " out of "
                    +  (int)alarmCount + " tasks.  Well done!  Click 'Close' to exit.");
        }
    }

}