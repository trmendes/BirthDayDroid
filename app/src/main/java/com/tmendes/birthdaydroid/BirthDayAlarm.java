package com.tmendes.birthdaydroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.GregorianCalendar;

class BirthDayAlarm {

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

        if ((!scanDaily) && (!scanInAdvance)) {
            this.cancelAlarm();
        } else {
            this.startAlarm(scanDailyInterval, preciseNotification);
        }
    }

    private void startAlarm(long mils, boolean preciseNotification) {
        this.cancelAlarm();

        AlarmManager alarmManager = (AlarmManager) this.ctx.getSystemService(Context.ALARM_SERVICE);

        Calendar now = Calendar.getInstance();
        Calendar alarm = Calendar.getInstance();

        alarm.setTimeInMillis(mils);

        if (alarm.before(now)) {
            alarm.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + 1);
        }

        if (preciseNotification) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pIntent);
        } else {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pIntent);
        }
    }

    private void cancelAlarm() {
        AlarmManager manager = (AlarmManager) this.ctx.getSystemService(Context.ALARM_SERVICE);
        if ((manager != null) && (pIntent != null)) {
            manager.cancel(pIntent);
        }
    }
}
