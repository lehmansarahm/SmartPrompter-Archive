package edu.temple.smartprompter_v3.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import edu.temple.smartprompter_v3.R;

public abstract class SliderBaseActivity extends BaseActivity {

    // protected TextView mInstructionText;

    protected SeekBar.OnSeekBarChangeListener changeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int selection, boolean b) {
            Log.i(LOG_TAG, "Acknowledgment SeekBar progress changed: " + selection);
            // updateSliderFlavor(selection);
            processSliderSelection(selection);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.i(LOG_TAG,"Acknowledgment SeekBar tracking touch started!");
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.i(LOG_TAG,"Acknowledgment SeekBar tracking touch stopped!");
        }
    };

    /* protected void updateSliderFlavor(int selection) {
        Log.i(LOG_TAG, "Updating flavor for slider selection: " + selection);
        if (mInstructionText == null) mInstructionText = findViewById(R.id.instruction_text);

        switch (selection) {
            case REMIND_ME_LATER:
                mInstructionText.setText(getReminderText());
                mInstructionText.setTypeface(Typeface.DEFAULT_BOLD);
                mInstructionText.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
                mInstructionText.setTextColor(ContextCompat.getColor(this,
                        R.color.colorPrimaryDark));
                break;

            case READY_ON_MY_WAY:
                mInstructionText.setText(getReadyText());
                mInstructionText.setTypeface(Typeface.DEFAULT_BOLD);
                mInstructionText.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
                mInstructionText.setTextColor(ContextCompat.getColor(this,
                        R.color.colorPrimaryDark));
                break;

            default:
                mInstructionText.setText(getDefaultText());
                mInstructionText.setTypeface(Typeface.DEFAULT);
                mInstructionText.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                mInstructionText.setTextColor(Color.parseColor("#000000"));
                break;
        }
    } */

    abstract String getReminderText();
    abstract String getReadyText();
    abstract String getDefaultText();

    abstract void processSliderSelection(int selection);


}
