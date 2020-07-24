package com.tmendes.birthdaydroid;

import android.content.SharedPreferences;

import com.tmendes.birthdaydroid.contact.ContactsViewModel;

public class ReloadContactsPreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final ContactsViewModel contactsViewModel;

    public ReloadContactsPreferenceChangeListener(ContactsViewModel contactsViewModel) {
        this.contactsViewModel = contactsViewModel;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        switch (key) {
            case "hide_ignored_contacts":
            case "show_birthday_type_only":
            case "selected_accounts_enabled":
            case "selected_accounts":
                contactsViewModel.reloadContacts();
                break;
        }
    }
}
