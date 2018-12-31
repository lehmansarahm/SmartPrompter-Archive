package edu.temple.smartprompter;

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
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ActiveAlarmsAdapter.AlarmDetailsListener,
        DatePickerFragment.DatePickerListener,
        TimePickerFragment.TimePickerListener {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
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
                ActiveAlarmListFragment aaf = ActiveAlarmListFragment.newInstance();
                ft.replace(R.id.fragment_container, aaf);
                break;
            case R.id.nav_incomplete_alarms:
                IncompleteAlarmListFragment iaf = IncompleteAlarmListFragment.newInstance();
                ft.replace(R.id.fragment_container, iaf);
                break;
            case R.id.nav_complete_alarms:
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

    @Override
    public void onAlarmSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ActiveAlarmDetailsFragment aadf = ActiveAlarmDetailsFragment.newInstance(position);
        ft.replace(R.id.fragment_container, aadf);
        ft.addToBackStack(null);
        ft.commit();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onDatePickerRequested(int[] date) {
        DialogFragment newFragment = DatePickerFragment.newInstance(date);
        newFragment.show(getSupportFragmentManager(), "datePicker");
        // TODO - grab the date the user selects, update the current alarm
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onTimePickerRequested(int[] time) {
        DialogFragment newFragment = TimePickerFragment.newInstance(time);
        newFragment.show(getSupportFragmentManager(), "timePicker");
        // TODO - grab the time the user selects, update the current alarm
    }

}