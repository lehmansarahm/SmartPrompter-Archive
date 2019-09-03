package edu.temple.smartprompter_v2.activities;

import android.content.Intent;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.widget.SeekBar;
import android.widget.TextView;

import edu.temple.smartprompter_v2.R;
import edu.temple.smartprompter_v2.SmartPrompter;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.utils.Constants;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class CompletionActivity extends BaseActivity {

    private static final int REMIND_ME_LATER = 0;
    private static final int READY = 2;

    private TextView mInstructionText;
    private String mAlarmGUID;
    private Alarm mAlarm;
    private int mSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completion);

        mAlarmGUID = getIntent().getStringExtra(Constants.BUNDLE_ARG_ALARM_GUID);
        populateView();

        mInstructionText = findViewById(R.id.instruction_text);
        mInstructionText.setTextColor(Color.parseColor("#000000"));

        SeekBar selection = findViewById(R.id.selection_seekbar);
        selection.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.i(LOG_TAG, "Completion SeekBar progress changed: " + i);
                mSelection = i;

                switch (mSelection) {
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

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i(LOG_TAG, "Completion SeekBar tracking touch started!");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i(LOG_TAG, "Completion SeekBar tracking touch stopped!  "
                        + "Initializing response for selection: " + mSelection);

                Intent intent;
                switch (mSelection) {
                    case REMIND_ME_LATER:
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
                        // progress to camera screen (do not update alarm status until picture is taken!!)
                        intent = new Intent(CompletionActivity.this,
                                CameraActivity.class);
                        intent.putExtra(Constants.BUNDLE_ARG_ALARM_GUID, mAlarmGUID);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        });
    }

    private void populateView() {
        mAlarm = ((SmartPrompter)getApplication()).getAlarm(mAlarmGUID);
        TextView taskText = findViewById(R.id.task_text);
        taskText.setText(mAlarm.getDesc());
    }

}