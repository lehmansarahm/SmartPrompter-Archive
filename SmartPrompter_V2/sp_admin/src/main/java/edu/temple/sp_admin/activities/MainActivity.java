package edu.temple.sp_admin.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import edu.temple.sp_admin.R;

import static edu.temple.sp_admin.SpAdmin.LOG_TAG;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Main Activity created!");
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        Button createNewButton = findViewById(R.id.create_new_button);
        createNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Create-New button clicked.");
                startActivity(new Intent(MainActivity.this,
                        NewAlarmActivity.class));
            }
        });

        Button viewCurrentButton = findViewById(R.id.view_current_button);
        viewCurrentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "View-Current button clicked.");
                startActivity(new Intent(MainActivity.this,
                        CurrentAlarmsActivity.class));
            }
        });

        Button viewPastButton = findViewById(R.id.view_past_button);
        viewPastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "View-Past button clicked.");
                startActivity(new Intent(MainActivity.this,
                        PastAlarmsActivity.class));
            }
        });

        Button surveyConfigButton = findViewById(R.id.configure_survey_button);
        surveyConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Configure-Surveys button clicked.");
                startActivity(new Intent(MainActivity.this,
                        SurveyConfigActivity.class));
            }
        });
    }

    @Override
    public void onPause() {
        Log.i(LOG_TAG, "Main Activity paused!");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "Main Activity destroyed!");
        super.onDestroy();
    }

}