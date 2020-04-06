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
import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.helpers.DBHelper;
import com.tmendes.birthdaydroid.helpers.PermissionHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class BirthdayDataProvider {

    private Context ctx;
    private PermissionHelper permissionHelper;
    private SharedPreferences prefs;

    private final String LOG_TAG = "BDD_DATA_PROVIDER";
    private static BirthdayDataProvider instance;
    private static StatisticsProvider statistics;

    private final List<String> dataFormatKnownPatterns = Arrays.asList(
            "yyyy-M-dd",
            "yyyy-M-dd hh:mm:ss.SSS",
            "dd-M-y",
            "dd-M-y hh:mm:ss.SSS",
            "--M-dd",
            "--M-dd hh:mm:ss.SSS",
            "dd-M-- hh:mm:ss.SSS",
            "dd-M--"
    );

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
                                    boolean ignored, boolean favorite) {
        Contact contact = new Contact(key, name, photoURI, customTypeLabel, eventTypeLabel);

        if (setBasicContactBirthInfo(contact, date)) {
            setContactZodiac(contact);
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

    @SuppressWarnings("ConstantConditions")
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
                            favoriteContact);

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

                                    if (statistics.getSignStats().get(contact.getZodiac()) != null) {
                                        statistics.getSignStats().put(contact.getZodiac(),
                                                statistics.getSignStats().get(contact.getZodiac()) + 1);
                                    } else {
                                        statistics.getSignStats().put(contact.getZodiac(), 1);
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
        boolean parseSuccess = false;

        for (String pattern : dataFormatKnownPatterns) {
            Date bornOnDate;
            try {
                bornOnDate = new SimpleDateFormat(pattern, Locale.getDefault()).parse(dateString);

                if (bornOnDate != null) {
                    Calendar bornOn = new GregorianCalendar();
                    bornOn.setTime(bornOnDate);

                    if (!pattern.contains("y")) {
                        contact.setMissinYearInfo();
                        bornOn.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                    }

                    contact.setBornOn(bornOn);
                    parseSuccess = true;

                    break;
                }
            } catch (ParseException ignored) {
            }
        }

        if (!parseSuccess) {
            /* If we reach this place is because we are not treating a case */
            this.contactsFailureOnParser.add(contact);
            Log.i(LOG_TAG, dateString + " not supported for " + contact.getName());
        }
        return parseSuccess;
    }

    private void setContactZodiac(Contact contact) {
        String zodiacSymbol = "";
        String zodiacElementSymbol = "";
        String zodiac = "";
        String zodiacElement = "";

        if (ctx != null) {
            int day = contact.getBornOnDay();

            switch (contact.getBornOnMonth()) {
                case Calendar.JANUARY:
                    if ((day >= 21) && (day <= 31)) {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_aquarius_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_air_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_aquarius);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_air);
                    } else {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_capricorn_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_earth_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_capricorn);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_earth);
                    }
                    break;
                case Calendar.FEBRUARY:
                    if ((day >= 20) && (day <= 29)) {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_pisces_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_water_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_pisces);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_water);
                    } else {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_aquarius_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_air_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_aquarius);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_air);
                    }
                    break;
                case Calendar.MARCH:
                    if ((day >= 21) && (day <= 31)) {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_aries_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_fire_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_aries);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_fire);
                    } else {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_pisces_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_water_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_pisces);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_water);
                    }
                    break;
                case Calendar.APRIL:
                    if ((day >= 20) && (day <= 30)) {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_taurus_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_earth_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_taurus);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_earth);
                    } else {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_aries_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_fire_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_aries);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_fire);
                    }
                    break;
                case Calendar.MAY:
                    if ((day >= 20) && (day <= 31)) {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_gemini_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_air_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_gemini);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_air);
                    } else {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_taurus_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_earth_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_taurus);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_earth);
                    }
                    break;
                case Calendar.JUNE:
                    if ((day >= 21) && (day <= 30)) {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_cancer_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_water_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_cancer);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_water);
                    } else {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_gemini_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_air_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_gemini);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_air);
                    }
                    break;
                case Calendar.JULY:
                    if ((day >= 23) && (day <= 31)) {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_leo_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_fire_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_leo);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_fire);
                    } else {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_cancer_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_water_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_cancer);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_water);
                    }
                    break;
                case Calendar.AUGUST:
                    if ((day >= 22) && (day <= 31)) {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_virgo_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_earth_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_virgo);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_earth);
                    } else {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_leo_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_fire_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_leo);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_fire);
                    }
                    break;
                case Calendar.SEPTEMBER:
                    if ((day >= 23) && (day <= 30)) {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_libra_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_air_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_libra);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_air);
                    } else {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_virgo_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_earth_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_virgo);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_earth);
                    }
                    break;
                case Calendar.OCTOBER:
                    if ((day >= 23) && (day <= 31)) {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_scorpio_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_water_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_scorpio);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_water);
                    } else {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_libra_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_air_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_libra);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_air);
                    }
                    break;
                case Calendar.NOVEMBER:
                    if ((day >= 22) && (day <= 30)) {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_sagittarius_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_fire_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_sagittarius);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_fire);
                    } else {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_scorpio_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_water_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_scorpio);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_water);
                    }
                    break;
                case Calendar.DECEMBER:
                    if ((day >= 22) && (day <= 31)) {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_capricorn_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_earth_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_capricorn);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_earth);
                    } else {
                        zodiacSymbol = ctx.getResources().getString(R.string.sign_sagittarius_symbol);
                        zodiacElementSymbol = ctx.getResources().getString(R.string.sign_element_fire_symbol);
                        zodiac = ctx.getResources().getString(R.string.sign_sagittarius);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_fire);
                    }
                    break;
            }
        }

        contact.setZodiacSymbol(zodiacSymbol);
        contact.setZodiacElementSymbol(zodiacElementSymbol);
        contact.setZodiac(zodiac);
        contact.setZodiacElement(zodiacElement);
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
