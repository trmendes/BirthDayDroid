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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.tmendes.birthdaydroid.receivers.BootReceiver;
import com.tmendes.birthdaydroid.receivers.NotifierReceiver;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static android.app.AlarmManager.INTERVAL_DAY;

public class AlarmHelper {

    private final static String ACTION_BD_NOTIFICATION = "com.tmendes.birthdaydroid.NOTIFICATION";

    public static final int DEFAULT_ALARM_TIME_HOUR = 8;

    public void setAlarm(Context context, long toGoesOffAt) {
        final LocalTime localTime;
        if (toGoesOffAt == -1) {
            localTime = LocalTime.of(DEFAULT_ALARM_TIME_HOUR, 0);
        } else {
            localTime = LocalTime.of(0, 0).plus(toGoesOffAt, ChronoUnit.MILLIS);
        }

        final LocalDateTime now = LocalDateTime.now();
        LocalDateTime defaultToRingAt = now.with(localTime);

        if(now.isAfter(defaultToRingAt)) {
            defaultToRingAt = defaultToRingAt.plusDays(1);
        }

        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        Log.i(getClass().toString(), "Setting alarm at " + defaultToRingAt.format(dtf));

        toGoesOffAt = defaultToRingAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(context, NotifierReceiver.class);
        alarmIntent.setAction(ACTION_BD_NOTIFICATION);

        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context,
                0,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        /* Repeat it every 24 hours from the configured time */
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                toGoesOffAt,
                INTERVAL_DAY,
                pendingAlarmIntent);

        /* Restart if rebooted */
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        context.getPackageManager().setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context) {
        Log.i("Alarm", "Cancel the alarm");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(context, NotifierReceiver.class);
        alarmIntent.setAction(ACTION_BD_NOTIFICATION);

        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context,
                0,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingAlarmIntent);

        /* Alarm won't start again if device is rebooted */
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
