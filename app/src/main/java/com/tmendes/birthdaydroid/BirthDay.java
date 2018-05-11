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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

    public ArrayList<Contact> shallWeCelebrate(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        boolean showTodayNotifications = prefs.getBoolean("scan_daily", false);
        boolean showNotificationInAdvace = prefs.getBoolean("scan_in_advance", false);
        int daysInAdvance = Integer.parseInt(prefs
                .getString("scan_in_advance_interval", "3"));

        ArrayList<Contact> notifications = new ArrayList<>();

        if (showTodayNotifications) {
            for (Contact contact : this.contactList) {
                if (contact.shallWeCelebrateToday()) {
                    /* Today notifications */
                    notifications.add(contact);
                } else if (showNotificationInAdvace &&
                        (contact.getDaysUntilNextBirthDay() <= daysInAdvance)) {
                    /* In advance notifications */
                    notifications.add(contact);
                }
            }
        }

        return notifications;
    }

    public void clearLists() {
        contactList.clear();
        ageStats.clear();
        signStats.clear();
        monthStats.clear();
        weekStats.clear();
    }
}
