package edu.temple.smartprompter_v3.admin;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;

import edu.temple.smartprompter_v3.res_lib.utils.Constants;

public class LoginActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (checkPermissions() && noBattOp()) onPermissionsGranted();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    onPermissionsGranted();
                else onPermissionsDenied();
            }
        }
    }

    private boolean noBattOp() {
        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (pm.isIgnoringBatteryOptimizations(packageName))
            return true;
        else {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
            return false;
        }
    }

    private void onPermissionsGranted() {
        Log.i("SmartPrompter_v3", "Permissions granted!");
        if (mFbAuth.getCurrentUser() != null) forwardToMain();

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> {
            mFbaEventLogger.buttonClick(LoginActivity.class, "Login", v.getId());
            String email = ((EditText)findViewById(R.id.email_edit)).getText().toString();
            String password = ((EditText)findViewById(R.id.password_edit)).getText().toString();
            mFbAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.i(LOG_TAG, "Login worked!");
                            forwardToMain();
                        } else {
                            Log.e(LOG_TAG, "Something went wrong during login!",
                                    task.getException());
                        }
                    });
        });

        Button forceCrashButton = findViewById(R.id.force_crash_button);
        forceCrashButton.setOnClickListener(v -> {
            mFbaEventLogger.buttonClick(LoginActivity.class, "ForceCrash", v.getId());
            Log.e(Constants.LOG_TAG, "Testing forced crash button");
        });
    }

    private void onPermissionsDenied() {
        Log.e("SmartPrompter_v3", "Permissions denied!");
    }

    private void forwardToMain() {
        String email = mFbAuth.getCurrentUser().getEmail();
        Crashlytics.setUserEmail(email);
        mFbAnalytics.setUserProperty("Email", email);

        Log.i(LOG_TAG, "User is logged in - forwarding to MainActivity.");
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

}