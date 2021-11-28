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

package com.tmendes.birthdaydroid.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.contact.ContactService;
import com.tmendes.birthdaydroid.contact.ContactServiceFactory;
import com.tmendes.birthdaydroid.helpers.NotificationHelper;
import com.tmendes.birthdaydroid.permission.PermissionChecker;

import java.util.List;

public class NotifierReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (new PermissionChecker(context).checkReadContactsPermission()) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            final boolean hideIgnoredContacts = prefs.getBoolean("hide_ignored_contacts", false);
            final boolean showBirthdayTypeOnly = prefs.getBoolean("show_birthday_type_only", false);
            final boolean showPreciseNotification = prefs.getBoolean("precise_notification", false);
            final int daysInAdvance = getDaysInAdviceFromPreferences(prefs);

            final ContactService contactService = new ContactServiceFactory().createContactService(context);
            final List<Contact> allContacts = contactService.getAllContacts(hideIgnoredContacts, showBirthdayTypeOnly);
            contactService.close();

            final NotificationHelper notificationHelper = new NotificationHelper(context);
            allContacts.stream()
                    .filter(c -> c.isCelebrationToday()
                            || (showPreciseNotification && c.getDaysUntilNextEvent() == daysInAdvance)
                            || (!showPreciseNotification && c.getDaysUntilNextEvent() <= daysInAdvance))
                    .forEach(notificationHelper::postNotification);
        }
    }

    private int getDaysInAdviceFromPreferences(SharedPreferences prefs) {
        boolean notificationInAdvance = prefs.getBoolean("scan_in_advance", false);
        int daysInAdvance = 0;
        if (notificationInAdvance) {
            daysInAdvance = prefs.getInt("days_in_advance_interval", 0);
        }
        return daysInAdvance;
    }
}
