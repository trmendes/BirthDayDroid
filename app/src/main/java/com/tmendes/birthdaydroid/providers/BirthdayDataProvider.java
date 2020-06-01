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

package com.tmendes.birthdaydroid.providers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.tmendes.birthdaydroid.contact.DBContact;
import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.contact.ContactBuilder;
import com.tmendes.birthdaydroid.contact.ContactBuilderException;
import com.tmendes.birthdaydroid.date.DateConverter;
import com.tmendes.birthdaydroid.contact.ContactDBHelper;
import com.tmendes.birthdaydroid.zodiac.ZodiacCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BirthdayDataProvider {
    private final String LOG_TAG = "BDD_DATA_PROVIDER";

    private static BirthdayDataProvider instance;
    private final ArrayList<Contact> contacts;
    private final ArrayList<Contact> contactsToCelebrate;

    private BirthdayDataProvider() {
        contacts = new ArrayList<>();
        contactsToCelebrate = new ArrayList<>();
    }

    public static synchronized BirthdayDataProvider getInstance() {
        if (instance == null) {
            instance = new BirthdayDataProvider();
        }
        return instance;
    }

    private void resetListsAndMaps() {
        contacts.clear();
        contactsToCelebrate.clear();
    }

    public void refreshData(Context ctx, boolean notificationListOnly) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        if (prefs == null) {
            Log.i(LOG_TAG, "You must set a permission helper");
            return;
        }

        if (new PermissionHelper().checkReadContactsPermission(ctx)) {
            Cursor cursor = getCursor(ctx);

            resetListsAndMaps();

            if (cursor == null) {
                return;
            }

            if (!Objects.requireNonNull(cursor).moveToFirst()) {
                cursor.close();
                return;
            }

            boolean hideIgnoredContacts = prefs.getBoolean("hide_ignored_contacts", false);
            boolean showBirthdayTypeOnly = prefs.getBoolean("show_birthday_type_only", false);
            boolean notificationInAdvance = prefs.getBoolean("scan_in_advance", false);
            int daysInAdvance = 0;
            if (notificationInAdvance) {
                daysInAdvance = prefs.getInt("days_in_advance_interval", 0);
            }

            final int keyColumn = cursor.getColumnIndex(
                    ContactsContract.Contacts.LOOKUP_KEY);
            final int dateColumn = cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Event.START_DATE);
            final int nameColumn = cursor.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME);
            final int photoColumn = cursor.getColumnIndex(
                    ContactsContract.Contacts.PHOTO_THUMBNAIL_URI);
            final int typeColumn = cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Event.TYPE);
            final int typeLabelColumn = cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Event.LABEL);

            final ContactDBHelper db = new ContactDBHelper(ctx);
            final HashMap<String, DBContact> dbContacts = db.getAllContacts();

            final ZodiacCalculator zodiacCalculator = new ZodiacCalculator();
            final DateConverter dateConverter = new DateConverter();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                final int eventType = cursor.getInt(typeColumn);

                final boolean parseContacts;
                if (showBirthdayTypeOnly) {
                    parseContacts = (eventType ==
                            ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY);
                } else {
                    parseContacts = true;
                }

                if (parseContacts) {
                    String keyCID = cursor.getString(keyColumn);

                    boolean ignoreContact = false;
                    boolean favoriteContact = false;
                    long contactDBId = -1;

                    DBContact dbContact;

                    if ((dbContact = dbContacts.remove(keyCID)) != null) {
                        ignoreContact = dbContact.isIgnore();
                        favoriteContact = dbContact.isFavorite();
                        contactDBId = dbContact.getId();
                    }

                    if (hideIgnoredContacts && ignoreContact) {
                        continue;
                    }

                    final String label = cursor.getString(typeLabelColumn);
                    final String eventTypeLabel = ContactsContract.CommonDataKinds.Event
                            .getTypeLabel(ctx.getResources(), eventType, label).toString();
                    final boolean customTypeLabel = eventType == ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM
                            && !TextUtils.isEmpty(label);

                    try {
                        Contact contact = new ContactBuilder(zodiacCalculator, dateConverter)
                                .setDbId(contactDBId)
                                .setKey(keyCID)
                                .setName(cursor.getString(nameColumn))
                                .setPhotoUri(cursor.getString(photoColumn))
                                .setBirthdayString(cursor.getString(dateColumn))
                                .setCustomEventTypeLabel(customTypeLabel)
                                .setEventTypeLabel(eventTypeLabel)
                                .setIgnore(ignoreContact)
                                .setFavorite(favoriteContact)
                                .build();

                        /* Birthday List */
                        if (contact.hasBirthDayToday() ||
                                contact.getDaysUntilNextBirthday() == daysInAdvance) {
                            contactsToCelebrate.add(contact);
                        }

                        if (!notificationListOnly) {
                            /* All Contacts */
                            contacts.add(contact);
                        }
                    } catch (ContactBuilderException e) {
                        Log.i(LOG_TAG, "Unable to build contact", e);
                    }
                }

                db.cleanDb(dbContacts);

                db.close();
            }
            cursor.close();
        }
    }

    private Cursor getCursor(Context ctx) {
        if (ctx == null) {
            Log.i(LOG_TAG, "You must set a permission helper");
            return null;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        ContentResolver contentResolver = ctx.getContentResolver();

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

        return contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                selectionBuilder.toString(),
                argsList.toArray(new String[0]),
                null
        );
    }

    public ArrayList<Contact> getAllContacts() {
        return contacts;
    }

    public ArrayList<Contact> getContactsToCelebrate() {
        return contactsToCelebrate;
    }
}
