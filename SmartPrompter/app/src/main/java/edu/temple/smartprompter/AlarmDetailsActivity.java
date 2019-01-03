package edu.temple.smartprompter;

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

    public static final String INTENT_EXTRA_ALARM_ID = "intent_extra_alarm_id";

    private static final int DEFAULT_ALARM_ID = -1;

    private SpAlarmManager mAlarmMgr;
    private Alarm mAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_details);

        int alarmID = getIntent().getIntExtra(INTENT_EXTRA_ALARM_ID, DEFAULT_ALARM_ID);
        if (alarmID == DEFAULT_ALARM_ID) {
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
        TextView statusText = findViewById(R.id.status_text);
        statusText.setText(mAlarm.getStatus());

        LinearLayout statusLayout = findViewById(R.id.status_layout);
        statusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Constants.LOG_TAG, "User clicked STATUS field for alarm ID: "
                        + mAlarm.getID());
                Toast.makeText(AlarmDetailsActivity.this,
                        "STATUS CLICKED", Toast.LENGTH_SHORT).show();
            }
        });
    }

}