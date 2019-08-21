package edu.temple.sp_admin.activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import edu.temple.sp_admin.R;
import edu.temple.sp_admin.fragments.AlarmDetailsFragment;
import edu.temple.sp_admin.fragments.ButtonBarFragment;

import static edu.temple.sp_admin.SpAdmin.LOG_TAG;

public class NewAlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);

        Log.i(LOG_TAG, "Populating " + this.getLocalClassName()
                + " with Alarm-Details fragment.");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        AlarmDetailsFragment fragment = new AlarmDetailsFragment();
        ft.replace(R.id.details_container, fragment);
        ft.commit();
    }

}