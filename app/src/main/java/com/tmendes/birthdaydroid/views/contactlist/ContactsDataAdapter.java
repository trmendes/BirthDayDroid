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

public class ContactsDataAdapter extends SortAndFilterRecyclerViewAdapter<Contact, AbstractContactViewHolder> {

    private final SharedPreferences prefs;
    private final ZodiacResourceHelper zodiacResourceHelper;

    public ContactsDataAdapter(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        zodiacResourceHelper = new ZodiacResourceHelper(context);
    }

    @NonNull
    @Override
    public AbstractContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        boolean compactView = prefs.getBoolean("compact_view", false);
        final AbstractContactViewHolder viewHolder;
        if (compactView) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item_compact, parent, false);
            viewHolder = new ContactCompactViewHolder(itemView, zodiacResourceHelper);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);
            viewHolder = new ContactNormalViewHolder(itemView, zodiacResourceHelper);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractContactViewHolder holder, int position) {
        final Contact contact = getItem(position);
        holder.setupNewContact(contact);
    }
}
