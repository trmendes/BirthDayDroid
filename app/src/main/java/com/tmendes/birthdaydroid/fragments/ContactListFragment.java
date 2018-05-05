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

package com.tmendes.birthdaydroid.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.tmendes.birthdaydroid.adapters.BirthDayArrayAdapter;
import com.tmendes.birthdaydroid.BirthDay;
import com.tmendes.birthdaydroid.R;

public class ContactListFragment extends Fragment {

    // Identifier for the permission request
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;

    // Search EditText
    private EditText inputSearch;

    // Adapter
    private BirthDayArrayAdapter adapter;

    // Birthdays
    private BirthDay birthdayData;

    // Context
    private Context ctx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_contact_list,
                container, false);

        this.ctx = container.getContext();

        PreferenceManager.setDefaultValues(this.ctx, R.xml.preferences, false);

        this.birthdayData = BirthDay.getBirthDayList(this.ctx);

        getPermissionToReadUserContacts();

        this.adapter = new BirthDayArrayAdapter(this.ctx, this.birthdayData.getList());

        ListView listView = (ListView) v.findViewById(R.id.lvContacts);
        listView.setTextFilterEnabled(true);
        listView.setAdapter(this.adapter);

        inputSearch = (EditText) v.findViewById(R.id.inputSearch);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                adapter.getFilter().filter(cs.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                String text = inputSearch.getText().toString();
                adapter.getFilter().filter(text);
            }
        });

        this.updateSortSettings();

        return v;
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
        birthdayData.refreshList();
        this.updateSortSettings();
    }

    private void updateSortSettings() {
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(this.ctx);
        int sortInput = Integer.valueOf(s.getString("sort_input", "0"));
        int sortMethod = Integer.valueOf(s.getString("sort_method", "0"));
        adapter.sort(sortInput, sortMethod);
    }

    // Called when the user is performing an action which requires the app to read the
    // user's contacts
    private void getPermissionToReadUserContacts() {
        if (ContextCompat.checkSelfPermission(this.ctx, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_CONTACTS)) {

                AlertDialog.Builder builderDialog = new AlertDialog.Builder(this.ctx);
                builderDialog.setMessage(ctx.getResources().getString(R.string.alert_contacts_dialog_msg));
                builderDialog.setCancelable(true);
                AlertDialog alertDialog = builderDialog.create();
                alertDialog.show();

            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSIONS_REQUEST);
        } else {
            birthdayData.refreshList();
        }
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
                Toast.makeText(this.ctx, getResources().getString(R.string.contact_request_grated), Toast.LENGTH_SHORT).show();
                birthdayData.refreshList();
            } else {
                Toast.makeText(this.ctx, getResources().getString(R.string.contact_request_denied), Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
