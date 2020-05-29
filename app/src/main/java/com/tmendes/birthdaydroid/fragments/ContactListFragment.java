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

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.tmendes.birthdaydroid.Contact;
import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.adapters.ContactsDataAdapter;
import com.tmendes.birthdaydroid.helpers.DBHelper;
import com.tmendes.birthdaydroid.helpers.RecyclerItemTouchHelper;
import com.tmendes.birthdaydroid.providers.BirthdayDataProvider;

import java.util.Objects;

import static android.content.Context.SEARCH_SERVICE;
import static androidx.recyclerview.widget.ItemTouchHelper.Callback.getDefaultUIUtil;


public class ContactListFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;
    private BirthdayDataProvider bddDataProviver;
    private ContactsDataAdapter contactsDataAdapter;
    private boolean hideIgnoredContacts;
    private DBHelper dbHelper;
    private CoordinatorLayout coordinatorLayout;
    private SharedPreferences prefs;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_contact_list,
                container, false);

        setHasOptionsMenu(true);

        PreferenceManager.setDefaultValues(getContext(), R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        hideIgnoredContacts = prefs.getBoolean("hide_ignored_contacts", false);

        bddDataProviver = BirthdayDataProvider.getInstance();
        dbHelper = new DBHelper(getContext());

        contactsDataAdapter = new ContactsDataAdapter(getContext(),
                bddDataProviver.getAllContacts());

        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(contactsDataAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        coordinatorLayout = v.findViewById(R.id.coordinator_layout);

        Objects.requireNonNull(getActivity()).getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_INSERT,
                        ContactsContract.Contacts.CONTENT_URI);
                startActivity(intent);
            }
        });

        showHideAddNewBirthday();

        return v;
    }

    private void showHideAddNewBirthday() {
        boolean hideAddNewBirthday = prefs.getBoolean("show_add_contact_fab", false);
        if (hideAddNewBirthday) {
            fab.hide();
        } else {
            fab.show();
        }
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar, menu);
        SearchManager searchManager = (SearchManager) Objects.requireNonNull(getContext())
                .getSystemService(SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(Objects.requireNonNull(searchManager)
                .getSearchableInfo(Objects.requireNonNull(getActivity()).getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String query) {
                    contactsDataAdapter.getFilter().filter(query);
                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    contactsDataAdapter.getFilter().filter(query);
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.app_bar_search) {
            return false;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        // ToDo rename Preferences
        int sortOrder = Integer.parseInt(prefs.getString("sort_input", "0"));
        int sortType = Integer.parseInt(prefs.getString("sort_method", "0"));

        showHideAddNewBirthday();

        bddDataProviver.refreshData(false);
        contactsDataAdapter.sort(sortOrder, sortType);
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

            InputMethodManager inputManager = (InputMethodManager)
                    Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);

            View currentFocus = getActivity().getCurrentFocus();

            if (currentFocus != null) {
                Objects.requireNonNull(inputManager).hideSoftInputFromWindow(currentFocus.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, contact.getName(), Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.RED);

            snackbar.setAction(Objects.requireNonNull(getContext()).getResources().getString(R.string.undo),
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
