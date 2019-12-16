package edu.temple.smartprompter_v3.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import edu.temple.smartprompter_v3.R;
import edu.temple.smartprompter_v3.SmartPrompter;
import edu.temple.smartprompter_v3.res_lib.SpController;
import edu.temple.smartprompter_v3.res_lib.data.Alarm;
import edu.temple.smartprompter_v3.res_lib.data.FirebaseConnector;
import edu.temple.smartprompter_v3.res_lib.utils.Constants;
import edu.temple.smartprompter_v3.res_lib.widgets.Slider;

public class CompletionActivity extends SliderBaseActivity {

    private static final int REMIND_ME_LATER = 0;
    private static final int READY = 2;

    private TextView mInstructionText;
    private Alarm mAlarm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completion);

        FirebaseConnector.getAlarmByGuid(mAlarmGUID, result -> {
            if (result != null) mAlarm = (Alarm)result;
            else {
                Log.e(BaseActivity.LOG_TAG, "Something went wrong while attempting to "
                        + "retrieve alarm with GUID: " + mAlarmGUID
                        + ".  Displaying default record.");
                mAlarm = new Alarm();
                mAlarmGUID = mAlarm.getGuid();
            }

            TextView taskText = findViewById(R.id.task_text);
            taskText.setText(mAlarm.getDesc());
        });

        mInstructionText = findViewById(R.id.instruction_text);
        mInstructionText.setTextColor(Color.parseColor("#000000"));

        Slider selection = findViewById(R.id.selection_seekbar);
        selection.setOnSeekBarChangeListener(changeListener);
    }

    @Override
    String getReminderText() {
        return getString(R.string.completion_remind_me_later);
    }

    @Override
    String getReadyText() {
        return getString(R.string.completion_ready);
    }

    @Override
    String getDefaultText() {
        return getString(R.string.completion_instruction_text);
    }

    @Override
    protected void processSliderSelection(int selection) {
        Log.i(LOG_TAG, "Processing final slider selection: " + selection);
        SmartPrompter.stopWakeup(this);
        Intent intent;

        switch (selection) {
            case REMIND_ME_LATER:
                // Set completion reminder
                mFbaEventLogger.alarmTaskSnooze(CompletionActivity.class,
                        "RemindMeLater", mAlarm);
                SpController.setReminder(this, mAlarm,
                        BaseActivity.ALARM_NOTIFICATION_CLASS,
                        BaseActivity.ALARM_RECEIVER_CLASS,
                        Alarm.REMINDER.Explicit,
                        true);

                // Shut down the completion screen, return to main activity
                intent = new Intent(CompletionActivity.this,
                        MainActivity.class);
                intent.putExtra(Constants.BUNDLE_REMIND_ME_LATER_COMP, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(intent);
                finish();
                break;

            case READY:
                mFbaEventLogger.alarmTaskPhaseChange(CompletionActivity.class,
                        "ReadyToTakePicture", mAlarm);

                // progress to camera screen (do not update alarm status until picture is taken!!)
                intent = new Intent(CompletionActivity.this,
                        CameraActivity.class);
                intent.putExtra(Constants.BUNDLE_ARG_ALARM_WAKEUP, mWakeup);
                intent.putExtra(Constants.BUNDLE_ARG_ALARM_GUID, mAlarmGUID);
                startActivity(intent);
                finish();
                break;
        }
    }

}