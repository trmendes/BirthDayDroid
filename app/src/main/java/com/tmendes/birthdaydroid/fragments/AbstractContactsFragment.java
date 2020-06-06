package com.tmendes.birthdaydroid.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.contact.ContactsViewModel;

import java.util.List;

public abstract class AbstractContactsFragment extends Fragment {
    private ContactsViewModel contactsViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contactsViewModel = ViewModelProviders.of(requireActivity()).get(ContactsViewModel.class);
        contactsViewModel.getContacts().observe(this, this::updateContacts);
    }

    public ContactsViewModel getContactsViewModel() {
        return contactsViewModel;
    }

    protected abstract void updateContacts(List<Contact> contacts);
}
