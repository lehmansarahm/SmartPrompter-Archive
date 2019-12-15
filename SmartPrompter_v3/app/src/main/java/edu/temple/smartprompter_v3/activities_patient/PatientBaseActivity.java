package edu.temple.smartprompter_v3.activities_patient;

import android.os.Bundle;

public abstract class PatientBaseActivity extends edu.temple.smartprompter_v3.BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ACTION_BAR_TITLE = "SmartPrompter";
        super.onCreate(savedInstanceState);
    }

}