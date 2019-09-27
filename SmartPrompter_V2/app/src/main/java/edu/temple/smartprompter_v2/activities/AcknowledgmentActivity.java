package edu.temple.smartprompter_v2.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.widget.SeekBar;
import android.widget.TextView;

import edu.temple.smartprompter_v2.R;
import edu.temple.smartprompter_v2.SmartPrompter;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.obj.Slider;
import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.Log;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class AcknowledgmentActivity extends BaseActivity {

    private static final int REMIND_ME_LATER = 0;
    private static final int ON_MY_WAY = 2;

    private SeekBar.OnSeekBarChangeListener changeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int selection, boolean b) {
            Log.i(LOG_TAG, "Explicit SeekBar progress changed: " + selection);
            updateSliderFlavor(selection);
            processSliderSelection(selection);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.ui(LOG_TAG, AcknowledgmentActivity.this,
                    "Explicit SeekBar tracking touch started!");
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.ui(LOG_TAG, AcknowledgmentActivity.this,
                    "Explicit SeekBar tracking touch stopped!");
        }
    };

    private TextView mInstructionText;
    private Alarm mAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acknowledgment);
        populateView();

        mInstructionText = findViewById(R.id.instruction_text);
        Slider selection = findViewById(R.id.selection_seekbar);
        selection.setOnSeekBarChangeListener(changeListener);
    }

    private void populateView() {
        mAlarm = ((SmartPrompter)getApplication()).getAlarm(mAlarmGUID);
        TextView taskText = findViewById(R.id.task_text);
        taskText.setText(mAlarm.getDesc());
    }

    private void updateSliderFlavor(int selection) {
        Log.i(LOG_TAG, "Updating flavor for slider selection: " + selection);
        switch (selection) {
            case REMIND_ME_LATER:
                mInstructionText.setText(getString(R.string.acknowledgment_remind_me_later));
                mInstructionText.setTypeface(Typeface.DEFAULT_BOLD);
                mInstructionText.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
                mInstructionText.setTextColor(ContextCompat.getColor(AcknowledgmentActivity.this,
                        R.color.colorPrimaryDark));
                break;
            case ON_MY_WAY:
                mInstructionText.setText(getString(R.string.acknowledgment_on_my_way));
                mInstructionText.setTypeface(Typeface.DEFAULT_BOLD);
                mInstructionText.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
                mInstructionText.setTextColor(ContextCompat.getColor(AcknowledgmentActivity.this,
                        R.color.colorPrimaryDark));
                break;
            default:
                mInstructionText.setText(getString(R.string.acknowledgment_instruction_text));
                mInstructionText.setTypeface(Typeface.DEFAULT);
                mInstructionText.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                mInstructionText.setTextColor(Color.parseColor("#000"));
                break;
        }
    }

    private void processSliderSelection(int selection) {
        Intent intent;
        ((SmartPrompter)getApplicationContext()).stopWakeup();

        Log.i(LOG_TAG, "Processing final slider selection: " + selection);
        switch (selection) {
            case REMIND_ME_LATER:
                Log.ui(LOG_TAG, AcknowledgmentActivity.this,
                        "User selected 'Remind me later'.");

                // Set acknowledgment reminder
                ((SmartPrompter)getApplication()).setAlarmReminder(mAlarm,
                        Alarm.REMINDER.Explicit);

                // Shut down the acknowledgment screen, return to main activity
                intent = new Intent(AcknowledgmentActivity.this,
                        MainActivity.class);
                intent.putExtra(Constants.BUNDLE_REMIND_ME_LATER_ACK, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(intent);
                finish();
                break;
            case ON_MY_WAY:
                Log.ui(LOG_TAG, AcknowledgmentActivity.this,
                        "User selected 'On my way'.");

                // if an acknowledgment reminder exists for this alarm, cancel it
                ((SmartPrompter)getApplication()).cancelAlarm(mAlarm);

                // Update alarm status and progress to completion screen
                ((SmartPrompter)getApplication()).updateAlarmStatus(mAlarmGUID,
                        Alarm.STATUS.Incomplete);
                intent = new Intent(AcknowledgmentActivity.this,
                        CompletionActivity.class);
                intent.putExtra(Constants.BUNDLE_ARG_ALARM_WAKEUP, mWakeup);
                intent.putExtra(Constants.BUNDLE_ARG_ALARM_GUID, mAlarmGUID);
                startActivity(intent);
                finish();
                break;
        }
    }

}