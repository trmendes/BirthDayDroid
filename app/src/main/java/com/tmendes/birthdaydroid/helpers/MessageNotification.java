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

package com.tmendes.birthdaydroid.helpers;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;

import com.tmendes.birthdaydroid.Contact;
import com.tmendes.birthdaydroid.R;

import java.io.IOException;

public class MessageNotification {

    private static String notificationId;
    private static Uri notificationSound;

    public static void setNotificationSound(String sound) {
        if (sound != null) {
            MessageNotification.notificationSound = Uri.parse(sound);
        } else {
            MessageNotification.notificationSound = null;
        }
    }

    public static void notify(final Context context, Contact contact, String msgShareBody, boolean customMsg, long daysUntil) throws IOException {

        final Resources res = context.getResources();

        String msgShareTitle;
        if (contact.isaPartyGoingOnToday()) {
            msgShareTitle = res.getString(R.string.message_notification_share_title);
        } else {
            msgShareTitle = res.getString(R.string.message_notification_bt_to_come_title);
        }

        if (contact.isaPartyGoingOnToday()) {
            if ((!customMsg) || (msgShareBody.length() == 0)) {
                msgShareBody = res.getString(R.string.message_notification_share_body, contact.getContactFirstName());
            }
        } else {
            msgShareBody = res.getString(R.string.message_notification_bt_to_come_share_body, contact.getContactFirstName());
        }

        String notifyMsgBody;
        if (contact.isaPartyGoingOnToday()) {
            if (contact.isMissingData()) {
                notifyMsgBody = res.getString(
                        R.string.message_notification_message, contact.getContactFirstName(), contact.getAge());
            } else {
                notifyMsgBody = res.getString(
                        R.string.message_notification_message_no_age, contact.getContactFirstName());
            }
        } else {
            notifyMsgBody = res.getString(
                    R.string.message_notification_message_bt_to_come, contact.getContactFirstName(), contact.getAge() + 1, daysUntil);
        }

        Bitmap notifyPicture;

        if (contact.getPhotoURI() != null) {
            Uri imageUri = Uri.parse(contact.getPhotoURI());
            notifyPicture = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        } else {
            notifyPicture = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.ic_account_circle_black_24dp);
        }
        final String title = contact.getName();
        final String text = notifyMsgBody;
        notificationId = title;

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_cake_white_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(notifyPicture)
                .setTicker(title)
                .setNumber(1)
                .setSound(MessageNotification.notificationSound, RingtoneManager.TYPE_NOTIFICATION)
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(Intent.ACTION_VIEW, Uri.parse(ContactsContract.Contacts.CONTENT_LOOKUP_URI + "/" + contact.getKey())),
                                PendingIntent.FLAG_UPDATE_CURRENT))

                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text)
                        .setBigContentTitle(title))

                .addAction(
                        R.drawable.ic_share_white_24dp,
                        res.getString(R.string.message_notification_action),
                        PendingIntent.getActivity(
                                context,
                                0,
                                Intent.createChooser(new Intent(Intent.ACTION_SEND)
                                        .setType("text/plain")
                                        .putExtra(Intent.EXTRA_TEXT, msgShareBody), msgShareTitle),
                                PendingIntent.FLAG_UPDATE_CURRENT))

                .setAutoCancel(true);

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(notificationId, 0, notification);
        } else {
            nm.notify(notificationId.hashCode(), notification);
        }
    }
}
