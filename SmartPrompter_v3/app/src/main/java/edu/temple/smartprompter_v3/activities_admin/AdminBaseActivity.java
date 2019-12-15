package edu.temple.smartprompter_v3.activities_admin;

import android.os.Bundle;

public abstract class AdminBaseActivity extends edu.temple.smartprompter_v3.BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ACTION_BAR_TITLE = "SmartPrompter - Admin";
        super.onCreate(savedInstanceState);
    }

}