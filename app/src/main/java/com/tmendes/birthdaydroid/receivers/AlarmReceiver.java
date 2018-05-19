package com.tmendes.birthdaydroid.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.tmendes.birthdaydroid.BirthDay;
import com.tmendes.birthdaydroid.Contact;
import com.tmendes.birthdaydroid.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;

import static android.app.AlarmManager.INTERVAL_DAY;

public class AlarmReceiver extends BroadcastReceiver {

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        BirthDay birthDay = new BirthDay(context);
        birthDay.refresh();
        ArrayList<Contact> todayBirthdayList = birthDay
                .shallWeCelebrate();
        for (Contact contact : todayBirthdayList) {
            birthDay.postNotification(contact);
        }
    }

    public void setAlarm(Context context, long toGoesOffAt) {

        if (toGoesOffAt == 0) {
            Calendar defaultToRingAt = Calendar.getInstance();
            defaultToRingAt.set(Calendar.HOUR, MainActivity.DEFAULT_ALARM_TIME);
            defaultToRingAt.set(Calendar.MINUTE, 0);
            defaultToRingAt.set(Calendar.SECOND, 0);
            toGoesOffAt = defaultToRingAt.getTimeInMillis();
        }

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmIntent = PendingIntent.getBroadcast(context,
                0,
                new Intent(context, AlarmReceiver.class),
                0);

        /* Repeat it every 24 hours from the configured time */
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                toGoesOffAt,
                INTERVAL_DAY,
                alarmIntent);

        /* Restart if rebooted */
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        context.getPackageManager().setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(context,
                0,
                new Intent(context, AlarmReceiver.class),
                0);
        alarmManager.cancel(alarmIntent);

        /* Alarm won't start again if device is rebooted */
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
