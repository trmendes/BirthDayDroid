package com.tmendes.birthdaydroid.views.contactlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.contact.Contact;

public abstract class AbstractContactViewHolder extends RecyclerView.ViewHolder {
    private final RelativeLayout ignoreContactLayout;
    private final RelativeLayout favoriteContactLayout;
    private final View itemLayout;

    private final Context context;
    private final SharedPreferences prefs;
    private Contact contact;

    public AbstractContactViewHolder(View view) {
        super(view);
        this.context = view.getContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        view.setOnClickListener(v ->
                openContactReadView()
        );

        view.setOnLongClickListener(v -> {
            openContactEditView();
            return true;
        });

        itemLayout = view.findViewById(R.id.view_item);
        ignoreContactLayout = view.findViewById(R.id.view_background_ignore);
        favoriteContactLayout = view.findViewById(R.id.view_background_favorite);
    }

    public void setupNewContact(Contact contact) {
        this.contact = contact;
        this.ignoreContactLayout.setVisibility(View.INVISIBLE);
        this.favoriteContactLayout.setVisibility(View.INVISIBLE);
    }

    private void openContactReadView() {
        openContactView(Intent.ACTION_VIEW);
    }

    private void openContactEditView() {
        openContactView(Intent.ACTION_EDIT);
    }

    private void openContactView(String actionView) {
        Intent i = new Intent(actionView);
        i.setData(Uri.parse(
                ContactsContract.Contacts.CONTENT_LOOKUP_URI
                        + "/" + contact.getKey()));
        this.context.startActivity(i);
    }

    public void setItemLayout() {
        favoriteContactLayout.setVisibility(View.INVISIBLE);
        ignoreContactLayout.setVisibility(View.INVISIBLE);
    }

    public void setSwipeIgnoreLayout() {
        favoriteContactLayout.setVisibility(View.INVISIBLE);
        ignoreContactLayout.setVisibility(View.VISIBLE);
    }

    public void setSwipeFavoriteLayout() {
        favoriteContactLayout.setVisibility(View.VISIBLE);
        ignoreContactLayout.setVisibility(View.INVISIBLE);
    }

    public View getItemLayout() {
        return itemLayout;
    }

    protected Context getContext() {
        return context;
    }

    protected SharedPreferences getPrefs() {
        return prefs;
    }

    protected Contact getContact() {
        return contact;
    }
}
