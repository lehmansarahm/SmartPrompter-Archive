package edu.temple.smartprompter_v2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import edu.temple.smartprompter_v2.R;
import edu.temple.smartprompter_v2.SmartPrompter;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.Log;
import edu.temple.sp_res_lib.utils.MediaUtil;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class ConfirmationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int progressPercentage = getProgress();
        if (progressPercentage < 100) initProgressView(progressPercentage);
        else initConfirmationView();
    }

    private int getProgress() {
        SmartPrompter sp = ((SmartPrompter)getApplication());
        ArrayList<Alarm> todaysAlarms = sp.getTodaysActiveAlarms();
        ArrayList<Alarm> todaysLogs = sp.getTodaysPastAlarms();

        int todaysTotalRecords = (todaysAlarms.size() + todaysLogs.size());
        if (todaysTotalRecords == 0) return 0;
        else {
            double percentage = (Double.valueOf(todaysLogs.size()) / Double.valueOf(todaysTotalRecords));
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
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.ui(LOG_TAG, ConfirmationActivity.this, "Confirmation return button clicked.");
                Intent intent = new Intent(ConfirmationActivity.this, MainActivity.class);
                intent.putExtra(Constants.BUNDLE_TASK_COMPLETE, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(intent);
                finish();
            }
        });
    }

}