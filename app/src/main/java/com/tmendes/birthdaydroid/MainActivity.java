/*
 * Copyright (C) 2015-2016 The Food Restriction Project Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tmendes.birthdaydroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.tmendes.birthdaydroid.fragments.AboutUsFragment;
import com.tmendes.birthdaydroid.fragments.BarChartAgeFragment;
import com.tmendes.birthdaydroid.fragments.ContactListFragment;
import com.tmendes.birthdaydroid.fragments.PieChartMonthFragment;
import com.tmendes.birthdaydroid.fragments.PieChartWeekFragment;
import com.tmendes.birthdaydroid.fragments.PieChartZodiacFragment;
import com.tmendes.birthdaydroid.fragments.SettingsFragment;
import com.tmendes.birthdaydroid.fragments.TextAgeFragment;
import com.tmendes.birthdaydroid.fragments.TextMonthFragment;
import com.tmendes.birthdaydroid.fragments.TextWeekFragment;
import com.tmendes.birthdaydroid.fragments.TextZodiacFragment;
import com.tmendes.birthdaydroid.helpers.PermissionHelper;
import com.tmendes.birthdaydroid.providers.BirthdayDataProvider;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // the Default time to notify the user about a birthday
    public static final int DEFAULT_ALARM_TIME = 8;

    // Permission Control
    private PermissionHelper permissionHelper;
    private static final int PERMISSION_CONTACT_READ = 100;

    private boolean doubleBackToExitPressedOnce = false;

    private boolean statisticsAsText = false;

    private MenuItem zodiacDrawerMenuItem;

    private DrawerLayout drawer;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean useDarkTheme = prefs.getBoolean("dark_theme", false);
        this.statisticsAsText = prefs.getBoolean("settings_statistics_as_text", false);

        if (useDarkTheme) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionHelper = new PermissionHelper(this);

        checkIsEnableBatteryOptimizations();
        requestForPermissions();

        // Birthdays
        BirthdayDataProvider bddDataProvider = BirthdayDataProvider.getInstance();
        bddDataProvider.setPermissionHelper(getApplicationContext(), permissionHelper);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        zodiacDrawerMenuItem = Objects.requireNonNull(navigationView).getMenu()
                .findItem(R.id.nav_statistics_zodiac);

        drawer = findViewById(R.id.drawer_layout);

        if (drawer != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            drawer.closeDrawer(GravityCompat.START);

            drawer.addDrawerListener(new DrawerLayout.DrawerListener() {

                @Override
                public void onDrawerSlide(@NonNull View view, float v) {
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }

                @Override
                public void onDrawerOpened(@NonNull View view) {
                }

                @Override
                public void onDrawerClosed(@NonNull View view) {

                }

                @Override
                public void onDrawerStateChanged(int i) {
                    boolean hideZoadiac = prefs.getBoolean("hide_zodiac", false);
                    zodiacDrawerMenuItem.setVisible(!hideZoadiac);
                    statisticsAsText = prefs.getBoolean("settings_statistics_as_text", false);
                }
            });
        }

        openContactFragments();
    }

    private void openContactFragments() {
        Fragment fragment;
        try {
            fragment = ContactListFragment.class.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            if (backStackEntryCount == 0) {
                Toast.makeText(this, getResources().getString(R.string.exit_warning_msg), Toast.LENGTH_SHORT).show();
                this.doubleBackToExitPressedOnce = true;
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment;
        Class fragmentClass = null;

        switch (id) {
            case android.R.id.home:
                finish();
                /* No break */
            case R.id.nav_birthday_list:
                fragmentClass = ContactListFragment.class;
                break;
            case R.id.nav_statistics_age:
                if (this.statisticsAsText) {
                    fragmentClass = TextAgeFragment.class;
                } else {
                    fragmentClass = BarChartAgeFragment.class;
                }
                break;
            case R.id.nav_statistics_zodiac:
                if (this.statisticsAsText) {
                    fragmentClass = TextZodiacFragment.class;
                } else {
                    fragmentClass = PieChartZodiacFragment.class;
                }
                break;
            case R.id.nav_statistics_week:
                if (this.statisticsAsText) {
                    fragmentClass = TextWeekFragment.class;
                } else {
                    fragmentClass = PieChartWeekFragment.class;
                }
                break;
            case R.id.nav_statistics_month:
                if (this.statisticsAsText) {
                    fragmentClass = TextMonthFragment.class;
                } else {
                    fragmentClass = PieChartMonthFragment.class;
                }
                break;
            case R.id.nav_settings:
                fragmentClass = SettingsFragment.class;
                break;
            case R.id.nav_about:
                fragmentClass = AboutUsFragment.class;
                break;
        }

        if (fragmentClass != null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment);

                if (fragmentClass != ContactListFragment.class) {
                    transaction.addToBackStack(null);
                }

                transaction.commit();

            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void requestForPermissions() {

        String permissionString;

        permissionString = Manifest.permission.READ_CONTACTS;

        if (ContextCompat.checkSelfPermission(this, permissionString)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permissionString)) {
                displayPermissionExplanation();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        PERMISSION_CONTACT_READ);
            }
        } else {
            permissionHelper.updatePermissionPreferences(PermissionHelper.CONTACT_PERMISSION, true);
            openContactFragments();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CONTACT_READ) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionHelper.updatePermissionPreferences(PermissionHelper.CONTACT_PERMISSION, true);
                openContactFragments();
            } else {
                permissionHelper.updatePermissionPreferences(PermissionHelper.CONTACT_PERMISSION, false);
            }
        }
    }

    @SuppressLint("BatteryLife")
    private void checkIsEnableBatteryOptimizations()
    {
        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

        if (!Objects.requireNonNull(pm).isIgnoringBatteryOptimizations(packageName)) {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }
    }

    private void displayPermissionExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(getResources().getString(R.string.alert_contacts_dialog_msg));
        builder.setTitle(getResources().getString(R.string.alert_contats_dialog_title));

        builder.setPositiveButton(getResources().getString(R.string.alert_permissions_allow), new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        PERMISSION_CONTACT_READ);
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.alert_permissions_deny), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
