package edu.temple.smartprompter_v3.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import edu.temple.smartprompter_v3.res_lib.utils.MediaUtil;

public class ConfigureSettingsActivity extends BaseActivity implements MediaUtil.MediaListener {

    private MediaUtil.AUDIO_TYPE selectedType = MediaUtil.AUDIO_TYPE.Alarm;
    private Button recordButton, playButton, deleteButton;
    private boolean record = true, play = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_audio);

        initializeControlButtons();
        initializeRadioButtons();

        boolean customAudioExists = MediaUtil.doesCustomAudioExist(this, selectedType);
        deleteButton.setEnabled(customAudioExists);
    }

    private void initializeRadioButtons() {
        RadioButton radioAlarm = findViewById(R.id.radio_alarm);
        radioAlarm.setOnClickListener(view -> {
            Log.i(LOG_TAG, "User has opted to update the Alarm audio.");
            play = false;
            togglePlay();

            selectedType = MediaUtil.AUDIO_TYPE.Alarm;
            boolean customAudioExists = MediaUtil.doesCustomAudioExist(this, selectedType);
            deleteButton.setEnabled(customAudioExists);
        });

        RadioButton radioReminder = findViewById(R.id.radio_reminder);
        radioReminder.setOnClickListener(view -> {
            Log.i(LOG_TAG, "User has opted to update the Reminder audio.");
            play = false;
            togglePlay();

            selectedType = MediaUtil.AUDIO_TYPE.Reminder;
            boolean customAudioExists = MediaUtil.doesCustomAudioExist(this, selectedType);
            deleteButton.setEnabled(customAudioExists);
        });

        RadioButton radioReward = findViewById(R.id.radio_reward);
        radioReward.setOnClickListener(view -> {
            Log.i(LOG_TAG, "User has opted to update the Reward audio.");
            play = false;
            togglePlay();

            selectedType = MediaUtil.AUDIO_TYPE.Reward;
            boolean customAudioExists = MediaUtil.doesCustomAudioExist(this, selectedType);
            deleteButton.setEnabled(customAudioExists);
        });
    }

    private void initializeControlButtons() {
        recordButton = findViewById(R.id.record_button);
        recordButton.setOnClickListener(view -> {
            Log.i(LOG_TAG, "User clicked the record button.");
            toggleRecord();
        });

        playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(view -> {
            Log.i(LOG_TAG, "User clicked the play button.");
            togglePlay();
        });

        deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(view -> {
            Log.i(LOG_TAG, "User clicked the delete button.");
            MediaUtil.deleteAudio(this, selectedType);
            Toast.makeText(ConfigureSettingsActivity.this, "Audio deleted!",
                    Toast.LENGTH_LONG).show();
        });
    }

    private void toggleRecord() {
        if (record) {
            MediaUtil.recordAudio(this, selectedType);
            recordButton.setText("Stop");
            record = false;
        } else {
            Toast.makeText(ConfigureSettingsActivity.this, "New audio recorded!",
                    Toast.LENGTH_LONG).show();
            MediaUtil.stopRecord();
            recordButton.setText("Record");
            deleteButton.setEnabled(true);
            record = true;
        }
    }

    private void togglePlay() {
        if (play) {
            MediaUtil.playAudio(ConfigureSettingsActivity.this, selectedType, this);
            playButton.setText("Stop");
            play = false;
        } else {
            MediaUtil.stopAudio();
            playButton.setText("Play");
            play = true;
        }
    }

    @Override
    public void audioPlayComplete() {
        play = false;
        togglePlay();
    }
}