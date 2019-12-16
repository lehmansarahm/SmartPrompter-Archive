package edu.temple.smartprompter_v3.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import edu.temple.smartprompter_v3.R;
import edu.temple.smartprompter_v3.SmartPrompter;
import edu.temple.smartprompter_v3.res_lib.SpController;
import edu.temple.smartprompter_v3.res_lib.data.Alarm;
import edu.temple.smartprompter_v3.res_lib.data.FirebaseConnector;
import edu.temple.smartprompter_v3.res_lib.utils.Constants;
import edu.temple.smartprompter_v3.res_lib.widgets.Slider;

public class AcknowledgmentActivity extends SliderBaseActivity {

    private Alarm mAlarm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acknowledgment);

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
        Slider selection = findViewById(R.id.selection_seekbar);
        selection.setOnSeekBarChangeListener(changeListener);
    }

    @Override
    String getReminderText() {
        return getString(R.string.acknowledgment_remind_me_later);
    }

    @Override
    String getReadyText() {
        return getString(R.string.acknowledgment_on_my_way);
    }

    @Override
    String getDefaultText() {
        return getString(R.string.acknowledgment_instruction_text);
    }

    @Override
    protected void processSliderSelection(int selection) {
        SmartPrompter.stopWakeup(this);
        Intent intent;

        Log.i(LOG_TAG, "Processing final slider selection: " + selection);
        switch (selection) {
            case REMIND_ME_LATER:
                // Set acknowledgment reminder
                mFbaEventLogger.alarmTaskSnooze(AcknowledgmentActivity.class,
                        "RemindMeLater", mAlarm);
                SpController.setReminder(this, mAlarm,
                        BaseActivity.ALARM_NOTIFICATION_CLASS,
                        BaseActivity.ALARM_RECEIVER_CLASS,
                        Alarm.REMINDER.Explicit,
                        true);

                // Shut down the acknowledgment screen, return to main activity
                intent = new Intent(AcknowledgmentActivity.this,
                        MainActivity.class);
                intent.putExtra(Constants.BUNDLE_REMIND_ME_LATER_ACK, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(intent);
                finish();
                break;

            case READY_ON_MY_WAY:
                // User has completed "acknowledgment" phase ...
                mFbaEventLogger.alarmTaskPhaseChange(AcknowledgmentActivity.class,
                        "OnMyWay", mAlarm);
                SpController.markAcknowledged(this, mAlarm,
                        BaseActivity.ALARM_NOTIFICATION_CLASS,
                        BaseActivity.ALARM_RECEIVER_CLASS);

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