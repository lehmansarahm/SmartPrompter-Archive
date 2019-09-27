package edu.temple.sp_admin.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import edu.temple.sp_admin.R;
import edu.temple.sp_res_lib.utils.EmailUtil;
import edu.temple.sp_res_lib.utils.Log;
import edu.temple.sp_res_lib.utils.MediaUtil;

import static edu.temple.sp_admin.SpAdmin.LOG_TAG;

public class AudioEmailConfigActivity extends BaseActivity implements MediaUtil.MediaListener {

    private MediaUtil.AUDIO_TYPE selectedType = MediaUtil.AUDIO_TYPE.Alarm;
    private Button recordButton, playButton, deleteButton;
    private boolean record = true, play = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_config);

        initializeControlButtons();
        initializeRadioButtons();
        initializeEmailButton();

        boolean customAudioExists =
                MediaUtil.doesCustomAudioExist(AudioEmailConfigActivity.this, selectedType);
        deleteButton.setEnabled(customAudioExists);
    }

    private void initializeRadioButtons() {
        RadioButton radioAlarm = findViewById(R.id.radio_alarm);
        radioAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.ui(LOG_TAG, AudioEmailConfigActivity.this,
                        "User has opted to update the Alarm audio.");
                play = false;
                togglePlay();

                selectedType = MediaUtil.AUDIO_TYPE.Alarm;
                boolean customAudioExists =
                        MediaUtil.doesCustomAudioExist(AudioEmailConfigActivity.this, selectedType);
                deleteButton.setEnabled(customAudioExists);
            }
        });

        RadioButton radioReminder = findViewById(R.id.radio_reminder);
        radioReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.ui(LOG_TAG, AudioEmailConfigActivity.this,
                        "User has opted to update the Reminder audio.");
                play = false;
                togglePlay();

                selectedType = MediaUtil.AUDIO_TYPE.Reminder;
                boolean customAudioExists =
                        MediaUtil.doesCustomAudioExist(AudioEmailConfigActivity.this, selectedType);
                deleteButton.setEnabled(customAudioExists);
            }
        });

        RadioButton radioReward = findViewById(R.id.radio_reward);
        radioReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.ui(LOG_TAG, AudioEmailConfigActivity.this,
                        "User has opted to update the Reward audio.");
                play = false;
                togglePlay();

                selectedType = MediaUtil.AUDIO_TYPE.Reward;
                boolean customAudioExists =
                        MediaUtil.doesCustomAudioExist(AudioEmailConfigActivity.this, selectedType);
                deleteButton.setEnabled(customAudioExists);
            }
        });
    }

    private void initializeControlButtons() {
        recordButton = findViewById(R.id.record_button);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.ui(LOG_TAG, AudioEmailConfigActivity.this,
                        "User clicked the record button.");
                toggleRecord();
            }
        });

        playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.ui(LOG_TAG, AudioEmailConfigActivity.this,
                        "User clicked the play button.");
                togglePlay();
            }
        });

        deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.ui(LOG_TAG, AudioEmailConfigActivity.this,
                        "User clicked the delete button.");
                MediaUtil.deleteAudio(AudioEmailConfigActivity.this, selectedType);
                Toast.makeText(AudioEmailConfigActivity.this, "Audio deleted!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void toggleRecord() {
        if (record) {
            MediaUtil.recordAudio(AudioEmailConfigActivity.this, selectedType);
            recordButton.setText("Stop");
            record = false;
        } else {
            Toast.makeText(AudioEmailConfigActivity.this, "New audio recorded!",
                    Toast.LENGTH_LONG).show();
            MediaUtil.stopRecord();
            recordButton.setText("Record");
            deleteButton.setEnabled(true);
            record = true;
        }
    }

    private void togglePlay() {
        if (play) {
            MediaUtil.playAudio(AudioEmailConfigActivity.this, selectedType, this);
            playButton.setText("Stop");
            play = false;
        } else {
            MediaUtil.stopAudio();
            playButton.setText("Play");
            play = true;
        }
    }

    private void initializeEmailButton() {
        final Button emailButton = findViewById(R.id.export_button);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO - schedule to run at midnight ... archive all emailed documents
                EmailUtil.send(AudioEmailConfigActivity.this,
                        "Daily Logs", "\n \n \n SmartPrompter - a Temple University application");
                Toast.makeText(AudioEmailConfigActivity.this, "Log email sent!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void audioPlayComplete() {
        play = false;
        togglePlay();
    }
}