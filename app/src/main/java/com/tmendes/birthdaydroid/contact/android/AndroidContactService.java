package com.tmendes.birthdaydroid.contact.android;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;

import com.tmendes.birthdaydroid.cursor.CursorIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AndroidContactService {
    private final Context context;

    public AndroidContactService(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context can not be null");
        }
        this.context = context;
    }

    public CursorIterator<AndroidContact> getAndroidContacts() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ContentResolver contentResolver = context.getContentResolver();

        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.CommonDataKinds.Event.START_DATE,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                ContactsContract.CommonDataKinds.Event.TYPE,
                ContactsContract.CommonDataKinds.Event.LABEL
        };

        List<String> argsList = new ArrayList<>();
        StringBuilder selectionBuilder = new StringBuilder();

        selectionBuilder.append(ContactsContract.Data.MIMETYPE).append(" = ? ");
        argsList.add(ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE);

        if (prefs.getBoolean("selected_accounts_enabled", false)) {
            Set<String> selectedAccounts = prefs.getStringSet("selected_accounts",
                    Collections.emptySet());

            selectionBuilder.append(" AND ");
            if (!selectedAccounts.isEmpty()) {
                selectionBuilder.append(ContactsContract.RawContacts.ACCOUNT_NAME);
                selectionBuilder.append(" IN (");
                boolean first = true;
                for (String accountName : selectedAccounts) {
                    if (first) {
                        first = false;
                    } else {
                        selectionBuilder.append(",");
                    }
                    selectionBuilder.append("?");
                    argsList.add(accountName);
                }
                selectionBuilder.append(")");
            } else {
                return null;
            }
        }

        Cursor cursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                selectionBuilder.toString(),
                argsList.toArray(new String[0]),
                null
        );

        return new AndroidContactCursorIterator(cursor);
    }
}
