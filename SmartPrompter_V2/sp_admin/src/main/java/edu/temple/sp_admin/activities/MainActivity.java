package edu.temple.sp_admin.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import edu.temple.sp_admin.R;

import static edu.temple.sp_admin.SpAdmin.LOG_TAG;

public class MainActivity extends BaseActivity {

    private static final int PERMISSION_REQUEST_CODE = 429;
    private static final String[] PERMISSIONS = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private Button createNewButton, viewCurrentButton,
            viewPastButton, configureQuestionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Main Activity created!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initButtons();
        if (checkPermissions()) setButtonStatus(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                setButtonStatus(grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED);
            }
        }
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    protected boolean checkPermissions() {
        boolean permissionsGranted = true;
        for (String permission : PERMISSIONS) {
            permissionsGranted &=
                    ((checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED));
        }

        if (permissionsGranted)
            return true;

        ActivityCompat.requestPermissions(this, PERMISSIONS,
                PERMISSION_REQUEST_CODE);
        return false;
    }

    protected void initButtons() {
        createNewButton = findViewById(R.id.create_new_button);
        createNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Create-New button clicked.");
                startActivity(new Intent(MainActivity.this,
                        NewAlarmActivity.class));
            }
        });

        viewCurrentButton = findViewById(R.id.view_current_button);
        viewCurrentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "View-Current button clicked.");
                startActivity(new Intent(MainActivity.this,
                        CurrentAlarmsActivity.class));
            }
        });

        viewPastButton = findViewById(R.id.view_past_button);
        viewPastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "View-Past button clicked.");
                startActivity(new Intent(MainActivity.this,
                        PastAlarmsActivity.class));
            }
        });

        configureQuestionsButton = findViewById(R.id.configure_survey_button);
        configureQuestionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Configure-Surveys button clicked.");
                startActivity(new Intent(MainActivity.this,
                        SurveyConfigActivity.class));
            }
        });
    }

    protected void setButtonStatus(boolean enabled) {
        createNewButton.setEnabled(enabled);
        viewCurrentButton.setEnabled(enabled);
        viewPastButton.setEnabled(enabled);
        configureQuestionsButton.setEnabled(enabled);
    }

}