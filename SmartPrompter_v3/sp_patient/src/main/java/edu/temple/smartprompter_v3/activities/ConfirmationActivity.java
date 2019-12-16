package edu.temple.smartprompter_v3.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.temple.smartprompter_v3.R;
import edu.temple.smartprompter_v3.res_lib.SpController;
import edu.temple.smartprompter_v3.res_lib.data.Alarm;
import edu.temple.smartprompter_v3.res_lib.data.FirebaseConnector;
import edu.temple.smartprompter_v3.res_lib.utils.Constants;
import edu.temple.smartprompter_v3.res_lib.utils.MediaUtil;

public class ConfirmationActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String email = mFbAuth.getCurrentUser().getEmail();
        FirebaseConnector.getAlarmsByEmail(email, results -> {
            List<Alarm> allAlarms = (List<Alarm>)(Object)results;
            int progressPercentage = getProgress(allAlarms);
            if (progressPercentage < 100) initProgressView(progressPercentage);
            else initConfirmationView();
        });
    }

    private int getProgress(List<Alarm> allAlarms) {
        List<Alarm> todaysLogs = new ArrayList<>();
        List<Alarm> todaysAlarms = new ArrayList<>();

        for (Alarm alarm : allAlarms) {
            Log.i(LOG_TAG, "Evaluating alarm: " + alarm.toString());
            if (alarm.isAlarmScheduledForToday()) {
                if (alarm.getStatus().equals(Alarm.STATUS.Complete))
                    todaysLogs.add(alarm);
                else todaysAlarms.add(alarm);
            }
        }

        Log.i(BaseActivity.LOG_TAG, "Total number of complete alarm tasks: " + todaysLogs);
        Log.i(BaseActivity.LOG_TAG, "Total number of incomplete alarm tasks: " + todaysAlarms);

        int todaysTotalRecords = (todaysAlarms.size() + todaysLogs.size());
        if (todaysTotalRecords == 0) return 0;
        else {
            double percentage = ((double) todaysLogs.size() / (double) todaysTotalRecords);
            return (int)(percentage * 100);
        }
    }

    private void initProgressView(int progressPercentage) {
        setContentView(R.layout.activity_reward_progress);
        ProgressBar bar = findViewById(R.id.progressBar);
        bar.setProgress(progressPercentage);

        TextView ppText = findViewById(R.id.progress_percentage_text);
        ppText.setText("You've completed " + progressPercentage + "% of your tasks for today!");
        initReturnButton();
    }

    private void initConfirmationView() {
        setContentView(R.layout.activity_reward_confirmation);
        ImageView confirmationImage = findViewById(R.id.confirmation_image);
        confirmationImage.setImageResource(R.drawable.high_five_dog);
        initReturnButton();

        MediaUtil.playAlarmAlerts(this, MediaUtil.AUDIO_TYPE.Reward, null);
    }

    private void initReturnButton() {
        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(view -> {
            Log.i(LOG_TAG, "Confirmation return button clicked.");
            Intent intent = new Intent(ConfirmationActivity.this, MainActivity.class);
            intent.putExtra(Constants.BUNDLE_TASK_COMPLETE, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);
            finish();
        });
    }

}