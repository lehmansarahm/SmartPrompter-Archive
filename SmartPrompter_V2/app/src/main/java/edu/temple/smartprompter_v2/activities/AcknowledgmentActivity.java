package edu.temple.smartprompter_v2.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import edu.temple.smartprompter_v2.R;
import edu.temple.smartprompter_v2.SmartPrompter;
import edu.temple.sp_res_lib.obj.Alarm;
import edu.temple.sp_res_lib.utils.Constants;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class AcknowledgmentActivity extends AppCompatActivity {

    private static final int REMIND_ME_LATER = 0;
    private static final int ON_MY_WAY = 2;

    private TextView mInstructionText;
    private String mAlarmGUID;
    private int mSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acknowledgment);

        mAlarmGUID = getIntent().getStringExtra(Constants.BUNDLE_ARG_ALARM_GUID);
        populateView();

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
                        break;
                    case ON_MY_WAY:
                        mInstructionText.setText(getString(R.string.acknowledgment_on_my_way));
                        break;
                    default:
                        mInstructionText.setText(getString(R.string.acknowledgment_instruction_text));
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
                if (mSelection == ON_MY_WAY) {
                    Intent intent = new Intent(AcknowledgmentActivity.this,
                            CompletionActivity.class);
                    intent.putExtra(Constants.BUNDLE_ARG_ALARM_GUID, mAlarmGUID);
                    startActivity(intent);
                }
            }
        });
    }

    private void populateView() {
        Alarm alarm = ((SmartPrompter)getApplication()).getAlarm(mAlarmGUID);
        TextView taskText = findViewById(R.id.task_text);
        taskText.setText(alarm.getDesc());
    }

}