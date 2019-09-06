package edu.temple.sp_admin.activities;

import android.support.v7.app.AppCompatActivity;

import edu.temple.sp_res_lib.utils.Log;

import static edu.temple.sp_admin.SpAdmin.LOG_TAG;

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onPause() {
        Log.i(LOG_TAG, this.getLocalClassName() + " paused!");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(LOG_TAG, this.getLocalClassName() + " stopped!");
        Log.dump(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, this.getLocalClassName() + " destroyed!");
        super.onDestroy();
    }

}
