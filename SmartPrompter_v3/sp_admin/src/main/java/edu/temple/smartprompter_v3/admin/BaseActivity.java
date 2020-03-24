package edu.temple.smartprompter_v3.admin;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

import edu.temple.smartprompter_v3.res_lib.utils.FbaEventLogger;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String LOG_TAG = "SmartPrompter_v3 Admin";

    protected static final int PERMISSION_REQUEST_CODE = 429;
    protected static final String[] PERMISSIONS = new String[] {
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    protected FirebaseAuth mFbAuth;
    protected FirebaseAnalytics mFbAnalytics;
    protected FbaEventLogger mFbaEventLogger;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFbAuth = FirebaseAuth.getInstance();
        mFbAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

        String email = (mFbAuth.getCurrentUser() == null
                ? "" : mFbAuth.getCurrentUser().getEmail());
        mFbaEventLogger = new FbaEventLogger(this, email);
    }

    protected boolean checkPermissions() {
        boolean permissionsGranted = true;
        for (String permission : PERMISSIONS) {
            permissionsGranted &= ((checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED));
        }

        if (permissionsGranted)
            return true;
        else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
            return false;
        }
    }

}