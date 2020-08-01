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
import com.tmendes.birthdaydroid.contact.ContactFactory;
import com.tmendes.birthdaydroid.contact.ContactService;
import com.tmendes.birthdaydroid.contact.EventTypeLabelService;
import com.tmendes.birthdaydroid.contact.android.AndroidContactService;
import com.tmendes.birthdaydroid.contact.db.DBContactService;
import com.tmendes.birthdaydroid.date.DateConverter;
import com.tmendes.birthdaydroid.helpers.NotificationHelper;
import com.tmendes.birthdaydroid.permission.PermissionChecker;
import com.tmendes.birthdaydroid.zodiac.ZodiacCalculator;

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

            final DBContactService dbContactService = new DBContactService(context);
            final AndroidContactService androidContactService = new AndroidContactService(context);
            final ZodiacCalculator zodiacCalculator = new ZodiacCalculator();
            final DateConverter dateConverter = new DateConverter();
            final EventTypeLabelService eventTypeLabelService = new EventTypeLabelService(context);
            final ContactFactory contactFactory = new ContactFactory(
                    zodiacCalculator,
                    dateConverter,
                    eventTypeLabelService
            );
            final ContactService contactService = new ContactService(
                    dbContactService,
                    androidContactService,
                    contactFactory);
            final List<Contact> allContacts = contactService.getAllContacts(hideIgnoredContacts, showBirthdayTypeOnly);
            dbContactService.close();

            final NotificationHelper notificationHelper = new NotificationHelper(context);
            allContacts.stream()
                    .filter(c -> c.isBirthdayToday()
                            || (showPreciseNotification && c.getDaysUntilNextBirthday() == daysInAdvance)
                            || (!showPreciseNotification && c.getDaysUntilNextBirthday() <= daysInAdvance))
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
