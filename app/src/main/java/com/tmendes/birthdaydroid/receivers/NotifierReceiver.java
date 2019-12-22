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

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.tmendes.birthdaydroid.BirthdayDataProvider;
import com.tmendes.birthdaydroid.Contact;
import com.tmendes.birthdaydroid.helpers.NotificationHelper;
import com.tmendes.birthdaydroid.helpers.PermissionHelper;

import java.util.ArrayList;

public class NotifierReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            PermissionHelper permission = new PermissionHelper(context);
            permission.updatePermissionPreferences(PermissionHelper.CONTACT_PERMISSION, true);
            BirthdayDataProvider bddProvider = BirthdayDataProvider.getInstance();
            bddProvider.setPermissionHelper(context, permission);
            bddProvider.refreshData(true);

            ArrayList<Contact> todayBirthdayList = bddProvider.getContactsToCelebrate();
            for (Contact contact : todayBirthdayList) {
                NotificationHelper.getInstance(context).postNotification(contact);
            }
        }
    }
}
