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

package com.tmendes.birthdaydroid.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.CheckBoxPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.helpers.AccountHelper;
import com.tmendes.birthdaydroid.helpers.AlarmHelper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

import static android.content.Context.POWER_SERVICE;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings,
                container, false);

        Objects.requireNonNull(getActivity()).getFragmentManager().beginTransaction()
                .replace(R.id.setting_frame, new PrefFragment())
                .commit();

        return v;
    }

    public static class PrefFragment extends PreferenceFragment implements
            SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            setPowerServiceStatus();
            setHasOptionsMenu(false);
            initIgnoredAccountPreference();
        }

        private void initIgnoredAccountPreference() {
            MultiSelectListPreference ignoredAccountsPreference =
                    (MultiSelectListPreference) findPreference("selected_accounts");

            Account[] accounts = new AccountHelper().getAllAccounts(getContext());

            Arrays.sort(accounts, new Comparator<Account>() {
                @Override
                public int compare(Account o1, Account o2) {
                    return 0;
                }
            });
            String[] entries = new String[accounts.length];
            String[] entryValues = new String[accounts.length];

            for (int i = 0; i < accounts.length; i++) {
                String accountName = accounts[i].name;
                entries[i] = accountName;
                entryValues[i] = accountName;
            }

            ignoredAccountsPreference.setEntries(entries);
            ignoredAccountsPreference.setEntryValues(entryValues);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            menu.clear();
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            setPowerServiceStatus();

            CheckBoxPreference mCheckBoxPref = (CheckBoxPreference)
                    findPreference("battery_status");
            mCheckBoxPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @SuppressLint("BatteryLife")
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent();
                    String packageName = getActivity().getPackageName();
                    PowerManager pm = (PowerManager) getActivity().getSystemService(POWER_SERVICE);

                    if (!Objects.requireNonNull(pm).isIgnoringBatteryOptimizations(packageName)) {
                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + packageName));
                        startActivity(intent);
                    } else {
                        intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                        startActivity(intent);
                    }
                    return true;
                }
                });

        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
            setPowerServiceStatus();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (key.equals("scan_daily") || key.equals("scan_daily_interval")) {
                boolean dailyNotification = prefs.getBoolean("scan_daily", false);

                AlarmHelper alarm = new AlarmHelper();

                if (dailyNotification) {
                    long toGoesOffAt = prefs.getLong("scan_daily_interval", 0);
                    alarm.setAlarm(getContext(), toGoesOffAt);
                } else {
                    alarm.cancelAlarm(getContext());
                }
            } else if (key.equals("dark_theme")) {
                getActivity().finish();
                final Intent intent = getActivity().getIntent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
            }
        }

        void setPowerServiceStatus() {
            CheckBoxPreference mCheckBoxPref = (CheckBoxPreference)
                    findPreference("battery_status");
            mCheckBoxPref.setChecked(checkPowerServiceStatus());
        }

        boolean checkPowerServiceStatus() {
            String packageName = getActivity().getPackageName();
            PowerManager pm = (PowerManager) getActivity().getSystemService(POWER_SERVICE);
            return Objects.requireNonNull(pm).isIgnoringBatteryOptimizations(packageName);
        }
    }
}
