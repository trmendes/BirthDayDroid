package com.tmendes.birthdaydroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tmendes.birthdaydroid.receivers.BirthDayBroadcastReceiver;

public class BirthDayAlarm {

    private final Context ctx;

    public final static String ACTION_BD_NOTIFICATION = "com.tmendes.birthdaydroid.NOTIFICATION";

    public BirthDayAlarm(Context ctx) {
        this.ctx = ctx;
        Intent intentAlarm = new Intent(this.ctx, BirthDayBroadcastReceiver.class);
        intentAlarm.setAction(ACTION_BD_NOTIFICATION);
        updateSettings();
    }

    private void updateSettings() {
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(this.ctx);
        String sound = s.getString("custom_notification_sound", null);
        //MessageNotification.setNotificationSound(sound);
    }
}
