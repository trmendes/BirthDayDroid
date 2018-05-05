package com.tmendes.birthdaydroid.helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorRes;

import com.tmendes.birthdaydroid.Contact;
import com.tmendes.birthdaydroid.R;

public class NotificationHelper extends ContextWrapper {
    private NotificationManager notifManager;
    private Context ctx;

    public static final String CHANNEL_ONE_ID = "com.wlnomads.uvindexnot.uvindexnotifications.CHONE";
    public static final String CHANNEL_ONE_NAME = "Channel One";

    public NotificationHelper(Context ctx) {
        super(ctx);
        this.ctx = ctx;
        createChannels();
    }

    public void createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, notifManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(notificationChannel);
        }
    }

    public Notification.Builder getNotification(String title,
                                                String body,
                                                Bitmap notifyPicture,
                                                PendingIntent pI,
                                                @ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Notification.Builder(getApplicationContext(), CHANNEL_ONE_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setColorized(true)
                    .setContentIntent(pI)
                    .setLargeIcon(notifyPicture)
                    .setColor(getApplicationContext().getResources()
                            .getColor(color, ctx.getTheme()))
                    .setSmallIcon(R.drawable.ic_cake_white_24dp)
                    .setAutoCancel(true);

        } else {
            //noinspection deprecation
            return new Notification.Builder(getApplicationContext())
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(pI)
                    .setLargeIcon(notifyPicture)
                    .setSmallIcon(R.drawable.ic_cake_white_24dp)
                    .setAutoCancel(true);
        }
    }

    public void notify(long id, Notification.Builder notification) {
        getManager().notify((int) id, notification.build());
    }

    private NotificationManager getManager() {
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notifManager;
    }
}
