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
import android.util.Log;

import com.tmendes.birthdaydroid.Contact;
import com.tmendes.birthdaydroid.R;;
import com.tmendes.birthdaydroid.helpers.PermissionHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

public class BirthdayDataProvider {

    private Context ctx;
    private PermissionHelper permissionHelper;

    private static final long DAY = 60 * 60 * 1000 * 24;
    private final int YEAR_LEN = 365;
    private final int LEAP_YEAR_LEN = 366;
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

    private ArrayList<Contact> contacts;
    private ArrayList<Contact> contactsToCelebrate;
    private ArrayList<Contact> contactsFailureOnParser;

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

    public void setPermissionHelper(Context ctx, PermissionHelper permissionHelper) {
        this.ctx = ctx;
        this.permissionHelper = permissionHelper;
    }

    private void resetListsAndMaps() {
        contacts.clear();
        contactsFailureOnParser.clear();
        contactsToCelebrate.clear();
        statistics.reset();
    }


    public Contact parseNewContact(String key, String name, String photoURI, String date,
                                   int eventType, String eventTypeLabel) {
        Contact contact = new Contact(key, name, photoURI, date, eventType, eventTypeLabel);
        setBasicContactBirthInfo(contact, date);
        setContactZodiac(contact);
        setContactPartyMode(contact);
        return contact;
    }

    public void refreshData(boolean notificationListOnly) {
        if (permissionHelper == null) {
            Log.i(LOG_TAG, "You must set a permission helper");
            return;
        }

        if (permissionHelper.checkPermissionPreferences(PermissionHelper.CONTACT_PERMISSION)) {
            Cursor cursor = getCursor();

            if (cursor == null) {
                return;
            }

            if (!Objects.requireNonNull(cursor).moveToFirst()) {
                cursor.close();
                return;
            }

            resetListsAndMaps();

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

            while (cursor.moveToNext()) {

                int eventType = cursor.getInt(typeColumn);

                String eventTypeLabel;

                if(eventType == ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
                {
                    eventTypeLabel = cursor.getString(typeColumn);
                }
                else
                {
                    CharSequence seq = ContactsContract.CommonDataKinds.Event.
                            getTypeLabel(ctx.getResources(), eventType, ctx.getResources().
                                    getString(R.string.type_birthday));
                    eventTypeLabel = seq.toString();
                }

                eventTypeLabel = eventTypeLabel.toLowerCase();


                Contact contact = parseNewContact(cursor.getString(keyColumn),
                        cursor.getString(nameColumn),
                        cursor.getString(photoColumn),
                        cursor.getString(dateColumn),
                        eventType,
                        eventTypeLabel);

                /* Birthday List */
                if (contact.shallWePartyToday()) {
                    contactsToCelebrate.add(contact);
                }

                if (!notificationListOnly) {
                    /* All Contacts */
                    contacts.add(contact);

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
                }
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
                ContactsContract.CommonDataKinds.Event.TYPE
        };

        String selection = ContactsContract.Data.MIMETYPE
                + "=? AND ("
                + ContactsContract.CommonDataKinds.Event.TYPE // Birthday
                + "=? OR "
                + ContactsContract.CommonDataKinds.Event.TYPE // Annniversary
                + "=? OR "
                + ContactsContract.CommonDataKinds.Event.TYPE // Other
                + "=? OR "
                + ContactsContract.CommonDataKinds.Event.TYPE // Custom
                + "=?)";

        String[] args = new String[]{
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
                Integer.toString(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY),
                Integer.toString(ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY),
                Integer.toString(ContactsContract.CommonDataKinds.Event.TYPE_OTHER),
                Integer.toString(ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM)
        };

        return contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                selection,
                args,
                null
        );
    }

    private void setBasicContactBirthInfo(Contact contact, String dateString) {
        boolean failOnParse = true;

        for (String pattern : dataFormatKnownPatterns) {
            Date bornOnDate = null;
            try {
                bornOnDate = new SimpleDateFormat(pattern).parse(dateString);
            } catch (ParseException e) {
                bornOnDate = null;
            }

            if (bornOnDate != null) {
                boolean contactHasYearSet = pattern.contains("y");
                boolean yetToBorn = false;

                Calendar now = Calendar.getInstance();
                int nowYear = now.get(Calendar.YEAR);
                boolean isNowLeapYear = new GregorianCalendar().isLeapYear(nowYear);

                Calendar bornOn = new GregorianCalendar();
                bornOn.setTime(bornOnDate);

                if (!contactHasYearSet) {
                    bornOn.set(Calendar.YEAR, nowYear);
                }

                Calendar nextBirthDay = (Calendar) bornOn.clone();
                nextBirthDay.set(Calendar.YEAR, nowYear);

                long daysOld = (now.getTimeInMillis() - bornOn.getTimeInMillis()) / DAY;

                boolean lessThanAYearOld = false;
                int daysUntilNextBirthDay = 0;
                int age = 0;

                if (daysOld >= 0) {
                    if (isNowLeapYear) {
                        lessThanAYearOld = daysOld < this.LEAP_YEAR_LEN;
                    } else {
                        lessThanAYearOld = daysOld < this.YEAR_LEN;
                    }

                    double msUntilNextBirthDay = 0;

                    if (!lessThanAYearOld) {
                        age = nowYear - bornOn.get(Calendar.YEAR);
                    }

                    /* Next birthday will be next year only */
                    boolean lateBirthday = nextBirthDay.getTimeInMillis()
                            <= now.getTimeInMillis();

                    if (lateBirthday) {
                        nextBirthDay.set(Calendar.YEAR, nowYear + 1);
                    } else {
                        if (!lessThanAYearOld) {
                            --age;
                        }
                    }

                    msUntilNextBirthDay = nextBirthDay.getTimeInMillis()
                            - now.getTimeInMillis();


                    daysUntilNextBirthDay = (int) (msUntilNextBirthDay / DAY) + 1;
                } else {
                    /* Born in the future */
                    daysOld = 0;
                }

                if ((!contactHasYearSet) || (yetToBorn)){
                    age = 0;
                    daysOld = 0;
                }

                contact.setYearSettled(contactHasYearSet);
                contact.setBornOn(bornOn);
                contact.setNextBirthday(nextBirthDay);
                contact.setDaysUntilNextBirthday(daysUntilNextBirthDay);
                contact.setAge(age);
                contact.setDaysOld(((int) daysOld));
                contact.setHeSheNotEvenOneYearOld(lessThanAYearOld);

                failOnParse = false;

                break;
            }
        }

        if (failOnParse) {
            /* If we reach this place is because we are not treating a case */
            this.contactsFailureOnParser.add(contact);
            Log.i(LOG_TAG, dateString + " not supported for " + contact.getName());
        }
    }

    private void setContactZodiac(Contact contact) {
        String zodiac = "";
        String zodiacElement =  "";

        if (ctx != null) {
            int day = contact.getBornOnDay();

            switch (contact.getBornOnMonth()) {
                case Calendar.JANUARY:
                    if ((day >= 21) && (day <= 31)) {
                        zodiac = ctx.getResources().getString(R.string.sign_aquarius);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_air);
                    } else {
                        zodiac = ctx.getResources().getString(R.string.sign_capricorn);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_earth);
                    }
                    break;
                case Calendar.FEBRUARY:
                    if ((day >= 20) && (day <= 29)) {
                        zodiac = ctx.getResources().getString(R.string.sign_pisces);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_water);
                    } else {
                        zodiac = ctx.getResources().getString(R.string.sign_aquarius);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_air);
                    }
                    break;
                case Calendar.MARCH:
                    if ((day >= 21) && (day <= 31)) {
                        zodiac = ctx.getResources().getString(R.string.sign_aries);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_fire);
                    } else {
                        zodiac = ctx.getResources().getString(R.string.sign_pisces);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_water);
                    }
                    break;
                case Calendar.APRIL:
                    if ((day >= 20) && (day <= 30)) {
                        zodiac = ctx.getResources().getString(R.string.sign_taurus);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_earth);
                    } else {
                        zodiac = ctx.getResources().getString(R.string.sign_aries);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_fire);
                    }
                    break;
                case Calendar.MAY:
                    if ((day >= 20) && (day <= 31)) {
                        zodiac = ctx.getResources().getString(R.string.sign_gemini);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_air);
                    } else {
                        zodiac = ctx.getResources().getString(R.string.sign_taurus);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_earth);
                    }
                    break;
                case Calendar.JUNE:
                    if ((day >= 21) && (day <= 30)) {
                        zodiac = ctx.getResources().getString(R.string.sign_cancer);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_water);
                    } else {
                        zodiac = ctx.getResources().getString(R.string.sign_gemini);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_air);
                    }
                    break;
                case Calendar.JULY:
                    if ((day >= 23) && (day <= 31)) {
                        zodiac = ctx.getResources().getString(R.string.sign_leo);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_fire);
                    } else {
                        zodiac = ctx.getResources().getString(R.string.sign_cancer);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_water);
                    }
                    break;
                case Calendar.AUGUST:
                    if ((day >= 22) && (day <= 31)) {
                        zodiac = ctx.getResources().getString(R.string.sign_virgo);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_earth);
                    } else {
                        zodiac = ctx.getResources().getString(R.string.sign_leo);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_fire);
                    }
                    break;
                case Calendar.SEPTEMBER:
                    if ((day >= 23) && (day <= 30)) {
                        zodiac = ctx.getResources().getString(R.string.sign_libra);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_air);
                    } else {
                        zodiac = ctx.getResources().getString(R.string.sign_virgo);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_earth);
                    }
                    break;
                case Calendar.OCTOBER:
                    if ((day >= 23) && (day <= 31)) {
                        zodiac = ctx.getResources().getString(R.string.sign_scorpio);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_water);
                    } else {
                        zodiac = ctx.getResources().getString(R.string.sign_libra);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_air);
                    }
                    break;
                case Calendar.NOVEMBER:
                    if ((day >= 22) && (day <= 30)) {
                        zodiac = ctx.getResources().getString(R.string.sign_sagittarius);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_fire);
                    } else {
                        zodiac = ctx.getResources().getString(R.string.sign_scorpio);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_water);
                    }
                    break;
                case Calendar.DECEMBER:
                    if ((day >= 22) && (day <= 31)) {
                        zodiac = ctx.getResources().getString(R.string.sign_capricorn);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_earth);
                    } else {
                        zodiac = ctx.getResources().getString(R.string.sign_sagittarius);
                        zodiacElement = ctx.getResources().getString(R.string.sign_element_fire);
                    }
                    break;
            }
        }

        contact.setZodiac(zodiac);
        contact.setZodiacElement(zodiacElement);
    }

    private void setContactPartyMode(Contact contact) {

        Calendar now = Calendar.getInstance();
        int nowDay = now.get(Calendar.DAY_OF_MONTH);
        int nowMonth = now.get(Calendar.MONTH);
        int nowYear = now.get(Calendar.YEAR);
        int bornOnDay = contact.getBornOnDay();
        int bornOnMonth = contact.getBornOnMonth();
        int daysUntilNextBirthday = contact.getDaysUntilNextBirthday();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        boolean showNotificationInAdvace = prefs.getBoolean("scan_in_advance", false);
        int daysInAdvance = prefs.getInt("days_in_advance_interval", 1);

        boolean isLeapYear = new GregorianCalendar().isLeapYear(nowYear);
        boolean letsCelebrate = false;

        if (showNotificationInAdvace &&
                daysUntilNextBirthday > 0 &&
                daysUntilNextBirthday <= daysInAdvance) {
            letsCelebrate = true;
        }

        if (!letsCelebrate) {
            if (!isLeapYear &&
                    (bornOnDay == 29 && bornOnMonth == Calendar.FEBRUARY)
                    && (nowDay == 1 && nowMonth == Calendar.MARCH)) {
                letsCelebrate = true;
            } else {
                if (nowDay == bornOnDay && nowMonth == bornOnMonth) {
                    letsCelebrate = true;
                }
            }
        }

        contact.setshallWePartyToday(letsCelebrate);
    }

    public ArrayList<Contact> getAllContacts() {
        return contacts;
    }

    public ArrayList<Contact> getContactsToCelebrate() {
        return contactsToCelebrate;
    }

    public ArrayList<Contact> getContactsMissingData() {
        return contactsFailureOnParser;
    }

    public StatisticsProvider getStatistics() {
        return statistics;
    }
}
