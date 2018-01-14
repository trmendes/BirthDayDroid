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

    private boolean isBatOk = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean keepShowing;
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(context);
        keepShowing = s.getBoolean("check_battery_status", true);

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            new BirthDayAlarm(context);
        } else if (intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
            isBatOk = false;
        } else if (intent.getAction().equals(Intent.ACTION_BATTERY_OKAY)) {
            isBatOk = true;
        }

        if (intent.getAction().equals(BirthDayAlarm.ACTION_BD_NOTIFICATION)) {
            if (keepShowing) {
                /* We're going to lie to this app because the users told us to do so by
                * asking us to show notifications even though the battery is running low */
                isBatOk = true;
            }

            if (isBatOk) {
                BirthDayDataList bd = BirthDayDataList.getBirthDayDataList(context);
                bd.checkBirthdaysPartyForToday();
            }
        }
    }
}
