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
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.tmendes.birthdaydroid.fragments.AboutUsFragment;
import com.tmendes.birthdaydroid.fragments.ContactListFragment;
import com.tmendes.birthdaydroid.fragments.DonationFragment;
import com.tmendes.birthdaydroid.fragments.SettingsFragment;
import com.tmendes.birthdaydroid.fragments.StatisticsFragment;
import com.tmendes.birthdaydroid.helpers.PermissionHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // the Default time to notify the user about a birthday
    public static final int DEFAULT_ALARM_TIME = 8;

    // Birthdays
    private BirthDay birthDays;

    // Permission Control
    private PermissionHelper permissionHelper;
    private static final int PERMISSION_CONTACT_READ = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionHelper = new PermissionHelper(this);
        requestForPermissions(PERMISSION_CONTACT_READ);

        birthDays = new BirthDay(getApplicationContext(), permissionHelper);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
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
        //FIXME Add a better message to warn the user about the back button
        new AlertDialog.Builder(this)
                .setMessage(getBaseContext().getResources().getString(R.string.exit_dialog))
                .setCancelable(false)
                .setPositiveButton(getBaseContext().getResources().getString(R.string.exit_yes),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton(getBaseContext().getResources().getString(R.string.exit_no),
                        null)
                .show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment;
        Class fragmentClass = null;

        switch (id) {
            case android.R.id.home:
                finish();
            case R.id.nav_birthday_list:
                fragmentClass = ContactListFragment.class;
                break;
            case R.id.nav_statistics:
                fragmentClass = StatisticsFragment.class;
                break;
            case R.id.nav_scan_now:
                isTodayADayToCelebrate();
                break;
            case R.id.nav_settings:
                fragmentClass = SettingsFragment.class;
                break;
            case R.id.nav_donations:
                fragmentClass = DonationFragment.class;
                break;
            case R.id.nav_about:
                fragmentClass = AboutUsFragment.class;
                break;
        }

        if (fragmentClass != null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void isTodayADayToCelebrate() {
        ArrayList<Contact> notifications = this.birthDays.shallWeCelebrate();
        if (notifications.size() == 0) {
            Toast.makeText(this, getResources().getString(R.string.birthday_scan_not_found),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.birthday_scan_found),
                    Toast.LENGTH_LONG).show();
            for (Contact contact : notifications) {
                birthDays.postNotification(contact);
            }
        }
    }

    public BirthDay getBirthday() {
         return birthDays;
    }

    private void requestForPermissions(int permission) {

        String permissionString = "";
        boolean isPermissionValid;

        if (permission == PERMISSION_CONTACT_READ) {
            permissionString = Manifest.permission.READ_CONTACTS;
            isPermissionValid = true;
        } else {
            isPermissionValid = false;
        }

        if (isPermissionValid) {
            if (ContextCompat.checkSelfPermission(this, permissionString)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        permissionString)) {
                    displayPermissionExplanation(permission);
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSION_CONTACT_READ);
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
                permissionHelper.updatePermissionPreferences(PermissionHelper.CONTACT_PERMISSION, true);
                openContactFragments();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CONTACT_READ: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionHelper.updatePermissionPreferences(PermissionHelper.CONTACT_PERMISSION, true);
                    openContactFragments();
                } else {
                    permissionHelper.updatePermissionPreferences(PermissionHelper.CONTACT_PERMISSION, false);
                }
            }
        }
    }

    private void displayPermissionExplanation(final int permission) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (permission) {
            case PERMISSION_CONTACT_READ:
                builder.setMessage(getResources().getString(R.string.alert_contacts_dialog_msg));
                builder.setTitle(getResources().getString(R.string.alert_contats_dialog_title));
        }

        builder.setPositiveButton(getResources().getString(R.string.alert_permissions_allow), new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (permission) {
                    case PERMISSION_CONTACT_READ:
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_CONTACTS},
                                PERMISSION_CONTACT_READ);
                        break;
                }
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
