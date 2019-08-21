package edu.temple.smartprompter_v2.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import edu.temple.smartprompter_v2.R;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class CompletionActivity extends AppCompatActivity {

    private static final int REMIND_ME_LATER = 0;
    private static final int READY = 2;

    private TextView mInstructionText;
    private int mSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completion);

        mInstructionText = findViewById(R.id.instruction_text);
        SeekBar selection = findViewById(R.id.selection_seekbar);
        selection.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.i(LOG_TAG, "Completion SeekBar progress changed: " + i);
                mSelection = i;

                switch (mSelection) {
                    case REMIND_ME_LATER:
                        mInstructionText.setText(getString(R.string.completion_remind_me_later));
                        break;
                    case READY:
                        mInstructionText.setText(getString(R.string.completion_ready));
                        break;
                    default:
                        mInstructionText.setText(getString(R.string.completion_instruction_text));
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
                if (mSelection == READY) {
                    startActivity(new Intent(CompletionActivity.this,
                            CameraActivity.class));
                }
            }
        });
    }

}