package com.tmendes.birthdaydroid.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tmendes.birthdaydroid.MainActivity;

import java.util.Calendar;

public class BirthDayReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        Calendar defaultToRingAt = Calendar.getInstance();
        defaultToRingAt.set(Calendar.HOUR_OF_DAY, MainActivity.DEFAULT_ALARM_TIME);
        defaultToRingAt.set(Calendar.MINUTE, 0);
        defaultToRingAt.set(Calendar.SECOND, 0);

        long toRingAt = prefs.getLong("scan_daily_interval",
                defaultToRingAt.getTimeInMillis());
        
        new AlarmReceiver().setAlarm(context, toRingAt);
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
    }
}
