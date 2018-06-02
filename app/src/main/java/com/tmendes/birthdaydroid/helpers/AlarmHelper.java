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

import com.tmendes.birthdaydroid.MainActivity;
import com.tmendes.birthdaydroid.receivers.BootReceiver;
import com.tmendes.birthdaydroid.receivers.NotifierReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.app.AlarmManager.INTERVAL_DAY;

public class AlarmHelper {

    private AlarmManager alarmManager;

    private final static String ACTION_BD_NOTIFICATION = "com.tmendes.birthdaydroid.NOTIFICATION";

    public void setAlarm(Context context, long toGoesOffAt) {
        Calendar defaultToRingAt = Calendar.getInstance();

        if (toGoesOffAt == 0) {
            defaultToRingAt.set(Calendar.HOUR, MainActivity.DEFAULT_ALARM_TIME);
            defaultToRingAt.set(Calendar.MINUTE, 0);
            defaultToRingAt.set(Calendar.SECOND, 0);
        } else {
            Calendar now = Calendar.getInstance();
            defaultToRingAt.setTimeInMillis(toGoesOffAt);
            defaultToRingAt.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
        }

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedDate = df.format(defaultToRingAt.getTime());
        Log.i("Alarm", "Setting alarm at " + formattedDate);

        toGoesOffAt = defaultToRingAt.getTimeInMillis();

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

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

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(context, NotifierReceiver.class);
        alarmIntent.setAction(ACTION_BD_NOTIFICATION);

        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context,
                0,
                alarmIntent,
                0);
        alarmManager.cancel(pendingAlarmIntent);

        /* Alarm won't start again if device is rebooted */
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
