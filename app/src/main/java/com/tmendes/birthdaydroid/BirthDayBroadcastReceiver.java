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

package com.tmendes.birthdaydroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BirthDayBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean bat_ok = true, check_bat;
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(context);
        check_bat = s.getBoolean("check_battery_status", false);

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            new BirthDayAlarm(context);
        } else if (intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
            bat_ok = false;
        } else if (intent.getAction().equals(Intent.ACTION_BATTERY_OKAY)) {
            bat_ok = true;
        }

        if (intent.getAction().equals(BirthDayAlarm.ACTION_BD_NOTIFICATION)) {
            bat_ok = bat_ok && check_bat;
            if (bat_ok) {
                BirthDayDataList bd = BirthDayDataList.getBirthDayDataList(context);
                bd.isThereAnyBirthDayToday();
            }
        }
    }
}
