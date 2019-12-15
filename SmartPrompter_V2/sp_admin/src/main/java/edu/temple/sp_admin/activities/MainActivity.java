package edu.temple.sp_admin.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.temple.sp_admin.R;
import edu.temple.sp_res_lib.utils.Log;

import static edu.temple.sp_admin.SpAdmin.LOG_TAG;

public class MainActivity extends BaseActivity {

    private static final int PERMISSION_REQUEST_CODE = 429;
    private static final String[] PERMISSIONS = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    private Button createNewButton, viewActiveButton,
            viewCompleteButton, viewArchivedButton,
            configAudioButton; // configEmailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                Log.ui(LOG_TAG, MainActivity.this, "Create-New button clicked.");
                startActivity(new Intent(MainActivity.this,
                        NewAlarmActivity.class));
            }
        });

        viewActiveButton = findViewById(R.id.view_current_button);
        viewActiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.ui(LOG_TAG, MainActivity.this,"View-Active button clicked.");
                startActivity(new Intent(MainActivity.this,
                        ActiveAlarmsActivity.class));
            }
        });

        // TODO - consolidate "complete" and "archived" lists???

        viewCompleteButton = findViewById(R.id.view_past_button);
        viewCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.ui(LOG_TAG, MainActivity.this, "View-Inactive button clicked.");
                startActivity(new Intent(MainActivity.this,
                        CompleteAlarmsActivity.class));
            }
        });

        viewArchivedButton = findViewById(R.id.view_archived_button);
        viewArchivedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.ui(LOG_TAG, MainActivity.this, "View-Archived button clicked.");
                startActivity(new Intent(MainActivity.this,
                        ArchivedAlarmsActivity.class));
            }
        });

        configAudioButton = findViewById(R.id.configure_audio_button);
        configAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.ui(LOG_TAG, MainActivity.this, "Configure-Audio button clicked.");
                startActivity(new Intent(MainActivity.this,
                        ConfigAudioActivity.class));
            }
        });

        /* configEmailButton = findViewById(R.id.configure_email_button);
        configEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.ui(LOG_TAG, MainActivity.this, "Configure-Email button clicked.");
                startActivity(new Intent(MainActivity.this,
                        ConfigEmailActivity.class));
            }
        }); */
    }

    protected void setButtonStatus(boolean enabled) {
        createNewButton.setEnabled(enabled);
        viewActiveButton.setEnabled(enabled);
        viewCompleteButton.setEnabled(enabled);
        configAudioButton.setEnabled(enabled);
        // configEmailButton.setEnabled(enabled);
    }

}