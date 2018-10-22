package edu.temple.mci_res_lib2.activities;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import edu.temple.mci_res_lib2.alarms.Alarm;
import edu.temple.mci_res_lib2.alarms.MCIAlarmManager;
import edu.temple.mci_res_lib2.R;

import static edu.temple.mci_res_lib2.utils.Constants.AM;
import static edu.temple.mci_res_lib2.utils.Constants.INTENT_PARAM_ALARM_ID;
import static edu.temple.mci_res_lib2.utils.Constants.PM;

public class AlarmDetailActivity extends AppCompatActivity {

    private Alarm mItem;
    private int mItemID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        mItemID = getIntent().getIntExtra(INTENT_PARAM_ALARM_ID, 0);
        mItem = MCIAlarmManager.getAlarm(mItemID);
        populateView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, AlarmListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateView() {
        final Spinner hourSpinner = findViewById(R.id.hourSpinner);
        ArrayAdapter<String> hourAdapter = populateSpinner(hourSpinner, getHours());

        final Spinner minuteSpinner = findViewById(R.id.minuteSpinner);
        ArrayAdapter<String> minuteAdapter = populateSpinner(minuteSpinner, getMinutes());

        final Spinner amPmSpinner = findViewById(R.id.amPmSpinner);
        ArrayAdapter<String> amPmAdapter = populateSpinner(amPmSpinner, getAmPm());

        if (mItem != null) {
            final int hourPosition = hourAdapter.getPosition(String.valueOf(mItem.getHour()));
            setSpinnerSelection(hourSpinner, hourPosition);

            final int minutePosition = minuteAdapter.getPosition(getMinuteString(mItem.getMinute()));
            setSpinnerSelection(minuteSpinner, minutePosition);

            final int amPmPosition = amPmAdapter.getPosition(mItem.isAm() ? AM : PM);
            setSpinnerSelection(amPmSpinner, amPmPosition);

            final Button saveButton = findViewById(R.id.saveButton);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Alarm newItem = new Alarm(
                            Integer.parseInt(mItem.getPosition()),
                            Integer.parseInt(hourSpinner.getSelectedItem().toString()),
                            Integer.parseInt(minuteSpinner.getSelectedItem().toString()),
                            (amPmSpinner.getSelectedItem().toString().equals(AM) ? true : false),
                            Alarm.STATUS.Active
                    );
                    MCIAlarmManager.updateAlarm(mItemID, newItem);
                    NavUtils.navigateUpTo(AlarmDetailActivity.this,
                            new Intent(AlarmDetailActivity.this, AlarmListActivity.class));
                }
            });
        }
    }

    private void setSpinnerSelection(final Spinner spinner, final int position) {
        spinner.post(new Runnable() {
            @Override
            public void run() {
                spinner.setSelection(position);
            }
        });
    }

    private ArrayAdapter<String> populateSpinner(Spinner spinner, List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return adapter;
    }

    private static List<String> getHours() {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            list.add(String.valueOf(i));
        }
        return list;
    }

    private static String getMinuteString(int minute) {
        return String.format("%02d", minute);
    }

    private static List<String> getMinutes() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            list.add(getMinuteString(i));
        }
        return list;
    }

    private static List<String> getAmPm() {
        List<String> list = new ArrayList<>();
        list.add(AM);
        list.add(PM);
        return list;
    }

}