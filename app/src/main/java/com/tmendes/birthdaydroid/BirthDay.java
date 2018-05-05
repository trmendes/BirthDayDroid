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
import android.database.Cursor;
import android.provider.ContactsContract;

import com.tmendes.birthdaydroid.helpers.MessageNotification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class BirthDay {

    private static BirthDay birthdays;

    // Contact list
    private ArrayList<Contact> contactList;

    // Statistics about your frinds
    private final Map<Integer, Integer> ageStats = new TreeMap<>();
    private final Map<String, Integer> signStats = new TreeMap<>();
    private final Map<Integer, Integer> monthStats = new TreeMap<>();
    private final Map<Integer, Integer> weekStats = new TreeMap<>();

    private String notificationCustomMessage;
    private boolean useNotificationCustomMessage;
    private boolean showNotificationInAdvace, showTodayNotifications;
    private int daysBeforeBirthday;

    private Context ctx;

    public static BirthDay getBirthDayList(Context ctx) {
        if (birthdays == null) {
            birthdays = new BirthDay();
            birthdays.ctx = ctx;
            if (birthdays.contactList == null) {
                birthdays.contactList = new ArrayList<>();
            }
        }
        return birthdays;
    }

    public boolean shallWeCelebrate() {
        if (this.contactList.size() == 0) {
            refreshList();
        }
        return scanListForParties();
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

    public ArrayList<Contact> getList() {
        return contactList;
    }

    private boolean scanListForParties() {
        boolean anyNotification = false;

        for (Contact person : this.contactList) {
            /* Today notifications */
            if (person.shallWeCelebrateToday()) {
                if (showTodayNotifications) {
                    try {
                        MessageNotification.notify(this.ctx, person, notificationCustomMessage,
                                useNotificationCustomMessage, person.getDaysUntilNextBirthDay());
                        anyNotification = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            /* In advance notifications */
            else if (showNotificationInAdvace &&
                    (person.getDaysUntilNextBirthDay() <= daysBeforeBirthday)) {
                try {
                    MessageNotification.notify(this.ctx, person, notificationCustomMessage,
                            useNotificationCustomMessage, person.getDaysUntilNextBirthDay());
                    anyNotification = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return anyNotification;
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
            Contact contact = new Contact(this.ctx,
                    c.getString(keyColumn),
                    c.getString(nameColumn),
                    c.getString(dateColumn),
                    c.getString(photoColumn));

            if (!contact.isMissingData()) {

                String sign = contact.getSign();
                int age = contact.getAge();
                int month = contact.getMonth();
                int bWeek = contact.getBirthDayWeek();

                if (ageStats.get(age) != null) {
                    ageStats.put(age, ageStats.get(age) + 1);
                }
                if (signStats.get(sign) != null) {
                    signStats.put(sign, signStats.get(sign) + 1);
                }
                if (monthStats.get(month) != null) {
                    monthStats.put(month, signStats.get(month) + 1);
                }
                if (weekStats.get(bWeek) != null) {
                    weekStats.put(bWeek, weekStats.get(bWeek) + 1);
                }

            }

            this.contactList.add(contact);


        } while (c.moveToNext());

        c.close();
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

    private Cursor getCursor() {
        ContentResolver r = this.ctx.getContentResolver();

        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.CommonDataKinds.Event.START_DATE,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
        };

        String selection = ContactsContract.Data.MIMETYPE +
                "=? AND " + ContactsContract.CommonDataKinds.Event.TYPE + "=?";

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

}
