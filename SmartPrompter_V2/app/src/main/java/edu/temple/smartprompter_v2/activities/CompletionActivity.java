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

public class CompletionActivity extends BaseActivity {

    private static final int REMIND_ME_LATER = 0;
    private static final int READY = 2;

    private SeekBar.OnSeekBarChangeListener changeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            Log.i(LOG_TAG, "Completion SeekBar progress changed: " + i);
            updateSliderFlavor(i);
            processSliderSelection(i);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.ui(LOG_TAG, CompletionActivity.this,
                    "Completion SeekBar tracking touch started!");
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.ui(LOG_TAG, CompletionActivity.this,
                    "Completion SeekBar tracking touch stopped!");
        }
    };

    private TextView mInstructionText;
    private Alarm mAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completion);
        populateView();

        mInstructionText = findViewById(R.id.instruction_text);
        mInstructionText.setTextColor(Color.parseColor("#000000"));

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
                mInstructionText.setText(getString(R.string.completion_remind_me_later));
                mInstructionText.setTypeface(Typeface.DEFAULT_BOLD);
                mInstructionText.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
                mInstructionText.setTextColor(ContextCompat.getColor(CompletionActivity.this,
                        R.color.colorPrimaryDark));
                break;
            case READY:
                mInstructionText.setText(getString(R.string.completion_ready));
                mInstructionText.setTypeface(Typeface.DEFAULT_BOLD);
                mInstructionText.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
                mInstructionText.setTextColor(ContextCompat.getColor(CompletionActivity.this,
                        R.color.colorPrimaryDark));
                break;
            default:
                mInstructionText.setText(getString(R.string.completion_instruction_text));
                mInstructionText.setTypeface(Typeface.DEFAULT);
                mInstructionText.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                mInstructionText.setTextColor(Color.parseColor("#000000"));
                break;
        }
    }

    private void processSliderSelection(int selection) {
        Intent intent;
        ((SmartPrompter)getApplicationContext()).stopWakeup();

        Log.i(LOG_TAG, "Processing final slider selection: " + selection);
        switch (selection) {
            case REMIND_ME_LATER:
                Log.ui(LOG_TAG, CompletionActivity.this,
                        "User selected 'Remind me later'.");

                // Set completion reminder
                ((SmartPrompter)getApplication()).setAlarmReminder(mAlarm,
                        Alarm.REMINDER.Completion);

                // Shut down the completion screen, return to main activity
                intent = new Intent(CompletionActivity.this,
                        MainActivity.class);
                intent.putExtra(Constants.BUNDLE_REMIND_ME_LATER_COMP, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(intent);
                finish();
                break;
            case READY:
                Log.ui(LOG_TAG, CompletionActivity.this,
                        "User selected 'Ready to take picture'.");

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