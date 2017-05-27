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

package com.tmendes.birthdaydroid;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class BirthDayDataList {

    private static BirthDayDataList birthdays;

    // Contact list
    private ArrayList<ContactData> contactList;

    // Statistics about your frinds
    private final Map<Integer, Integer> ageStats = new TreeMap<>();
    private final Map<String, Integer> signStats = new TreeMap<>();
    private final Map<Integer, Integer> monthStats = new TreeMap<>();
    private final Map<Integer, Integer> weekStats = new TreeMap<>();

    private String notificationCustomMessage;
    private boolean useNotificationCustomMessage;
    private boolean notificationInAdvace, todayBirthdayNotifications;
    private int daysBeforeBirthday;

    private Context ctx;

    public static BirthDayDataList getBirthDayDataList(Context ctx) {
        if (birthdays == null) {
            birthdays = new BirthDayDataList();
            birthdays.ctx = ctx;
            birthdays.updateSettings();
            if (birthdays.contactList == null) {
                birthdays.contactList = new ArrayList<>();
            }
        }
        return birthdays;
    }

    public void updateSettings() {
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(this.ctx);
        notificationCustomMessage = s.getString("custom_notification_message", "");
        useNotificationCustomMessage = s.getBoolean("custom_notification_status", false);
        todayBirthdayNotifications = s.getBoolean("scan_daily", false);
        notificationInAdvace = s.getBoolean("scan_in_advance", false);
        daysBeforeBirthday = Integer.valueOf(s.getString("scan_in_advance_interval", "1"));
    }

    private Cursor getCursor() {
        ContentResolver r = this.ctx.getContentResolver();

        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.CommonDataKinds.Event.START_DATE,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
        };

        String selection = ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.CommonDataKinds.Event.TYPE + "=?";

        String[] args = new String[]{
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
                Integer.toString(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
        };

        return r.query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                selection,
                args,
                null
        );
    }

    public boolean isThereAnyBirthDayToday() {
        boolean anyBirthDayToday = false;
        this.updateSettings();

        if (this.contactList.size() == 0) {
            anyBirthDayToday = scanBirthDaysNowFromCursor();
        } else {
            anyBirthDayToday = scanBirthDaysNowFromList();
        }

        return anyBirthDayToday;
    }

    private boolean scanBirthDaysNowFromList() {
        boolean anyNotification = false;

        for (ContactData person : this.contactList) {
            /* Today notifications */
            if (person.isThereAPartyToday()) {
                if (todayBirthdayNotifications) {
                    try {
                        MessageNotification.notify(this.ctx, person, notificationCustomMessage, useNotificationCustomMessage, person.getDaysUntilNextBirthDay());
                        anyNotification = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            /* In advance notifications */
            else if (notificationInAdvace && (person.getDaysUntilNextBirthDay() <= daysBeforeBirthday)) {
                try {
                    MessageNotification.notify(this.ctx, person, notificationCustomMessage, useNotificationCustomMessage, person.getDaysUntilNextBirthDay());
                    anyNotification = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return anyNotification;
    }

    private boolean scanBirthDaysNowFromCursor() {
        boolean anyNotification = false;
        Cursor c = getCursor();

        if (!c.moveToFirst()) {
            c.close();
            return false;
        }

        final int keyColumn = c.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
        final int dateColumn = c.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
        final int nameColumn = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        final int photoColumn = c.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI);

        ContactData contact = new ContactData(this.ctx);

        do {
            String name = c.getString(nameColumn);
            String date = c.getString(dateColumn);
            String sKeyID = c.getString(keyColumn);
            String photoURI = c.getString(photoColumn);

            contact.parseDate(date);
            contact.setName(name);
            contact.setKeyID(sKeyID);
            contact.setPhotoURI(photoURI);

            /* Today notifications */
            if (contact.isThereAPartyToday()) {
                if (todayBirthdayNotifications) {
                    try {
                        MessageNotification.notify(this.ctx, contact, notificationCustomMessage, useNotificationCustomMessage, contact.getDaysUntilNextBirthDay());
                        anyNotification = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            /* In advance notifications */
            else if (notificationInAdvace && (contact.getDaysUntilNextBirthDay() <= daysBeforeBirthday)) {
                try {
                    MessageNotification.notify(this.ctx, contact, notificationCustomMessage, useNotificationCustomMessage, contact.getDaysUntilNextBirthDay());
                    anyNotification = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } while (c.moveToNext());

        c.close();
        return anyNotification;
    }

    private void clearLists() {
        if (contactList != null) {
            contactList.clear();
            ageStats.clear();
            signStats.clear();
            monthStats.clear();
            weekStats.clear();
        }
    }

    public void refreshList() {

        clearLists();

        Cursor c = getCursor();

        if (!c.moveToFirst()) {
            c.close();
            return;
        }

        final int keyColumn = c.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
        final int dateColumn = c.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
        final int nameColumn = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        final int photoColumn = c.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI);

        do {
            String name = c.getString(nameColumn);
            String date = c.getString(dateColumn);
            String sKeyID = c.getString(keyColumn);
            String photoURI = c.getString(photoColumn);

            ContactData contact = new ContactData(this.ctx, sKeyID, name, date, photoURI);

            if (contact.hasYear()) {

                String sign = contact.getSign();
                int age = contact.getAge();
                int month = contact.getMonth();
                int bWeek = contact.getBirthDayWeek();
                int ageCnt = 1, signCnt = 1, monthCnt = 1, weekCnt = 1;

                if (ageStats.get(age) != null) {
                    ageCnt = ageStats.get(age) + 1;
                }
                if (signStats.get(sign) != null) {
                    signCnt = signStats.get(sign) + 1;
                }
                if (monthStats.get(month) != null) {
                    monthCnt = monthStats.get(month) + 1;
                }
                if (weekStats.get(bWeek) != null) {
                    weekCnt = weekStats.get(bWeek) + 1;
                }

                ageStats.put(age, ageCnt);
                signStats.put(sign, signCnt);
                monthStats.put(month, monthCnt);
                weekStats.put(bWeek, weekCnt);
            }

            this.contactList.add(contact);

        } while (c.moveToNext());

        c.close();
    }

    public Map<Integer, Integer> getAgeStats() {
        return ageStats;
    }

    public Map<String, Integer> getSignStats() {
        return signStats;
    }

    public Map<Integer, Integer> getMonthStats() {
        return monthStats;
    }

    public Map<Integer, Integer> getWeekStats() {
        return weekStats;
    }

    public ArrayList<ContactData> getList() {
        return contactList;
    }

}
