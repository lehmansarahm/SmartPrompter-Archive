package edu.temple.sp_admin.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import edu.temple.sp_admin.ActiveAlarmsActivity;
import edu.temple.sp_admin.CompleteAlarmsActivity;
import edu.temple.sp_admin.IncompleteAlarmsActivity;
import edu.temple.sp_admin.R;
import edu.temple.sp_res_lib.SpAlarmManager;

public class BaseActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final String INTENT_EXTRA_SELECTED_MENU_ITEM = "selected_menu_item";

    private static final int DEFAULT_VALUE_INT = -1;

    protected DrawerLayout mDrawerLayout;
    protected SpAlarmManager mAlarmMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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