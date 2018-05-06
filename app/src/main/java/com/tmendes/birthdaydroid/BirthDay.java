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

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import com.tmendes.birthdaydroid.helpers.NotificationHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class BirthDay {

    private static BirthDay birthdays;

    // Contact list
    private ArrayList<Contact> contactList;

    // Statistics about your friends
    private final Map<Integer, Integer> ageStats = new TreeMap<>();
    private final Map<String, Integer> signStats = new TreeMap<>();
    private final Map<Integer, Integer> monthStats = new TreeMap<>();
    private final Map<Integer, Integer> weekStats = new TreeMap<>();

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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        boolean anyNotification = false;
        boolean showTodayNotifications = prefs.getBoolean("scan_daily", false);
        boolean showNotificationInAdvace = prefs.getBoolean("scan_in_advance", false);
        int daysInAdvance = Integer.parseInt(prefs
                .getString("scan_in_advance_interval", "3"));

        if (!showTodayNotifications) {
            return false;
        }

        for (Contact contact : this.contactList) {
            if (contact.shallWeCelebrateToday()) {
                /* Today notifications */
                anyNotification = true;
                postNotification(contact);
            }
            else if (showNotificationInAdvace &&
                    (contact.getDaysUntilNextBirthDay() <= daysInAdvance)) {
                /* In advance notifications */
                anyNotification = true;
                postNotification(contact);
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
                } else {
                    ageStats.put(age, 1);
                }
                if (signStats.get(sign) != null) {
                    signStats.put(sign, signStats.get(sign) + 1);
                } else {
                    signStats.put(sign, 1);
                }
                if (monthStats.get(month) != null) {
                    monthStats.put(month, monthStats.get(month) + 1);
                } else {
                    monthStats.put(month, 1);
                }
                if (weekStats.get(bWeek) != null) {
                    weekStats.put(bWeek, weekStats.get(bWeek) + 1);
                } else {
                    weekStats.put(bWeek, 1);
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

    @SuppressLint("ResourceType")
    //FIXME REMOVE SUPPRESS
    private void postNotification(Contact contact) {
        try {
            /* Text to notify */
            /* Title */
            String title = contact.getName();
            StringBuilder body = new StringBuilder();
            if (contact.shallWeCelebrateToday()) {
                if (contact.isMissingYear()) {
                    body.append(ctx.getString(
                            R.string.message_notification_message_no_age,
                            contact.getContactFirstName()));
                } else {
                    body.append(ctx.getString(
                            R.string.message_notification_message, contact.getContactFirstName(),
                            contact.getAge()));
                }
            } else {
                if (contact.isMissingYear()) {
                    body.append(ctx.getString(
                            R.string.message_notification_message_bt_to_come_no_year,
                            contact.getContactFirstName(),
                            contact.getDaysUntilNextBirthDay()));
                } else {
                    body.append(ctx.getString(
                            R.string.message_notification_message_bt_to_come,
                            contact.getContactFirstName(), contact.getAge() + 1,
                            contact.getDaysUntilNextBirthDay()));
                }
            }

            /* Contact Picture */
            Bitmap notifyPicture;
            if (contact.getPhotoURI() != null) {
                notifyPicture = MediaStore.Images.Media.getBitmap(
                        ctx.getContentResolver(), Uri.parse(contact.getPhotoURI()));
            } else {
                notifyPicture = BitmapFactory.decodeResource(ctx.getResources(),
                        R.drawable.ic_account_circle_black_24dp);
            }

            /* To open contact when notification clicked */
            Intent openContact = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(ContactsContract.Contacts.CONTENT_LOOKUP_URI + "/"
                            + contact.getKey()));
            PendingIntent openContactPI = PendingIntent.getActivity(ctx, 0, openContact,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            /* Notify */
            NotificationHelper nHelper = new NotificationHelper(this.ctx);
            Notification.Builder nBuilder = nHelper
                    .getNotification(title, body.toString(),
                            notifyPicture, openContactPI, Color.RED);
            nHelper.notify(System.currentTimeMillis(), nBuilder);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
