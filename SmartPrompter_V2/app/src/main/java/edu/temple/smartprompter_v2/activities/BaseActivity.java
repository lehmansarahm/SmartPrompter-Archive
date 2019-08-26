package edu.temple.smartprompter_v2.activities;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import edu.temple.smartprompter_v2.SmartPrompter;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onPause() {
        Log.i(LOG_TAG, this.getLocalClassName() + " paused!");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(LOG_TAG, this.getLocalClassName() + " stopped!");
        ((SmartPrompter)getApplication()).onAppStopped();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, this.getLocalClassName() + " destroyed!");
        super.onDestroy();
    }

}
