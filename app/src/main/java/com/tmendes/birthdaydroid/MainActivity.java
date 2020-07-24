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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.tmendes.birthdaydroid.contact.ContactsViewModel;
import com.tmendes.birthdaydroid.contact.android.ContactContentChangeObserver;
import com.tmendes.birthdaydroid.helpers.AlarmHelper;
import com.tmendes.birthdaydroid.permission.PermissionGranter;
import com.tmendes.birthdaydroid.receivers.LocalDateNowChangeReceiver;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Objects;
import java.util.TimeZone;

import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;

public class MainActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;

    private DrawerLayout drawerLayout;

    private SharedPreferences prefs;
    private LocalDateNowChangeReceiver localDateNowChangeReceiver;
    private ContactContentChangeObserver contactChangeContentObserver;
    private PermissionGranter permissionGranter;
    private SharedPreferences.OnSharedPreferenceChangeListener drawerMenuChangeListener;
    private SharedPreferences.OnSharedPreferenceChangeListener reloadContactsChangeListener;

    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        executeFirstRunInitializationIfNeeded(prefs, this);

        // Theme
        final boolean useDarkTheme = prefs.getBoolean("dark_theme", false);

        if (useDarkTheme) {
            setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ContactsViewModel contactsViewModel = ViewModelProviders.of(this).get(ContactsViewModel.class);
        contactChangeContentObserver = new ContactContentChangeObserver(this);

        // Permission Control
        permissionGranter = new PermissionGranter(this, () -> {
            contactsViewModel.reloadContacts();
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
        NavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        appBarConfiguration = new AppBarConfiguration.Builder(
                new HashSet<>(Arrays.asList(
                        R.id.nav_birthday_list,
                        R.id.nav_statistics_age_text,
                        R.id.nav_statistics_age_diagram,
                        R.id.nav_statistics_month_text,
                        R.id.nav_statistics_month_diagram,
                        R.id.nav_statistics_week_text,
                        R.id.nav_statistics_week_diagram,
                        R.id.nav_statistics_zodiac_text,
                        R.id.nav_statistics_zodiac_diagram,
                        R.id.nav_settings,
                        R.id.nav_about
                ))).setOpenableLayout(drawerLayout)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        // ZodiacDrawerMenuItem
        MenuItem[] zodiacDrawerMenuItems = new MenuItem[]{
                navView.getMenu().findItem(R.id.nav_statistics_zodiac_text),
                navView.getMenu().findItem(R.id.nav_statistics_zodiac_diagram)
        };

        drawerMenuChangeListener = new DrawerMenuPreferenceChangeListener(navView, zodiacDrawerMenuItems);
        prefs.registerOnSharedPreferenceChangeListener(drawerMenuChangeListener);

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        localDateNowChangeReceiver = new LocalDateNowChangeReceiver(this);
        getApplicationContext().registerReceiver(localDateNowChangeReceiver, intentFilter);

        reloadContactsChangeListener = (sharedPreferences, key) -> {
            if ("hide_ignored_contacts".equals(key) || "show_birthday_type_only".equals(key)) {
                contactsViewModel.reloadContacts();
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(reloadContactsChangeListener);
    }

    @Override
    protected void onDestroy() {
        prefs.unregisterOnSharedPreferenceChangeListener(drawerMenuChangeListener);
        prefs.unregisterOnSharedPreferenceChangeListener(reloadContactsChangeListener);
        getApplicationContext().unregisterReceiver(localDateNowChangeReceiver);
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
    public boolean onSupportNavigateUp() {
        if (drawerLayout.isOpen()) {
            drawerLayout.closeDrawers();
            return false;
        }

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isOpen()) {
            drawerLayout.closeDrawers();
        } else if (isRootView() && !doubleBackToExitPressedOnce) {
            Toast.makeText(this, getResources().getString(R.string.exit_warning_msg), Toast.LENGTH_SHORT).show();
            this.doubleBackToExitPressedOnce = true;
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        } else {
            super.onBackPressed();
        }
    }

    private boolean isRootView() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return navController.getPreviousBackStackEntry() == null;
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
