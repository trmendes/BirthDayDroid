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

import com.tmendes.birthdaydroid.Contact;
import com.tmendes.birthdaydroid.DBContact;
import com.tmendes.birthdaydroid.contact.DateConverter;
import com.tmendes.birthdaydroid.helpers.DBHelper;
import com.tmendes.birthdaydroid.helpers.PermissionHelper;
import com.tmendes.birthdaydroid.zodiac.Zodiac;
import com.tmendes.birthdaydroid.zodiac.ZodiacCalculator;
import com.tmendes.birthdaydroid.zodiac.ZodiacResourceHelper;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BirthdayDataProvider {

    private Context ctx;
    private PermissionHelper permissionHelper;
    private SharedPreferences prefs;

    private final String LOG_TAG = "BDD_DATA_PROVIDER";
    private static BirthdayDataProvider instance;
    private static StatisticsProvider statistics;

    private final ArrayList<Contact> contacts;
    private final ArrayList<Contact> contactsToCelebrate;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final ArrayList<Contact> contactsFailureOnParser;

    private BirthdayDataProvider() {
        contacts = new ArrayList<>();
        contactsToCelebrate = new ArrayList<>();
        contactsFailureOnParser = new ArrayList<>();
        statistics = new StatisticsProvider();
    }

    public static BirthdayDataProvider getInstance() {
        if (instance == null) {
            instance = new BirthdayDataProvider();
        }
        return instance;
    }

    public void init(Context ctx, PermissionHelper permissionHelper) {
        this.ctx = ctx;
        this.permissionHelper = permissionHelper;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    private void resetListsAndMaps() {
        contacts.clear();
        contactsFailureOnParser.clear();
        contactsToCelebrate.clear();
        statistics.reset();
    }

    private Contact parseNewContact(String key, String name, String photoURI, String date,
                                    boolean customTypeLabel, String eventTypeLabel,
                                    boolean ignored, boolean favorite, ZodiacCalculator zodiacCalculator,
                                    ZodiacResourceHelper zodiacResourceHelper) {
        Contact contact = new Contact(key, name, photoURI, customTypeLabel, eventTypeLabel);

        if (setBasicContactBirthInfo(contact, date)) {
            @Zodiac final int zodiac = zodiacCalculator.calculateZodiac(contact.getBornOn());
            contact.setZodiacName(zodiacResourceHelper.getZodiacName(zodiac));
            contact.setZodiacSymbol(zodiacResourceHelper.getZodiacSymbol(zodiac));
            contact.setZodiacElementName(zodiacResourceHelper.getZodiacElementName(zodiac));
            contact.setZodiacElementSymbol(zodiacResourceHelper.getZodiacElementSymbol(zodiac));

            if (favorite) {
                contact.setFavorite();
            }
            if (ignored) {
                contact.setIgnore();
            }
            return contact;
        }
        return null;
    }

    public void refreshData(boolean notificationListOnly) {
        if (permissionHelper == null || prefs == null) {
            Log.i(LOG_TAG, "You must set a permission helper");
            return;
        }

        if (permissionHelper.checkPermissionPreferences(PermissionHelper.CONTACT_PERMISSION)) {
            Cursor cursor = getCursor();

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

            boolean parseContacts;

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

            DBHelper db = new DBHelper(ctx);
            HashMap<String, DBContact> dbContacs = db.getAllCotacts();
            final ZodiacCalculator zodiacCalculator = new ZodiacCalculator();
            final ZodiacResourceHelper zodiacResourceHelper = new ZodiacResourceHelper(ctx.getResources());

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                int eventType = cursor.getInt(typeColumn);

                if (showBirthdayTypeOnly) {
                    parseContacts = (eventType ==
                            ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY);
                } else {
                    parseContacts = true;
                }

                if (parseContacts) {

                    String keyCID = cursor.getString(keyColumn);
                    String label = cursor.getString(typeLabelColumn);

                    String eventTypeLabel = ContactsContract.CommonDataKinds.Event
                            .getTypeLabel(ctx.getResources(), eventType, label).toString();
                    boolean customTypeLabel = eventType == ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM
                            && !TextUtils.isEmpty(label);

                    boolean ignoreContact = false;
                    boolean favoriteContact = false;
                    int contactDBId = -1;

                    DBContact dbContact;

                    if ((dbContact = dbContacs.remove(keyCID)) != null) {
                        ignoreContact = dbContact.isIgnore();
                        favoriteContact = dbContact.isFavorite();
                        contactDBId = dbContact.getId();
                    }

                    if (hideIgnoredContacts && ignoreContact) {
                        continue;
                    }

                    Contact contact = parseNewContact(keyCID,
                            cursor.getString(nameColumn),
                            cursor.getString(photoColumn),
                            cursor.getString(dateColumn),
                            customTypeLabel,
                            eventTypeLabel,
                            ignoreContact,
                            favoriteContact,
                            zodiacCalculator,
                            zodiacResourceHelper);

                    if (contact != null) {
                        contact.setDbID(contactDBId);

                        /* Birthday List */
                        if (contact.shallWePartyToday() ||
                                contact.getDaysUntilNextBirthday() == daysInAdvance) {
                            contactsToCelebrate.add(contact);
                        }

                        if (!notificationListOnly) {
                            /* All Contacts */
                            contacts.add(contact);

                            if (!contact.isIgnore()) {
                                try {
                                    if (statistics.getAgeStats().get(contact.getAge()) != null) {
                                        statistics.getAgeStats().put(contact.getAge(),
                                                statistics.getAgeStats().get(contact.getAge()) + 1);
                                    } else {
                                        statistics.getAgeStats().put(contact.getAge(), 1);
                                    }

                                    if (statistics.getSignStats().get(contact.getZodiacName()) != null) {
                                        statistics.getSignStats().put(contact.getZodiacName(),
                                                statistics.getSignStats().get(contact.getZodiacName()) + 1);
                                    } else {
                                        statistics.getSignStats().put(contact.getZodiacName(), 1);
                                    }

                                    if (statistics.getMonthStats().get(contact.getBornOnMonth()) != null) {
                                        statistics.getMonthStats().put(contact.getBornOnMonth(),
                                                statistics.getMonthStats().get(contact.getBornOnMonth()) + 1);
                                    } else {
                                        statistics.getMonthStats().put(contact.getBornOnMonth(), 1);
                                    }

                                    if (statistics.getWeekStats().get(contact.getBornOnDayWeek()) != null) {
                                        statistics.getWeekStats().put(contact.getBornOnDayWeek(),
                                                statistics.getWeekStats().get(contact.getBornOnDayWeek()) + 1);
                                    } else {
                                        statistics.getWeekStats().put(contact.getBornOnDayWeek(), 1);
                                    }
                                } catch (NullPointerException ignored) {
                                }
                            }
                        }
                    }
                }

                db.cleanDb(dbContacs);

                db.close();
            }
            cursor.close();
        }
    }

    private Cursor getCursor() {
        if (ctx == null) {
            Log.i(LOG_TAG, "You must set a permission helper");
            return null;
        }

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
                    Collections.<String>emptySet());

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

    private boolean setBasicContactBirthInfo(Contact contact, String dateString) {
        DateConverter.DateConverterResult converterResult = new DateConverter().convert(dateString);

        if (converterResult.isSuccess()) {
            if (converterResult.getMissingYearInfo()) {
                contact.setMissingYearInfo();
            }
            Calendar calendar = GregorianCalendar.from(
                    converterResult.getDate().atTime(0, 0).atZone(ZoneId.systemDefault())
            );
            contact.setBornOn(calendar);
            return true;
        } else {
            /* If we reach this place is because we are not treating a case */
            this.contactsFailureOnParser.add(contact);
            Log.i(LOG_TAG, dateString + " not supported for " + contact.getName());
            return false;
        }
    }

    public ArrayList<Contact> getAllContacts() {
        return contacts;
    }

    public ArrayList<Contact> getContactsToCelebrate() {
        return contactsToCelebrate;
    }

    public StatisticsProvider getStatistics() {
        return statistics;
    }
}
