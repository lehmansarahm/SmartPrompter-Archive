package edu.temple.smartprompter_v3.activities_admin;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import edu.temple.smartprompter_v3.R;
import edu.temple.smartprompter_v3.utils.Constants;

public class AdminMainActivity extends AdminBaseActivity implements View.OnClickListener {

    private Button createNewButton, viewCurrentButton, configAudioButton;

    @Override
    protected void showLoggedInView() {
        Log.i(Constants.LOG_TAG, "showLoggedInView method called for class: "
                + this.getClass().getSimpleName());
        setContentView(R.layout.activity_main_admin);

        initButtons();
        if (checkPermissions()) setButtonStatus(true);
    }

    protected void initButtons() {
        createNewButton = findViewById(R.id.create_new_button);
        createNewButton.setOnClickListener(this);

        viewCurrentButton = findViewById(R.id.view_current_button);
        viewCurrentButton.setOnClickListener(this);

        configAudioButton = findViewById(R.id.configure_audio_button);
        configAudioButton.setOnClickListener(this);
    }

    protected void setButtonStatus(boolean enabled) {
        createNewButton.setEnabled(enabled);
        viewCurrentButton.setEnabled(enabled);
        configAudioButton.setEnabled(false); // TODO - add custom audio back in ...
    }

    @Override
    public void onClick(View v) {
        Class targetClass = null;
        switch (v.getId()) {
            case R.id.create_new_button:
                fbaEventLogger.buttonClick(AdminMainActivity.this.getClass(),
                        "CreateNewAlarm", v.getId());
                targetClass = AlarmDetailsActivity.class;
                break;
            case R.id.view_current_button:
                fbaEventLogger.buttonClick(AdminMainActivity.this.getClass(),
                        "ViewCurrentAlarms", v.getId());
                targetClass = AlarmListActivity.class;
                break;
            case R.id.configure_audio_button:
                fbaEventLogger.buttonClick(AdminMainActivity.this.getClass(),
                        "ConfigureAudio", v.getId());
                // targetClass = ConfigAudioActivity.class;
                break;
            default:
                fbaEventLogger.buttonClick(AdminMainActivity.this.getClass(),
                        "Unknown", v.getId());
                break;
        }

        if (targetClass != null)
            startActivity(new Intent(AdminMainActivity.this, targetClass));
        else
            Log.e(Constants.LOG_TAG, "Can't launch activity with null target class!");
    }
}