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
import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.Log;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class AcknowledgmentActivity extends BaseActivity {

    private static final int REMIND_ME_LATER = 0;
    private static final int ON_MY_WAY = 2;

    private TextView mInstructionText;
    private String mAlarmGUID;
    private Alarm mAlarm;
    private int mSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acknowledgment);

        mAlarmGUID = getIntent().getStringExtra(Constants.BUNDLE_ARG_ALARM_GUID);
        populateView();

        boolean wakeup = getIntent().getBooleanExtra(Constants.BUNDLE_ARG_ALARM_WAKEUP, false);
        if (wakeup) super.wakeup(this);

        mInstructionText = findViewById(R.id.instruction_text);
        SeekBar selection = findViewById(R.id.selection_seekbar);
        selection.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.i(LOG_TAG, "Acknowledgment SeekBar progress changed: " + i);
                mSelection = i;

                switch (mSelection) {
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

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i(LOG_TAG, "Acknowledgment SeekBar tracking touch started!");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i(LOG_TAG, "Acknowledgment SeekBar tracking touch stopped!  "
                        + "Initializing response for selection: " + mSelection);

                Intent intent;
                switch (mSelection) {
                    case REMIND_ME_LATER:
                        // Set acknowledgment reminder
                        ((SmartPrompter)getApplication()).setAlarmReminder(mAlarm,
                                Alarm.REMINDER.Acknowledgment);

                        // Shut down the acknowledgment screen, return to main activity
                        intent = new Intent(AcknowledgmentActivity.this,
                                MainActivity.class);
                        intent.putExtra(Constants.BUNDLE_REMIND_ME_LATER_ACK, true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        startActivity(intent);
                        finish();
                        break;
                    case ON_MY_WAY:
                        // if an acknowledgment reminder exists for this alarm, cancel it
                        ((SmartPrompter)getApplication()).cancelAlarm(mAlarm);

                        // Update alarm status and progress to completion screen
                        ((SmartPrompter)getApplication()).updateAlarmStatus(mAlarmGUID,
                                Alarm.STATUS.Incomplete);
                        intent = new Intent(AcknowledgmentActivity.this,
                                CompletionActivity.class);
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
        if (mAlarm.getStatus().equals(Alarm.STATUS.Active))
            ((SmartPrompter)getApplication()).updateAlarmStatus(mAlarmGUID,
                    Alarm.STATUS.Unacknowledged);

        TextView taskText = findViewById(R.id.task_text);
        taskText.setText(mAlarm.getDesc());
    }

}