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

import com.tmendes.birthdaydroid.adapters.ContactsDataAdapter;
import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.controllers.SwipeController;
import com.tmendes.birthdaydroid.controllers.SwipeControllerActions;
import com.tmendes.birthdaydroid.providers.BirthdayDataProvider;

import java.util.Objects;


public class ContactListFragment extends Fragment {

    private BirthdayDataProvider bddDataProviver;
    private EditText inputSearch;
    public SwipeController swipeController;
    public ContactsDataAdapter contactsDataAdapter;
    private boolean hideIgnoredContacts;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_contact_list,
                container, false);

        PreferenceManager.setDefaultValues(getContext(), R.xml.preferences, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        hideIgnoredContacts = prefs.getBoolean("hide_ignored_contacts", false);

        bddDataProviver = BirthdayDataProvider.getInstance();

        contactsDataAdapter = new ContactsDataAdapter(getContext(),
                bddDataProviver.getAllContacts());

        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(contactsDataAdapter);

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                bddDataProviver.getAllContacts().get(position).setIgnore();
                if (hideIgnoredContacts) {
                    bddDataProviver.getAllContacts().remove(position);
                }
                contactsDataAdapter.notifyItemRangeChanged(position,
                        contactsDataAdapter.getItemCount());
            }
            @Override
            public void onLeftClicked(int position) {
                bddDataProviver.getAllContacts().get(position).setFavorite();
                contactsDataAdapter.notifyItemRangeChanged(position,
                        contactsDataAdapter.getItemCount());
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

        inputSearch = v.findViewById(R.id.inputSearch);

        getActivity().getWindow()
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
                    InputMethodManager inputManager = (InputMethodManager) getContext().
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
}
