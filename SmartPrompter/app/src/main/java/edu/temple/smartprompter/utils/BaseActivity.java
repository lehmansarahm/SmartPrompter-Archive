package edu.temple.smartprompter.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import edu.temple.smartprompter.ActiveAlarmsActivity;
import edu.temple.smartprompter.CompleteAlarmsActivity;
import edu.temple.smartprompter.R;
import edu.temple.smartprompter.fragments.MissingPermissionsFragment;
import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.SpAlarmManager;

public abstract class BaseActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final String INTENT_EXTRA_SELECTED_MENU_ITEM = "selected_menu_item";
    private static final int DEFAULT_VALUE_INT = -1;

    protected SpAlarmManager mAlarmMgr;
    protected Alarm mAlarm;
    protected int mAlarmID;
    protected String mAlarmStatus;

    protected DrawerLayout mDrawerLayout;

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private static final int PERMISSION_REQUEST_CODE = 201;
    private static final String[] PERMISSIONS = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    protected boolean checkPermissions() {
        boolean permissionsGranted = true;
        for (String permission : PERMISSIONS) {
            permissionsGranted &=
                    (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }

        if (permissionsGranted)
            return true;

        ActivityCompat.requestPermissions(this, PERMISSIONS,
                PERMISSION_REQUEST_CODE);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initNavigation();
                    showDefaultFragment();
                } else {
                    showMissingPermissionsFragment();
                }
            }
        }
    }

    protected void initNavigation() {
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

        if (getIntent().hasExtra(INTENT_EXTRA_SELECTED_MENU_ITEM)) {
            int selectedMenuItemID = getIntent().getIntExtra(INTENT_EXTRA_SELECTED_MENU_ITEM,
                    DEFAULT_VALUE_INT);
            if (selectedMenuItemID != DEFAULT_VALUE_INT) {
                try {
                    Log.i(Constants.LOG_TAG, "Received pre-selected navigation item.  "
                            + "Selecting index: " + selectedMenuItemID);
                    navigationView.getMenu().getItem(selectedMenuItemID).setChecked(true);
                } catch (Exception ex) {
                    Log.e(Constants.LOG_TAG, "Something went wrong while trying to "
                            + "pre-select nav menu item with index: " + selectedMenuItemID);
                }
            }
        }
    }

    protected abstract void showDefaultFragment();

    protected void showMissingPermissionsFragment() {
        Log.i(Constants.LOG_TAG, "Populating current activity with missing-permissions fragment.");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        MissingPermissionsFragment fragment = new MissingPermissionsFragment();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
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

        Intent intent = new Intent();
        Context pkgContext = BaseActivity.this;

        switch (menuItem.getItemId()) {
            case R.id.nav_active_alarms:
                intent.putExtra(INTENT_EXTRA_SELECTED_MENU_ITEM, 0);
                intent.setClass(pkgContext, ActiveAlarmsActivity.class);
                break;
            case R.id.nav_complete_alarms:
                intent.putExtra(INTENT_EXTRA_SELECTED_MENU_ITEM, 1);
                intent.setClass(pkgContext, CompleteAlarmsActivity.class);
                break;
            default:
                // do nothing
        }

        startActivity(intent);
        return true;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    protected boolean verifyIntentExtras() {
        if (!getIntent().hasExtra(Constants.INTENT_EXTRA_ALARM_ID)) {
            Log.e(Constants.LOG_TAG, "Alarm response has been initiated, "
                    + "but intent was missing the alarm ID.");
            return false;
        }

        if (!getIntent().hasExtra(Constants.INTENT_EXTRA_ALARM_CURRENT_STATUS)) {
            Log.e(Constants.LOG_TAG, "Alarm response has been initiated, "
                    + "but intent was missing the alarm's current status.");
            return false;
        }

        // parse out the intent extras
        mAlarmID = getIntent().getIntExtra(Constants.INTENT_EXTRA_ALARM_ID,
                Constants.DEFAULT_ALARM_ID);
        mAlarmStatus = getIntent().getStringExtra(Constants.INTENT_EXTRA_ALARM_CURRENT_STATUS);
        Log.i(Constants.LOG_TAG, "Received alarm response request for alarm with ID: "
                + mAlarmID + " \t\t and original alarm status: " + mAlarmStatus);

        return true;
    }

    protected void updateAlarmStatus(Alarm.STATUS newStatus) {
        // retrieve alarm record
        mAlarmMgr = new SpAlarmManager(this);
        mAlarm = mAlarmMgr.get(mAlarmID);
        mAlarm.updateStatus(newStatus);

        // check to see if a timestamp is necessary
        if (newStatus == Alarm.STATUS.Incomplete) {
            // user completed acknowledgement phase
            mAlarm.updateTimeAcknowledged();
        } else if (newStatus == Alarm.STATUS.Complete) {
            // user completed entire task
            mAlarm.updateTimeCompleted();
        }

        // commit changes to database
        mAlarmMgr.update(mAlarm);

        // just for sanity's sake ...
        mAlarm = mAlarmMgr.get(mAlarmID);
        Log.i(Constants.LOG_TAG, "Received and acknowledged alarm response for alarm ID: "
                + mAlarm.getID() + ".  \t\t and updated alarm status: " + mAlarm.getStatusString());
    }

    protected void startNextActivity(Context origContext, Class nextActClass) {
        Intent intent = new Intent(origContext, nextActClass);
        intent.putExtra(Constants.INTENT_EXTRA_ALARM_ID, mAlarmID);
        intent.putExtra(Constants.INTENT_EXTRA_ALARM_CURRENT_STATUS, mAlarmStatus);
        startActivity(intent);
    }

}