package com.tmendes.birthdaydroid.helpers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.appcompat.widget.TintTypedArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AccountHelper {
    public Account[] getAllAccounts(Context ctx) {
        Account[] deviceAccounts = AccountManager.get(ctx).getAccounts();
        List<Account> contentResolverAccounts = getContentResolverAccounts(ctx);

        Set<Account> allAccounts = new HashSet<>(Arrays.asList(deviceAccounts));
        allAccounts.addAll(contentResolverAccounts);

        return allAccounts.toArray(new Account[0]);
    }

    private List<Account> getContentResolverAccounts(Context ctx) {
        Set<Account> sources = new HashSet<>();

        List<Uri> urisArray = Arrays.asList(
                ContactsContract.Groups.CONTENT_URI,
                ContactsContract.Settings.CONTENT_URI,
                ContactsContract.RawContacts.CONTENT_URI
        );

        for (Uri uri : urisArray) {
            fillSourcesFromUri(uri, sources, ctx);
        }

        return new ArrayList<>(sources);
    }

    private void fillSourcesFromUri(Uri uri, Set<Account> sources, Context ctx) {
        String[] projection = new String[]{
                ContactsContract.RawContacts.ACCOUNT_NAME,
                ContactsContract.RawContacts.ACCOUNT_TYPE
        };

        String selection = ContactsContract.RawContacts.ACCOUNT_NAME + " IS NOT NULL"
                + " AND " + ContactsContract.RawContacts.ACCOUNT_TYPE + " IS NOT NULL";

        Cursor cursor = null;
        try {
            cursor = ctx.getContentResolver().query(uri, projection, selection, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_NAME));
                    String type = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE));
                    sources.add(new Account(name, type));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }
}
