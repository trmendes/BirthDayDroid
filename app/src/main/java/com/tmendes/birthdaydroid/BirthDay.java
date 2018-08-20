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
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import com.tmendes.birthdaydroid.helpers.NotificationHelper;
import com.tmendes.birthdaydroid.helpers.PermissionHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class BirthDay {

    // Contact list
    private final ArrayList<Contact> contactList;
    private final StatisticsProvider statistics;
    private final Context ctx;
    private final PermissionHelper permissions;

    public BirthDay(Context ctx, PermissionHelper permissions) {
        this.ctx = ctx;
        this.permissions = permissions;
        contactList = new ArrayList<>();
        statistics = new StatisticsProvider();
    }

    public ArrayList<Contact> shallWeCelebrate() {
        ArrayList<Contact> notifications = new ArrayList<>();
        if (permissions.checkPermissionPreferences(PermissionHelper.CONTACT_PERMISSION)) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            boolean showTodayNotifications = prefs.getBoolean("scan_daily", false);
            boolean showNotificationInAdvace = prefs.getBoolean("scan_in_advance", false);
            boolean preciseAdvanceNotification = prefs.getBoolean("precise_notification", false);
            int daysInAdvance = prefs.getInt("days_in_advance_interval", 1);


            if (showTodayNotifications) {
                for (Contact contact : this.contactList) {
                    long daysUntilNextBirthday = contact.getDaysUntilNextBirthDay();
                    if (contact.shallWeCelebrateToday()) {
                        /* Today notifications */
                        notifications.add(contact);
                    } else if (showNotificationInAdvace &&
                            daysUntilNextBirthday > 0 &&
                            daysUntilNextBirthday != Long.MAX_VALUE &&
                            daysUntilNextBirthday <= daysInAdvance) {
                        /* In advance notifications */
                        if (preciseAdvanceNotification) {
                            if (daysUntilNextBirthday == daysInAdvance) {
                                notifications.add(contact);
                            }
                        } else {
                            /* Notify the user every day until the contact birthday */
                            notifications.add(contact);
                        }
                    }
                }
            }
        }

        return notifications;
    }

    public ArrayList<Contact> getBirthDayList() {
        if (permissions.checkPermissionPreferences(PermissionHelper.CONTACT_PERMISSION)) {
            return contactList;
        }
        return new ArrayList<>();
    }

    public Map<Integer, Integer> getAgeStats() {
        if (permissions.checkPermissionPreferences(PermissionHelper.CONTACT_PERMISSION)) {
            return statistics.getAgeStats();
        }
        return new TreeMap<>();
    }

    public Map<String, Integer> getSignStats() {
        if (permissions.checkPermissionPreferences(PermissionHelper.CONTACT_PERMISSION)) {
            return statistics.getSignStats();
        }
        return new TreeMap<>();
    }

    public Map<Integer, Integer> getMonthStats() {
        if (permissions.checkPermissionPreferences(PermissionHelper.CONTACT_PERMISSION)) {
            return statistics.getMonthStats();
        }
        return new TreeMap<>();
    }

    public Map<Integer, Integer> getWeekStats() {
        if (permissions.checkPermissionPreferences(PermissionHelper.CONTACT_PERMISSION)) {
            return statistics.getWeekStats();
        }
        return new TreeMap<>();
    }

    public ArrayList<Contact> getFailContactList() {
        if (permissions.checkPermissionPreferences(PermissionHelper.CONTACT_PERMISSION)) {
            return statistics.getFailList();
        }
        return new ArrayList<>();
    }

    public void postNotification(Contact contact) {
        if (permissions.checkPermissionPreferences(PermissionHelper.CONTACT_PERMISSION)) {
            try {
                /* Text to notify */
                /* Title */
                String title = contact.getName();
                StringBuilder body = new StringBuilder();
                if (contact.shallWeCelebrateToday()) {
                    if (contact.getAge() > 0) {
                        body.append(ctx.getString(
                                R.string.message_notification_message, contact.getContactFirstName(),
                                contact.getAge()));
                    } else {
                        body.append(ctx.getString(R.string.party_message));
                    }
                } else {
                    if (contact.getAge() > 0) {
                        body.append(ctx.getResources().getQuantityString(
                                R.plurals.message_notification_message_bt_to_come,
                                contact.getDaysUntilNextBirthDay().intValue(),
                                contact.getContactFirstName(), contact.getAge() + 1,
                                contact.getDaysUntilNextBirthDay().intValue()));
                    } else {
                        body.append(ctx.getResources().getQuantityString(
                                R.plurals.days_until,
                                contact.getDaysUntilNextBirthDay().intValue()));
                    }

                }

                /* Contact Picture */
                Bitmap notifyPicture;
                if (contact.getPhotoURI() != null) {
                    notifyPicture = MediaStore.Images.Media.getBitmap(
                            ctx.getContentResolver(),
                            Uri.parse(contact.getPhotoURI()));
                } else {
                    notifyPicture = BitmapFactory.decodeResource(ctx.getResources(),
                            R.drawable.ic_account_circle_black_24dp);
                }

                /* To open contact when notification clicked */
                Intent openContact = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(ContactsContract.Contacts.CONTENT_LOOKUP_URI + "/"
                                + contact.getKey()));
                PendingIntent openContactPI = PendingIntent.getActivity(ctx,
                        0, openContact,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                /* Notify */
                NotificationHelper nHelper = new NotificationHelper(ctx);
                @SuppressLint("ResourceType") Notification.Builder nBuilder = nHelper
                        .getNotification(title, body.toString(),
                                notifyPicture, openContactPI);
                nHelper.notify(System.currentTimeMillis(), nBuilder);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void refresh() {
        if (permissions.checkPermissionPreferences(PermissionHelper.CONTACT_PERMISSION)) {
            Cursor c = getCursor();

            if (!Objects.requireNonNull(c).moveToFirst()) {
                c.close();
                return;
            }

            final int keyColumn = c.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
            final int dateColumn = c.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
            final int nameColumn = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            final int photoColumn = c.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI);

            clearLists();

            do {
                Contact contact = new Contact(ctx,
                        c.getString(keyColumn),
                        c.getString(nameColumn),
                        c.getString(dateColumn),
                        c.getString(photoColumn));

                if (!contact.failOnParseDateString()) {
                    String sign = contact.getSign();
                    int age = contact.getAge();
                    int month = contact.getMonth();
                    int bWeek = contact.getBirthDayWeek();

                    if (statistics.getAgeStats().get(age) != null) {
                        statistics.getAgeStats().put(age, statistics.getAgeStats().get(age) + 1);
                    } else {
                        statistics.getAgeStats().put(age, 1);
                    }

                    if (statistics.getSignStats().get(sign) != null) {
                        statistics.getSignStats().put(sign,
                                statistics.getSignStats().get(sign) + 1);
                    } else {
                        statistics.getSignStats().put(sign, 1);
                    }

                    if (statistics.getMonthStats().get(month) != null) {
                        statistics.getMonthStats().put(month,
                                statistics.getMonthStats().get(month) + 1);
                    } else {
                        statistics.getMonthStats().put(month, 1);
                    }

                    if (statistics.getWeekStats().get(bWeek) != null) {
                        statistics.getWeekStats().put(bWeek,
                                statistics.getWeekStats().get(bWeek) + 1);
                    } else {
                        statistics.getWeekStats().put(bWeek, 1);
                    }

                    contactList.add(contact);
                } else {
                    statistics.getFailList().add(contact);
                }
            } while (c.moveToNext());

            c.close();
        }
    }

    private void clearLists() {
        if (permissions.checkPermissionPreferences(PermissionHelper.CONTACT_PERMISSION)) {
            contactList.clear();
            statistics.reset();
        }
    }

    private Cursor getCursor() {
        if (permissions.checkPermissionPreferences(PermissionHelper.CONTACT_PERMISSION)) {
            ContentResolver r = ctx.getContentResolver();

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
        return null;
    }
}
