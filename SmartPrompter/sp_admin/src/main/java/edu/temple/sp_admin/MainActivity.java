package edu.temple.sp_admin;

import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import edu.temple.sp_admin.adapters.ActiveAlarmsAdapter;
import edu.temple.sp_admin.fragments.ActiveAlarmDetailsFragment;
import edu.temple.sp_admin.fragments.ActiveAlarmListFragment;
import edu.temple.sp_admin.fragments.CompleteAlarmListFragment;
import edu.temple.sp_admin.fragments.DatePickerFragment;
import edu.temple.sp_admin.fragments.IncompleteAlarmListFragment;
import edu.temple.sp_admin.fragments.TimePickerFragment;
import edu.temple.sp_admin.fragments.WelcomeFragment;
import edu.temple.sp_admin.utils.Constants;
import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.SpAlarmManager;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ActiveAlarmsAdapter.AlarmDetailsListener,
        DatePickerFragment.DatePickerListener,
        TimePickerFragment.TimePickerListener,
        ActiveAlarmDetailsFragment.AlarmDetailChangeListener,
        ActiveAlarmListFragment.AlarmCreationListener {

    private DrawerLayout mDrawerLayout;
    private SpAlarmManager mAlarmMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOG_TAG, "Main Activity created!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAlarmMgr = new SpAlarmManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        WelcomeFragment fragment = new WelcomeFragment();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    @Override
    public void onPause() {
        Log.i(Constants.LOG_TAG, "Main Activity paused!");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.i(Constants.LOG_TAG, "Main Activity destroyed!");
        // TODO - commit alarm data to persistent storage
        super.onDestroy();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        switch (menuItem.getItemId()) {
            case R.id.nav_active_alarms:
                Log.i(Constants.LOG_TAG, "Active Alarms list selected from nav drawer.");
                ActiveAlarmListFragment aaf = ActiveAlarmListFragment.newInstance();
                ft.replace(R.id.fragment_container, aaf);
                break;
            case R.id.nav_incomplete_alarms:
                Log.i(Constants.LOG_TAG, "Incomplete Alarms list selected from nav drawer.");
                IncompleteAlarmListFragment iaf = IncompleteAlarmListFragment.newInstance();
                ft.replace(R.id.fragment_container, iaf);
                break;
            case R.id.nav_complete_alarms:
                Log.i(Constants.LOG_TAG, "Complete Alarms list selected from nav drawer.");
                CompleteAlarmListFragment caf = CompleteAlarmListFragment.newInstance();
                ft.replace(R.id.fragment_container, caf);
                break;
            default:
                return true;
        }

        ft.addToBackStack(null);
        ft.commit();
        return true;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private ActiveAlarmDetailsFragment aadf;

    @Override
    public void onAlarmSelected(int alarmID) {
        Log.i(Constants.LOG_TAG, "User wants to view details of alarm ID: " + alarmID);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        aadf = ActiveAlarmDetailsFragment.newInstance(alarmID);
        ft.replace(R.id.fragment_container, aadf);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onDatePickerRequested(int alarmID, int[] date) {
        Log.i(Constants.LOG_TAG, "User wants to view a date picker dialog with default date: "
                + date[1] + "/" + date[2] + "/" + date[0]);
        DialogFragment newFragment = DatePickerFragment.newInstance(alarmID, date);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDatePicked(int alarmID, int year, int month, int day) {
        Log.i(Constants.LOG_TAG, "User selected the following date from the picker: "
                + month + "/" + day + "/" + year + " \t\t for current alarm: "
                + alarmID);
        aadf.updateDate(year, month, day);

        /*Alarm currentAlarm = mAlarmMgr.get(alarmID);
        currentAlarm.updateDate(year, month, day);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.detach(aadf);
        ft.attach(aadf);
        ft.commit();*/
    }

    @Override
    public void onTimePickerRequested(int alarmID, int[] time) {
        Log.i(Constants.LOG_TAG, "User wants to view a time picker dialog with default time: "
                + time[0] + ":" + time[1]);
        DialogFragment newFragment = TimePickerFragment.newInstance(alarmID, time);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimePicked(int alarmID, int hourOfDay, int minute) {
        Log.i(Constants.LOG_TAG, "User selected the following time from the picker: "
                + hourOfDay + ":" + minute + " \t\t for current alarm: "
                + alarmID);
        aadf.updateTime(hourOfDay, minute);

        /*Alarm currentAlarm = mAlarmMgr.get(alarmID);
        currentAlarm.updateTime(hourOfDay, minute);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.detach(aadf);
        ft.attach(aadf);
        ft.commit();*/
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onAlarmDetailsChanged() {
        Log.i(Constants.LOG_TAG, "User has made changes to an alarm!  Reloading the "
                + "active alarm list ...");

        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(); // remove alarm details fragment from backstack
        fm.popBackStack(); // remove previous view of alarm list from backstack (keep current)

        FragmentTransaction ft = fm.beginTransaction();
        ActiveAlarmListFragment aaf = ActiveAlarmListFragment.newInstance();
        ft.replace(R.id.fragment_container, aaf);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onAlarmCreated(int position) {
        onAlarmSelected(position);
    }

}