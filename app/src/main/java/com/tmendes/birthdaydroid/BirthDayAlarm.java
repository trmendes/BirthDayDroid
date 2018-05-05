package com.tmendes.birthdaydroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tmendes.birthdaydroid.helpers.MessageNotification;
import com.tmendes.birthdaydroid.receivers.BirthDayBroadcastReceiver;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class BirthDayAlarm {

    private final PendingIntent pIntent;
    private final Context ctx;

    public final static String ACTION_BD_NOTIFICATION = "com.tmendes.birthdaydroid.NOTIFICATION";

    public BirthDayAlarm(Context ctx) {
        this.ctx = ctx;
        Intent intentAlarm = new Intent(this.ctx, BirthDayBroadcastReceiver.class);
        intentAlarm.setAction(ACTION_BD_NOTIFICATION);
        pIntent = PendingIntent.getBroadcast(this.ctx, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        updateSettings();
    }

    public void updateSettings() {
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(this.ctx);
        boolean scanDaily = s.getBoolean("scan_daily", true);
        boolean scanInAdvance = s.getBoolean("scan_in_advance", false);
        long scanDailyInterval = s.getLong("scan_daily_interval", new GregorianCalendar().getTimeInMillis());
        String sound = s.getString("custom_notification_sound", null);
        boolean preciseNotification = s.getBoolean("precise_notification", false);

        MessageNotification.setNotificationSound(sound);
    }
}
