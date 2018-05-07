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
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.tmendes.birthdaydroid.fragments.AboutUsFragment;
import com.tmendes.birthdaydroid.fragments.ContactListFragment;
import com.tmendes.birthdaydroid.fragments.SettingsFragment;
import com.tmendes.birthdaydroid.fragments.StatisticsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // TAG - debug
    @SuppressWarnings("unused")
    public static final String TAG = "BirthDayDroid";

    // the Default time to notify the user about a birthday
    public static final int DEFAULT_ALARM_TIME = 8;

    // Identifier for the permission request
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;

    // Birthdays
    private BirthDay birthDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        birthDays = new BirthDay();

        refreshBirthDayList();

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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment;
        Class fragmentClass = null;

        switch (id) {
            case R.id.nav_birthday_list:
                fragmentClass = ContactListFragment.class;
                break;
            case R.id.nav_statistics:
                fragmentClass = StatisticsFragment.class;
                break;
            case R.id.nav_scan_now:
                if (this.birthDays.shallWeCelebrate(getApplicationContext())) {
                    Toast.makeText(this, getResources().getString(R.string.birthday_scan_found),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.birthday_scan_not_found),
                            Toast.LENGTH_LONG).show();
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

    public BirthDay getBirthDays() {
         return birthDays;
    }

    public void refreshBirthDayList() {

        if (!getPermissionToReadUserContacts()) {
            return;
        }

        Cursor c = getCursor();

        if (!c.moveToFirst()) {
            c.close();
            return;
        }

        final int keyColumn = c.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
        final int dateColumn = c.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
        final int nameColumn = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        final int photoColumn = c.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI);

        birthDays.clearLists();

        do {
            Contact contact = new Contact(getApplicationContext(),
                    c.getString(keyColumn),
                    c.getString(nameColumn),
                    c.getString(dateColumn),
                    c.getString(photoColumn));

            if (!contact.isMissingData()) {

                String sign = contact.getSign();
                int age = contact.getAge();
                int month = contact.getMonth();
                int bWeek = contact.getBirthDayWeek();

                if (birthDays.getAgeStats().get(age) != null) {
                    birthDays.getAgeStats().put(age, birthDays.getAgeStats().get(age) + 1);
                } else {
                    birthDays.getAgeStats().put(age, 1);
                }
                if (birthDays.getSignStats().get(sign) != null) {
                    birthDays.getSignStats().put(sign,
                            birthDays.getSignStats().get(sign) + 1);
                } else {
                    birthDays.getSignStats().put(sign, 1);
                }
                if (birthDays.getMonthStats().get(month) != null) {
                    birthDays.getMonthStats().put(month,
                            birthDays.getMonthStats().get(month) + 1);
                } else {
                    birthDays.getMonthStats().put(month, 1);
                }
                if (birthDays.getWeekStats().get(bWeek) != null) {
                    birthDays.getWeekStats().put(bWeek,
                            birthDays.getWeekStats().get(bWeek) + 1);
                } else {
                    birthDays.getWeekStats().put(bWeek, 1);
                }

            }

            birthDays.getList().add(contact);


        } while (c.moveToNext());

        c.close();
    }

    private Cursor getCursor() {
        ContentResolver r = getApplicationContext().getContentResolver();

        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.CommonDataKinds.Event.START_DATE,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
        };

        String selection = ContactsContract.Data.MIMETYPE +
                "=? AND " + ContactsContract.CommonDataKinds.Event.TYPE + "=?";

        String[] args = new String[]{
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
                Integer.toString(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
        };

        return r.query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                selection,
                args,
                null
        );
    }

    // Called when the user is performing an action which requires the app to read the
    // user's contacts
    private boolean getPermissionToReadUserContacts() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_CONTACTS)) {

                AlertDialog.Builder builderDialog = new AlertDialog
                        .Builder(getApplicationContext());
                builderDialog.setMessage(getApplicationContext().getResources()
                        .getString(R.string.alert_contacts_dialog_msg));
                builderDialog.setCancelable(true);
                AlertDialog alertDialog = builderDialog.create();
                alertDialog.show();

            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSIONS_REQUEST);

            return false;
        }

        return true;
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.contact_request_grated),
                        Toast.LENGTH_SHORT).show();
                refreshBirthDayList();
            } else {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.contact_request_denied),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
