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

import static android.provider.ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY;
import static android.provider.ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;

public class BirthDay {

    private final ArrayList<Contact> contactList;
    private final Context ctx;
    private final PermissionHelper permissions;

    public BirthDay(Context ctx, PermissionHelper permissions) {
        this.ctx = ctx;
        this.permissions = permissions;
        contactList = new ArrayList<>();
    }

    public void postNotification(Contact contact) {
        if (permissions.checkPermissionPreferences(PermissionHelper.CONTACT_PERMISSION)) {
            try {
                /* Text to notify */
                /* Title */
                String title = contact.getName();
                StringBuilder body = new StringBuilder();

                String eventTypeStr;

                switch (contact.getEventType()) {
                    default:
                    case TYPE_BIRTHDAY:
                        eventTypeStr = ctx.getResources().getString(R.string.type_birthday);
                        break;
                    case TYPE_ANNIVERSARY:
                        eventTypeStr = ctx.getResources().getString(R.string.type_anniversary);
                        break;
                }

                eventTypeStr = eventTypeStr.toLowerCase();

                if (contact.shallWePartyToday()) {
                    if (contact.getDaysOld() > 0) {
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
                                contact.getDaysUntilNextBirthday(),
                                contact.getContactFirstName(), contact.getAge() + 1,
                                contact.getDaysUntilNextBirthday()));
                    } else {
                        body.append(ctx.getResources().getQuantityString(
                                R.plurals.days_until,
                                contact.getDaysUntilNextBirthday(),
                                contact.getDaysUntilNextBirthday(),
                                eventTypeStr));
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
}
