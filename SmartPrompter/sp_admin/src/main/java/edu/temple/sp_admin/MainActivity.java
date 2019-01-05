package edu.temple.sp_admin;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import edu.temple.sp_admin.fragments.WelcomeFragment;
import edu.temple.sp_admin.utils.BaseActivity;
import edu.temple.sp_admin.utils.Constants;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOG_TAG, "Main Activity created!");
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        if (checkPermissions()) {
            initNavigation();
            showDefaultFragment();
        }
    }

    @Override
    public void onPause() {
        Log.i(Constants.LOG_TAG, "Main Activity paused!");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.i(Constants.LOG_TAG, "Main Activity destroyed!");
        super.onDestroy();
    }

    protected void showDefaultFragment() {
        Log.i(Constants.LOG_TAG, "Populating Main Activity with welcome fragment.");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        WelcomeFragment fragment = new WelcomeFragment();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

}