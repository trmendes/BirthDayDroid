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

package com.tmendes.birthdaydroid.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tmendes.birthdaydroid.helpers.AlarmHelper;

import java.util.Objects;

public class BootReceiver extends BroadcastReceiver {

    private final AlarmHelper alarm = new AlarmHelper();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = Objects.requireNonNull(intent.getAction());
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)
                || action.equals(Intent.ACTION_TIME_CHANGED)
                || action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            long toRingAt = prefs.getLong("scan_daily_interval", 0);
            alarm.cancelAlarm(context);
            alarm.setAlarm(context, toRingAt);
        }
    }
}
