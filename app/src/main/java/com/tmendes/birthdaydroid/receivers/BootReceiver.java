package com.tmendes.birthdaydroid.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.tmendes.birthdaydroid.MainActivity;

import java.util.Calendar;
import java.util.Objects;

public class BootReceiver extends BroadcastReceiver {

    private final AlarmReceiver alarm = new AlarmReceiver();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.requireNonNull(intent.getAction()).equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            long toRingAt = prefs.getLong("scan_daily_interval", 0);
            alarm.setAlarm(context, toRingAt);
        }
    }
}
