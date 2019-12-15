package edu.temple.smartprompter_v3;

import android.content.Intent;

import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import edu.temple.smartprompter_v3.activities_admin.AdminMainActivity;
import edu.temple.smartprompter_v3.activities_patient.PatientMainActivity;
import edu.temple.smartprompter_v3.data.FirebaseConnector;
import edu.temple.smartprompter_v3.data.User;
import edu.temple.smartprompter_v3.utils.Constants;

import static edu.temple.smartprompter_v3.utils.Constants.LOG_TAG;

public class LoginActivity extends BaseActivity
        implements View.OnClickListener, BaseActivity.PermissionsListener {

    @Override
    protected void showLoggedInView() {
        Log.i(Constants.LOG_TAG, "showLoggedInView method called for class: "
                + this.getClass().getSimpleName());

        String email = mFbAuth.getCurrentUser().getEmail();
        Crashlytics.setUserEmail(email);

        FirebaseConnector.getUsersByEmail(email, results -> {
            Log.e(LOG_TAG, "Returned more than one result for user email: " + email);
            Intent intent = new Intent(LoginActivity.this,
                    (((User)results.get(0)).getRole() == User.ROLE.Participant)
                            ? PatientMainActivity.class : AdminMainActivity.class);
            LoginActivity.this.startActivity(intent);
            LoginActivity.this.finish();
        });
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onPermissionsProvided() {

    }

    @Override
    public void onPermissionsDenied() {

    }

}