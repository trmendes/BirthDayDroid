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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.R;

import java.io.IOException;

public class NotificationHelper extends ContextWrapper {
    private static final String CHANNEL_ONE_ID = "com.wlnomads.uvindexnot.uvindexnotifications.CHONE";
    private static final String CHANNEL_ONE_NAME = "Channel One";

    public NotificationHelper(Context ctx) {
        super(ctx);
        createChannels();
    }

    private void createChannels() {
        NotificationChannel notificationChannel;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotification(String title,
                                                       String body,
                                                       Bitmap notifyPicture,
                                                       PendingIntent pI) {

        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ONE_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setColorized(true) // Ignore on API level < 26
                .setShowWhen(true)
                .setContentIntent(pI)
                .setLargeIcon(notifyPicture)
                .setSmallIcon(R.drawable.ic_cake_white_24dp)
                .setAutoCancel(true);
    }

    private NotificationManager getManager() {
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void postNotification(Contact contact) {
        try {
            boolean notify;

            if (contact.isIgnore()) {
                notify = false;
            } else {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean favoritesOnly = prefs.getBoolean("notify_favorites_only", false);
                if (!favoritesOnly) {
                    notify = true;
                } else {
                    notify = contact.isFavorite();
                }
            }

            if (notify) {
                String title = contact.getName() + " (" + contact.getEventTypeLabel() + ")";
                StringBuilder body = new StringBuilder();

                if (contact.isBirthdayToday()) {
                    body.append(getBaseContext().getString(R.string.party_message));
                } else {
                    final String firstName = contact.getName().split(" ")[0];
                    if (!contact.isMissingYearInfo()) {
                        body.append(getBaseContext().getResources().getQuantityString(
                                R.plurals.message_notification_message_bt_to_come,
                                contact.getDaysUntilNextBirthday(),
                                firstName, contact.getAgeInYears(),
                                contact.getDaysUntilNextBirthday()));
                    } else {
                        body.append(getBaseContext().getResources().getQuantityString(
                                R.plurals.message_notification_message_bt_to_come_no_age,
                                contact.getDaysUntilNextBirthday(),
                                firstName,
                                contact.getDaysUntilNextBirthday()));
                    }
                }

                /* Contact Picture */
                Bitmap notifyPicture;
                if (contact.getPhotoUri() != null) {
                    notifyPicture = MediaStore.Images.Media.getBitmap(
                            getBaseContext().getContentResolver(),
                            Uri.parse(contact.getPhotoUri()));
                } else {
                    notifyPicture = BitmapFactory.decodeResource(getBaseContext().getResources(),
                            R.drawable.ic_account_circle_black_48dp);
                }

                /* To open contact when notification clicked */
                Intent openContact = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(ContactsContract.Contacts.CONTENT_LOOKUP_URI + "/"
                                + contact.getKey()));
                PendingIntent openContactPI = PendingIntent.getActivity(getBaseContext(),
                        0, openContact,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                /* Notify */
                NotificationCompat.Builder nBuilder = getNotification(title, body.toString(), notifyPicture,
                        openContactPI);
                getManager().notify((int) System.currentTimeMillis(), nBuilder.build());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
