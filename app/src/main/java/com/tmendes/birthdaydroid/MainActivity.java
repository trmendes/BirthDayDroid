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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.navigation.NavigationView;
import com.tmendes.birthdaydroid.contact.ContactsViewModel;
import com.tmendes.birthdaydroid.contact.android.ContactContentChangeObserver;
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
import com.tmendes.birthdaydroid.helpers.AlarmHelper;
import com.tmendes.birthdaydroid.permission.PermissionGranter;
import com.tmendes.birthdaydroid.receivers.DayChangeReceiver;

import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean doubleBackToExitPressedOnce = false;

    private MenuItem zodiacDrawerMenuItem;

    private DrawerLayout drawerLayout;

    private SharedPreferences prefs;
    private DayChangeReceiver dateChangeReceiver;
    private ContactContentChangeObserver contactChangeContentObserver;
    private ContactsViewModel contactsViewModel;
    private PermissionGranter permissionGranter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        executeFirstRunInitializationIfNeeded(prefs, this);

        // Theme
        final boolean useDarkTheme = prefs.getBoolean("dark_theme", false);
        setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        if (useDarkTheme) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactsViewModel = ViewModelProviders.of(this).get(ContactsViewModel.class);
        contactChangeContentObserver = new ContactContentChangeObserver(this);

        // Permission Control
        permissionGranter = new PermissionGranter(this, () -> {
            contactsViewModel.reloadContactsAsync();
            getApplicationContext().getContentResolver().registerContentObserver(
                    ContactsContract.Contacts.CONTENT_URI,
                    true,
                    contactChangeContentObserver);
        });
        permissionGranter.grandReadContactOrExit();

        showBreakingChangeDialogAndMigrateIfNeeded();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        zodiacDrawerMenuItem = Objects.requireNonNull(navigationView).getMenu()
                .findItem(R.id.nav_statistics_zodiac);

        drawerLayout.closeDrawer(GravityCompat.START);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(@NonNull View view, float v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                Objects.requireNonNull(inputManager).hideSoftInputFromWindow(view.getWindowToken(),
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
                boolean hideZodiac = prefs.getBoolean("hide_zodiac", false);
                zodiacDrawerMenuItem.setVisible(!hideZodiac);
            }
        });

        showFragments(new ContactListFragment());


        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        dateChangeReceiver = new DayChangeReceiver(this);
        getApplicationContext().registerReceiver(dateChangeReceiver, intentFilter);

        prefs.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if ("hide_ignored_contacts".equals(key) || "show_birthday_type_only".equals(key)) {
                contactsViewModel.reloadContactsAsync();
            }
        });
    }

    @Override
    protected void onDestroy() {
        getApplicationContext().unregisterReceiver(dateChangeReceiver);
        getApplicationContext().getContentResolver().unregisterContentObserver(contactChangeContentObserver);
        super.onDestroy();
    }

    private void executeFirstRunInitializationIfNeeded(SharedPreferences prefs, Context ctx) {
        if (prefs.getBoolean("run_first_time", true)) {
            checkIsEnableBatteryOptimizations();
            new AlarmHelper().setAlarm(ctx, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("run_first_time", false);
            edit.apply();
        }
    }

    private void showBreakingChangeDialogAndMigrateIfNeeded() {
        if (!prefs.contains("breaking_change_v46")) {
            long scanDailyInterval = prefs.getLong("scan_daily_interval", 0);
            if (scanDailyInterval != 0) {
                // Migrade
                Calendar oldCalendar = Calendar.getInstance();
                oldCalendar.setTimeInMillis(scanDailyInterval);
                Calendar newCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                newCalendar.set(Calendar.HOUR_OF_DAY, oldCalendar.get(Calendar.HOUR_OF_DAY));
                newCalendar.set(Calendar.MINUTE, oldCalendar.get(Calendar.MINUTE));
                long scanDailyIntervalNew = newCalendar.getTimeInMillis();
                prefs.edit().putLong("scan_daily_interval", scanDailyIntervalNew).apply();

                AlarmHelper alarmHelper = new AlarmHelper();
                alarmHelper.cancelAlarm(this);
                alarmHelper.setAlarm(this, scanDailyIntervalNew);

                // Show message because migration is not save.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.breaking_change_dialog_title)
                        .setMessage(R.string.breaking_change_dialog_message_v46)
                        .show();
            }

            prefs.edit().putBoolean("breaking_change_v46", true).apply();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
            if (backStackEntryCount == 0) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.exit_warning_msg),
                            Toast.LENGTH_SHORT).show();
                    this.doubleBackToExitPressedOnce = true;
                    new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                }
            } else {
                getSupportFragmentManager().popBackStack(null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                /* No break */
            case R.id.nav_birthday_list:
                showFragments(new ContactListFragment());
                break;
            case R.id.nav_statistics_age:
                if (prefs.getBoolean("settings_statistics_as_text", false)) {
                    showFragments(new TextAgeFragment());
                } else {
                    showFragments(new BarChartAgeFragment());
                }
                break;
            case R.id.nav_statistics_zodiac:
                if (prefs.getBoolean("settings_statistics_as_text", false)) {
                    showFragments(new TextZodiacFragment());
                } else {
                    showFragments(new PieChartZodiacFragment());
                }
                break;
            case R.id.nav_statistics_week:
                if (prefs.getBoolean("settings_statistics_as_text", false)) {
                    showFragments(new TextWeekFragment());
                } else {
                    showFragments(new PieChartWeekFragment());
                }
                break;
            case R.id.nav_statistics_month:
                if (prefs.getBoolean("settings_statistics_as_text", false)) {
                    showFragments(new TextMonthFragment());
                } else {
                    showFragments(new PieChartMonthFragment());
                }
                break;
            case R.id.nav_settings:
                showFragments(new SettingsFragment());
                break;
            case R.id.nav_about:
                showFragments(new AboutUsFragment());
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void showFragments(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, fragment);
        if (!(fragment instanceof ContactListFragment)) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permissionGranter.onRequestPermissionsResultForReadContacts(requestCode, permissions, grantResults);
    }

    @SuppressLint("BatteryLife")
    private void checkIsEnableBatteryOptimizations() {
        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

        if (!Objects.requireNonNull(pm).isIgnoringBatteryOptimizations(packageName)) {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }
    }
}
