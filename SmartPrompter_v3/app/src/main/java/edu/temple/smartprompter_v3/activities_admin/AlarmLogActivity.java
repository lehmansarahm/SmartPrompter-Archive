package edu.temple.smartprompter_v3.activities_admin;

import android.util.Log;

import edu.temple.smartprompter_v3.BaseActivity;
import edu.temple.smartprompter_v3.R;
import edu.temple.smartprompter_v3.utils.Constants;

public class AlarmLogActivity extends BaseActivity {

    @Override
    protected void showLoggedInView() {
        Log.i(Constants.LOG_TAG, "showLoggedInView method called for class: "
                + this.getClass().getSimpleName());
        setContentView(R.layout.activity_alarm_log);
    }

}