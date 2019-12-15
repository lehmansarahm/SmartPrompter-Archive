package edu.temple.sp_admin_v3.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.temple.sp_admin_v3.BuildConfig;
import edu.temple.sp_admin_v3.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = "SmartPrompter_v3";

    private static final long FB_WAIT_INTERVAL = TimeUnit.SECONDS.toMillis(5);

    private static final String USERPROP_KEY_EXEC_MODE = "exec_mode";

    private static final String BUNDLE_KEY_BUTTON_ID = "bundle_button_id";

    private FirebaseAuth mFbAuth;
    private FirebaseAuth.AuthStateListener mFbAuthListener;

    private FirebaseAnalytics mFbAnalytics;
    private FirebaseRemoteConfig mFbConfig;

    // Retrieve remote config, set 'developer mode' so we can get updates to our config
    // properties immediately (not get rate-limited)
    private FirebaseRemoteConfigSettings mFbConfigSettings =
            new FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG).build();

    private EditText emailText, passwordText;
    private Spinner roleSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFbAuth = FirebaseAuth.getInstance();
        mFbAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFbAuth.getCurrentUser();
                if (user != null) showLoggedInView();
                else Log.e(LOG_TAG, "No users signed in!");
            }
        };

        mFbAnalytics = FirebaseAnalytics.getInstance(this);
        mFbAnalytics.setMinimumSessionDuration(FB_WAIT_INTERVAL);

        mFbConfig = FirebaseRemoteConfig.getInstance();
        mFbConfig.setConfigSettings(mFbConfigSettings);
        mFbConfig.setDefaults(R.xml.sp_config_params);

        emailText = findViewById(R.id.email_edit);
        passwordText = findViewById(R.id.password_edit);
        roleSpinner = findViewById(R.id.role_spinner);

        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.force_crash_button).setOnClickListener(this);

        List<String> list = new ArrayList<>();
        list.add("Researcher");
        list.add("Caretaker");
        list.add("Patient");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(dataAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mFbAuth.addAuthStateListener(mFbAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mFbAuth.removeAuthStateListener(mFbAuthListener);
    }

    @Override
    public void onClick(View v) {
        String eventName = (this.getClass().getSimpleName() + "_ButtonClick_");
        switch (v.getId()) {
            case R.id.login_button:
                mFbAuth.signInWithEmailAndPassword(emailText.getText().toString(),
                        passwordText.getText().toString());
                mFbAnalytics.setUserProperty(USERPROP_KEY_EXEC_MODE,
                        roleSpinner.getSelectedItem().toString());
                if (mFbAuth.getCurrentUser() != null) showLoggedInView();
                eventName += "Login";
                break;
            case R.id.force_crash_button:
                Crashlytics.getInstance().crash();
                eventName += "ForceCrash";
                break;
            default:
                eventName += "Unknown";
                break;
        }

        Bundle params = new Bundle();
        params.putInt(BUNDLE_KEY_BUTTON_ID, v.getId());
        mFbAnalytics.logEvent(eventName, params);
    }

    private void showLoggedInView() {
        Log.i(LOG_TAG, "FORWARD TO THE APPROPRIATE APP VIEW FOR USER: "
                + mFbAuth.getCurrentUser().getUid());
    }

}