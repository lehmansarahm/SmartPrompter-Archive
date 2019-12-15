package edu.temple.smartprompter_v3;

import android.Manifest;
import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import edu.temple.smartprompter_v3.activities_admin.AdminMainActivity;
import edu.temple.smartprompter_v3.activities_patient.PatientMainActivity;
import edu.temple.smartprompter_v3.utils.FbaEventLogger;

import static edu.temple.smartprompter_v3.utils.Constants.LOG_TAG;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    interface PermissionsListener {
        void onPermissionsProvided();
        void onPermissionsDenied();
    }

    protected PermissionsListener listener;

    protected static final int PERMISSION_REQUEST_CODE = 429;
    protected static final String[] PERMISSIONS = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    protected static String ACTION_BAR_TITLE = "SmartPrompter";

    protected FbaEventLogger fbaEventLogger;

    protected FirebaseAuth mFbAuth;
    protected FirebaseAuth.AuthStateListener mFbAuthListener;

    protected EditText emailText, passwordText;
    protected Spinner roleSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActionBar() != null) {
            getActionBar().setTitle(ACTION_BAR_TITLE);
        } else if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(ACTION_BAR_TITLE);
        }

        mFbAuth = FirebaseAuth.getInstance();
        mFbAuthListener = firebaseAuth -> {
            FirebaseUser user = mFbAuth.getCurrentUser();
            if (user == null) {
                Log.e(LOG_TAG, "No users signed in!");
                showDefaultView();
            } else {
                Log.i(LOG_TAG, "User already logged in: " + user.getEmail());
                showLoggedInView();
            }
        };

        fbaEventLogger = new FbaEventLogger(this,
                mFbAuth.getCurrentUser() == null);
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

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (listener != null)
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        listener.onPermissionsProvided();
                    else listener.onPermissionsDenied();
            }
        }
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

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                fbaEventLogger.buttonClick(BaseActivity.this.getClass(), "Login", v.getId());
                mFbAuth.signInWithEmailAndPassword
                        (emailText.getText().toString(), passwordText.getText().toString())
                        .addOnFailureListener(e -> {
                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(BaseActivity.this,
                                        "Incorrect password", Toast.LENGTH_LONG).show();
                            } else if (e instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(BaseActivity.this,
                                        "No user with this email", Toast.LENGTH_LONG).show();
                            } else if (e instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(BaseActivity.this,
                                        "Cannot create account; username already exists",
                                        Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(BaseActivity.this,
                                        "Login successful!", Toast.LENGTH_LONG).show();
                                showLoggedInView();
                            } else {
                                Log.e(LOG_TAG, "Something went wrong while trying "
                                        + "to log in!");
                            }
                        });
                break;
            case R.id.force_crash_button:
                fbaEventLogger.buttonClick(BaseActivity.this.getClass(), "ForceCrash", v.getId());
                Crashlytics.getInstance().crash();
                break;
            default:
                fbaEventLogger.buttonClick(BaseActivity.this.getClass(), "Unknown", v.getId());
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        Class targetClass = null;

        switch (item.getItemId()) {
            case R.id.menu_sp:
                fbaEventLogger.buttonClick(BaseActivity.this.getClass(), "MenuSp", item.getItemId());
                message = "Are you sure you want to switch to SmartPrompter?";
                targetClass = PatientMainActivity.class;
                break;
            case R.id.menu_spa:
                fbaEventLogger.buttonClick(BaseActivity.this.getClass(), "MenuSpa", item.getItemId());
                message = "Are you sure you want to switch to SmartPrompter Admin?";
                targetClass = AdminMainActivity.class;
                break;
            case R.id.menu_exit:
                fbaEventLogger.buttonClick(BaseActivity.this.getClass(), "MenuExit", item.getItemId());
                message = "Are you sure you want to exit the application?";
                break;
        }

        showConfirmationDialog(message, targetClass);
        return(super.onOptionsItemSelected(item));
    }

    protected abstract void showLoggedInView();

    private void showDefaultView() {
        setContentView(R.layout.activity_login);
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

    private void showConfirmationDialog(String message, final Class targetClass) {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    fbaEventLogger.buttonClick(BaseActivity.this.getClass(), "DialogYes", which);
                    if (targetClass != null) {
                        startActivity(new Intent(BaseActivity.this, targetClass));
                        finish();
                    } else finishAndRemoveTask();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    fbaEventLogger.buttonClick(BaseActivity.this.getClass(), "DialogNo", which);
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

}