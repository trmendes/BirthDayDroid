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

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.tmendes.birthdaydroid.Contact;
import com.tmendes.birthdaydroid.adapters.ContactsDataAdapter;
import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.helpers.DBHelper;
import com.tmendes.birthdaydroid.helpers.RecyclerItemTouchHelper;
import com.tmendes.birthdaydroid.providers.BirthdayDataProvider;

import java.util.Objects;

import static android.support.v7.widget.helper.ItemTouchHelper.Callback.getDefaultUIUtil;


public class ContactListFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private BirthdayDataProvider bddDataProviver;
    private EditText inputSearch;
    private ContactsDataAdapter contactsDataAdapter;
    private boolean hideIgnoredContacts;
    private DBHelper dbHelper;
    private CoordinatorLayout coordinatorLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_contact_list,
                container, false);

        PreferenceManager.setDefaultValues(getContext(), R.xml.preferences, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        hideIgnoredContacts = prefs.getBoolean("hide_ignored_contacts", false);

        bddDataProviver = BirthdayDataProvider.getInstance();
        dbHelper = new DBHelper(getContext());

        contactsDataAdapter = new ContactsDataAdapter(getContext(),
                bddDataProviver.getAllContacts());

        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(contactsDataAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        inputSearch = v.findViewById(R.id.inputSearch);
        coordinatorLayout = v.findViewById(R.id.coordinator_layout);

        Objects.requireNonNull(getActivity()).getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                contactsDataAdapter.getFilter().filter(cs.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                String text = inputSearch.getText().toString();
                contactsDataAdapter.getFilter().filter(text);
            }
        });

        inputSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputManager = (InputMethodManager) Objects
                            .requireNonNull(getContext()).
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    Objects.requireNonNull(inputManager).hideSoftInputFromWindow(
                            Objects.requireNonNull(getView()).getWindowToken(), 0);
                }
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        int sortInput = Integer.valueOf(prefs.getString("sort_input", "0"));
        int sortMethod = Integer.valueOf(prefs.getString("sort_method", "0"));

        bddDataProviver.refreshData(false);
        contactsDataAdapter.sort(sortInput, sortMethod);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder instanceof ContactsDataAdapter.ContactViewHolder) {
            final int index = viewHolder.getAdapterPosition();
            final int dir = direction;
            final Contact contact = contactsDataAdapter.getContact(index);

            if (direction == ItemTouchHelper.LEFT) {
                contact.setIgnore();
                contactsDataAdapter.ignoreItem(index, hideIgnoredContacts);
            }
            if (direction == ItemTouchHelper.RIGHT) {
                contact.setFavorite();
                contactsDataAdapter.favoriteItem(index);
            }

            if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {
                long dbID = dbHelper.insertContact(contact.getDbID(), contact.getKey(),
                        contact.isFavorite(), contact.isIgnore());
                contact.setDbID(dbID);
            }

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, contact.getName(), Snackbar.LENGTH_LONG);
            snackbar.setAction(getContext().getResources().getString(R.string.undo),
                    new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dir == ItemTouchHelper.LEFT) {
                        contact.setIgnore();
                        if (hideIgnoredContacts) {
                            contactsDataAdapter.restoreContact(contact);
                        } else {
                            contactsDataAdapter.notifyDataSetChanged();
                        }
                    }
                    if (dir == ItemTouchHelper.RIGHT) {
                        contact.setFavorite();
                        contactsDataAdapter.favoriteItem(index);
                    }

                    if (dir == ItemTouchHelper.LEFT || dir == ItemTouchHelper.RIGHT) {
                        long dbID = dbHelper.insertContact(contact.getDbID(), contact.getKey(),
                                contact.isFavorite(), contact.isIgnore());
                        contact.setDbID(dbID);
                    }
                }
            });
            snackbar.show();
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        final View foregroundView = ((ContactsDataAdapter.ContactViewHolder) viewHolder)
                .itemLayout;
        final View ignoredLayout = ((ContactsDataAdapter.ContactViewHolder) viewHolder)
                .ignoreContactLayout;
        final View favoriteLayout = ((ContactsDataAdapter.ContactViewHolder) viewHolder)
                .favoriteContactLayout;
        
        if (dX > 0) {
            ignoredLayout.setVisibility(View.INVISIBLE);
            favoriteLayout.setVisibility(View.VISIBLE);
        } else if (dX < 0) {
            ignoredLayout.setVisibility(View.VISIBLE);
            favoriteLayout.setVisibility(View.INVISIBLE);
        } else {
            favoriteLayout.setVisibility(View.INVISIBLE);
            ignoredLayout.setVisibility(View.INVISIBLE);
        }

        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState,
                isCurrentlyActive);
    }
}
