package edu.temple.smartprompter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.SpAlarmManager;

public class AlarmDetailsActivity extends AppCompatActivity {

    private SpAlarmManager mAlarmMgr;
    private Alarm mAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_details);

        int alarmID = getIntent().getIntExtra(Constants.INTENT_EXTRA_ALARM_ID,
                Constants.DEFAULT_ALARM_ID);
        if (alarmID == Constants.DEFAULT_ALARM_ID) {
            Log.e(Constants.LOG_TAG, "No alarm ID provided!");
            return;
        }

        mAlarmMgr = new SpAlarmManager(this);
        mAlarm = mAlarmMgr.get(alarmID);

        initLabel();
        initDate();
        initTime();
        initStatus();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void initLabel() {
        TextView labelText = findViewById(R.id.label_text);
        labelText.setText(mAlarm.getLabel());

        LinearLayout labelLayout = findViewById(R.id.label_layout);
        labelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Constants.LOG_TAG, "User clicked LABEL field for alarm ID: "
                        + mAlarm.getID());
                Toast.makeText(AlarmDetailsActivity.this,
                        "LABEL CLICKED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initDate() {
        TextView dateText = findViewById(R.id.date_text);
        dateText.setText(mAlarm.getDateString());

        LinearLayout dateLayout = findViewById(R.id.date_layout);
        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Constants.LOG_TAG, "User clicked DATE field for alarm ID: "
                        + mAlarm.getID());
                // mDateListener.onDatePickerRequested(mAlarm.getID(), mAlarm.getDate());
            }
        });
    }

    private void initTime() {
        TextView timeText = findViewById(R.id.time_text);
        timeText.setText(mAlarm.getTimeString());

        LinearLayout timeLayout = findViewById(R.id.time_layout);
        timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Constants.LOG_TAG, "User clicked TIME field for alarm ID: "
                        + mAlarm.getID());
                // mTimeListener.onTimePickerRequested(mAlarm.getID(), mAlarm.getTime());
            }
        });
    }

    private void initStatus() {
        final Intent intent = new Intent(this, AlarmResponseActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_ALARM_ID, mAlarm.getID());
        intent.putExtra(Constants.INTENT_EXTRA_ALARM_CURRENT_STATUS, mAlarm.getStatus());

        TextView statusText = findViewById(R.id.status_text);
        statusText.setText(mAlarm.getStatus());

        LinearLayout statusLayout = findViewById(R.id.status_layout);
        statusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Constants.LOG_TAG, "User clicked STATUS field for alarm ID: "
                        + mAlarm.getID());
                new AlertDialog.Builder(AlarmDetailsActivity.this)
                        .setTitle("Initiate Alarm Task")
                        .setMessage("This alarm task is incomplete.  Would you like to resume task completion?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                AlarmDetailsActivity.this.startActivity(intent);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

}