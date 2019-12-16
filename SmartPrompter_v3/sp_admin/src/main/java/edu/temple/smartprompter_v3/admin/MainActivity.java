package edu.temple.smartprompter_v3.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import edu.temple.smartprompter_v3.res_lib.utils.Constants;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Button createNewButton, viewCurrentButton, configSettingsButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initButtons();
        if (checkPermissions()) setButtonStatus(true);
    }

    protected void initButtons() {
        createNewButton = findViewById(R.id.create_new_button);
        createNewButton.setOnClickListener(this);

        viewCurrentButton = findViewById(R.id.view_current_button);
        viewCurrentButton.setOnClickListener(this);

        configSettingsButton = findViewById(R.id.configure_settings_button);
        configSettingsButton.setOnClickListener(this);
    }

    protected void setButtonStatus(boolean enabled) {
        createNewButton.setEnabled(enabled);
        viewCurrentButton.setEnabled(enabled);
        configSettingsButton.setEnabled(enabled);
    }

    @Override
    public void onClick(View v) {
        Class targetClass = null;
        switch (v.getId()) {
            case R.id.create_new_button:
                mFbaEventLogger.buttonClick(MainActivity.this.getClass(),
                        "CreateNewAlarm", v.getId());
                targetClass = AlarmDetailsActivity.class;
                break;
            case R.id.view_current_button:
                mFbaEventLogger.buttonClick(MainActivity.this.getClass(),
                        "ViewCurrentAlarms", v.getId());
                targetClass = AlarmListActivity.class;
                break;
            case R.id.configure_settings_button:
                mFbaEventLogger.buttonClick(MainActivity.this.getClass(),
                        "ConfigureSettings", v.getId());
                targetClass = ConfigureSettingsActivity.class;
                break;
            default:
                mFbaEventLogger.buttonClick(MainActivity.this.getClass(),
                        "Unknown", v.getId());
                break;
        }

        if (targetClass != null)
            startActivity(new Intent(MainActivity.this, targetClass));
        else
            Log.e(Constants.LOG_TAG, "Can't launch activity with null target class!");
    }
}