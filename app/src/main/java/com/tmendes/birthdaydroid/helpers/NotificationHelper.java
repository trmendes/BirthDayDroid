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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import com.tmendes.birthdaydroid.Contact;
import com.tmendes.birthdaydroid.R;

import java.io.IOException;

public class NotificationHelper extends ContextWrapper {
    private static NotificationHelper instance;

    private Context ctx;
    private NotificationManager notifManager;

    private static final String CHANNEL_ONE_ID = "com.wlnomads.uvindexnot.uvindexnotifications.CHONE";
    private static final String CHANNEL_ONE_NAME = "Channel One";

    private NotificationHelper(Context ctx) {
        super(ctx);
        createChannels();
        this.ctx = ctx;
    }

    public static NotificationHelper getInstance(Context ctx) {
        if (instance == null) {
            instance = new NotificationHelper(ctx);
        }
        return instance;
    }

    private void createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(notificationChannel);
        }
    }

    private Notification.Builder getNotification(String title,
                                                 String body,
                                                 Bitmap notifyPicture,
                                                 PendingIntent pI) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Notification.Builder(getApplicationContext(), CHANNEL_ONE_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setColorized(true)
                    .setShowWhen(true)
                    .setContentIntent(pI)
                    .setLargeIcon(notifyPicture)
                    .setSmallIcon(R.drawable.ic_cake_white_24dp)
                    .setAutoCancel(true);

        } else {
            //noinspection deprecation
            return new Notification.Builder(getApplicationContext())
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(pI)
                    .setLargeIcon(notifyPicture)
                    .setShowWhen(true)
                    .setSmallIcon(R.drawable.ic_cake_white_24dp)
                    .setAutoCancel(true);
        }
    }

    private void notify(long id, Notification.Builder notification) {
        getManager().notify((int) id, notification.build());
    }

    private NotificationManager getManager() {
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notifManager;
    }

    public void postNotification(Contact contact) {
        try {
            String title = contact.getName() + " " + contact.getEventTypeLabel();
            StringBuilder body = new StringBuilder();


            if (contact.shallWePartyToday()) {
                if (contact.getDaysUntilNextBirthday() == 0) {
                    body.append(ctx.getString(R.string.party_message));
                } else {
                    body.append(ctx.getResources().getQuantityString(
                            R.plurals.message_notification_message_bt_to_come,
                            contact.getDaysUntilNextBirthday(),
                            contact.getContactFirstName(), contact.getAge() + 1,
                            contact.getDaysUntilNextBirthday()));
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
            Notification.Builder nBuilder = getNotification(title, body.toString(), notifyPicture,
                    openContactPI);
            notify(System.currentTimeMillis(), nBuilder);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
