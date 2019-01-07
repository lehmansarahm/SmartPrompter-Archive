package edu.temple.sp_admin.utils;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import edu.temple.sp_admin.ActiveAlarmsActivity;
import edu.temple.sp_admin.CompleteAlarmsActivity;
import edu.temple.sp_admin.IncompleteAlarmsActivity;
import edu.temple.sp_admin.R;
import edu.temple.sp_admin.fragments.MissingPermissionsFragment;
import edu.temple.sp_res_lib.SpAlarmManager;

public abstract class BaseActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    protected static final String DEFAULT_FRAGMENT_TAG = "default_fragment";

    protected static final String INTENT_EXTRA_SELECTED_MENU_ITEM = "selected_menu_item";

    protected static final int DEFAULT_VALUE_INT = -1;

    protected DrawerLayout mDrawerLayout;
    protected SpAlarmManager mAlarmMgr;

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private static final int PERMISSION_REQUEST_CODE = 101;
    private static final String[] PERMISSIONS = new String[] {
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
        FragmentManager fm = getSupportFragmentManager();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (isTaskRoot()) {
            new AlertDialog.Builder(this)
                        .setTitle("No Prior Screens")
                        .setMessage("Pressing back will exit the app.  "
                                + "Are you sure you wish to continue?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                BaseActivity.super.onBackPressed();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
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

        // NOTE - assuming that each activity's default fragment is tagged with
        // a non-null flag, this command will pop everything BUT the default
        // fragment before redirecting to the next activity selected from the nav
        getSupportFragmentManager().popBackStack(null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Intent intent = new Intent();
        Context pkgContext = BaseActivity.this;

        switch (menuItem.getItemId()) {
            case R.id.nav_active_alarms:
                intent.putExtra(INTENT_EXTRA_SELECTED_MENU_ITEM, 0);
                intent.setClass(pkgContext, ActiveAlarmsActivity.class);
                break;
            case R.id.nav_incomplete_alarms:
                intent.putExtra(INTENT_EXTRA_SELECTED_MENU_ITEM, 1);
                intent.setClass(pkgContext, IncompleteAlarmsActivity.class);
                break;
            case R.id.nav_complete_alarms:
                intent.putExtra(INTENT_EXTRA_SELECTED_MENU_ITEM, 2);
                intent.setClass(pkgContext, CompleteAlarmsActivity.class);
                break;
            default:
                // do nothing
        }

        startActivity(intent);
        return true;
    }

}