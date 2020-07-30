package com.tmendes.birthdaydroid.views.contactlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.zodiac.ZodiacResourceHelper;

public class ContactsDataAdapter extends SortAndFilterRecyclerViewAdapter<Contact, ContactViewHolder> {

    private final SharedPreferences prefs;
    private final ZodiacResourceHelper zodiacResourceHelper;

    public ContactsDataAdapter(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        zodiacResourceHelper = new ZodiacResourceHelper(context);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);
        return new ContactViewHolder(itemView, zodiacResourceHelper);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        final boolean hideZodiac = prefs.getBoolean("hide_zodiac", false);
        final boolean showCurrentAge = prefs.getBoolean("show_current_age", false);

        final Contact contact = getItem(position);

        holder.setupContact(contact, showCurrentAge, hideZodiac);
    }
}
