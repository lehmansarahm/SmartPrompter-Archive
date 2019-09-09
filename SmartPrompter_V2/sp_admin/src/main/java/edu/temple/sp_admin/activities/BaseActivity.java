package edu.temple.sp_admin.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.temple.sp_res_lib.utils.Log;

import static edu.temple.sp_admin.SpAdmin.LOG_TAG;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.ui(LOG_TAG, this, "Created");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        Log.ui(LOG_TAG, this, "Paused");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.ui(LOG_TAG, this, "Resumed");
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.ui(LOG_TAG, this, "Stopped");
        Log.dump(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.ui(LOG_TAG, this, "Destroyed");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.ui(LOG_TAG, this, "Back button pressed");
        super.onBackPressed();
    }

}
