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

package com.tmendes.birthdaydroid.views.contactlist;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.contact.db.DBContactService;
import com.tmendes.birthdaydroid.views.AbstractContactsFragment;

import java.util.List;
import java.util.Objects;

import static android.content.Context.SEARCH_SERVICE;


public class ContactListFragment extends AbstractContactsFragment implements ContactViewHolderTouchHelper.SwipeListener {

    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;
    private ContactsDataAdapter contactsDataAdapter;
    private boolean hideIgnoredContacts;
    private DBContactService dbContactService;
    private CoordinatorLayout coordinatorLayout;
    private SharedPreferences prefs;
    private FloatingActionButton fab;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbContactService = new DBContactService(getContext());
        contactsDataAdapter = new ContactsDataAdapter(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_contact_list,
                container, false);

        setHasOptionsMenu(true);

//        PreferenceManager.setDefaultValues(getContext(), R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        hideIgnoredContacts = prefs.getBoolean("hide_ignored_contacts", false);

        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(contactsDataAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ContactViewHolderTouchHelper(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        coordinatorLayout = v.findViewById(R.id.coordinator_layout);

        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_INSERT,
                    ContactsContract.Contacts.CONTENT_URI);
            startActivity(intent);
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
        SearchManager searchManager = (SearchManager) requireContext().getSystemService(SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(Objects.requireNonNull(searchManager)
                .getSearchableInfo(requireActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(requireActivity().getComponentName()));

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

        contactsDataAdapter.sort(sortOrder, sortType);
    }

    @Override
    public void onSwipeFavorite(int position) {
        final Contact contact = contactsDataAdapter.getContact(position);

        contact.toggleFavorite();
        contactsDataAdapter.favoriteItem(position);
        saveContactInDB(contact);

        disableKeyBoardIfNeeded();
    }

    @Override
    public void onSwipeIgnore(int position) {
        final Contact contact = contactsDataAdapter.getContact(position);

        contact.toggleIgnore();
        contactsDataAdapter.ignoreItem(position, hideIgnoredContacts);
        saveContactInDB(contact);

        disableKeyBoardIfNeeded();

        if (contact.isIgnore()) {
            createSnackbar(position, contact);
        }
    }

    private void createSnackbar(int position, Contact contact) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, contact.getName(), Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.RED);

        snackbar.setAction(
                requireContext().getResources().getString(R.string.undo),
                view -> {
                    contact.toggleIgnore();
                    contactsDataAdapter.restoreIgnoredContact(position, hideIgnoredContacts, contact);
                    saveContactInDB(contact);
                });
        snackbar.show();
    }

    private void disableKeyBoardIfNeeded() {
        final FragmentActivity activity = requireActivity();
        final View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null) {
            final InputMethodManager inputManager = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(inputManager).hideSoftInputFromWindow(currentFocus.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void saveContactInDB(Contact contact) {
        final long dbIDInAction = dbContactService.insertContact(contact.getDbId(), contact.getKey(),
                contact.isFavorite(), contact.isIgnore());
        contact.setDbId(dbIDInAction);
    }

    @Override
    protected void updateContacts(List<Contact> contacts) {
        contactsDataAdapter.refreshContacts(contacts);
    }
}
