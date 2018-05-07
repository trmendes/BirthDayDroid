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

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    // Contact list
    private final ArrayList<Contact> contactList;

    // Statistics about your friends
    private final Map<Integer, Integer> ageStats;
    private final Map<String, Integer> signStats;
    private final Map<Integer, Integer> monthStats;
    private final Map<Integer, Integer> weekStats;

    public BirthDay() {
        contactList = new ArrayList<>();
        ageStats = new TreeMap<>();
        signStats = new TreeMap<>();
        monthStats = new TreeMap<>();
        weekStats = new TreeMap<>();
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

    public boolean shallWeCelebrate(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        boolean showTodayNotifications = prefs.getBoolean("scan_daily", false);
        boolean showNotificationInAdvace = prefs.getBoolean("scan_in_advance", false);
        int daysInAdvance = Integer.parseInt(prefs
                .getString("scan_in_advance_interval", "3"));

        boolean anyNotification = false;

        if (!showTodayNotifications) {
            return false;
        }

        for (Contact contact : this.contactList) {
            if (contact.shallWeCelebrateToday()) {
                /* Today notifications */
                anyNotification = true;
                postNotification(ctx, contact);
            }
            else if (showNotificationInAdvace &&
                    (contact.getDaysUntilNextBirthDay() <= daysInAdvance)) {
                /* In advance notifications */
                anyNotification = true;
                postNotification(ctx, contact);
            }
        }

        return anyNotification;
    }

    public void clearLists() {
        if (contactList != null) {
            contactList.clear();
            ageStats.clear();
            signStats.clear();
            monthStats.clear();
            weekStats.clear();
        }
    }

    private void postNotification(Context ctx, Contact contact) {
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
            NotificationHelper nHelper = new NotificationHelper(ctx);
            Notification.Builder nBuilder = nHelper
                    .getNotification(title, body.toString(),
                            notifyPicture, openContactPI, Color.BLUE);
            nHelper.notify(System.currentTimeMillis(), nBuilder);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
