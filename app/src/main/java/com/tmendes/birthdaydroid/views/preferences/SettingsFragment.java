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

package com.tmendes.birthdaydroid.views.preferences;

import android.accounts.Account;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.preference.CheckBoxPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.helpers.AccountHelper;
import com.tmendes.birthdaydroid.helpers.AlarmHelper;
import com.tmendes.birthdaydroid.views.preferences.numberpicker.NumberPickerFragment;
import com.tmendes.birthdaydroid.views.preferences.numberpicker.NumberPickerPreference;
import com.tmendes.birthdaydroid.views.preferences.timepicker.TimePickerFragment;
import com.tmendes.birthdaydroid.views.preferences.timepicker.TimePickerPreference;

import java.util.Arrays;
import java.util.Objects;

import static android.content.Context.POWER_SERVICE;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String DIALOG_FRAGMENT_TAG = "androidx.preference.PreferenceFragment.DIALOG";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        setPowerServiceStatus();
        initIgnoredAccountPreference();
    }

    private void initIgnoredAccountPreference() {
        MultiSelectListPreference selectedAccounts = findPreference("selected_accounts");

        Account[] accounts = new AccountHelper().getAllAccounts(requireContext());

        Arrays.sort(accounts, (o1, o2) -> o1.name.compareToIgnoreCase(o2.name));
        String[] entries = new String[accounts.length];
        String[] entryValues = new String[accounts.length];

        for (int i = 0; i < accounts.length; i++) {
            String accountName = accounts[i].name;
            entries[i] = accountName;
            entryValues[i] = accountName;
        }

        selectedAccounts.setEntries(entries);
        selectedAccounts.setEntryValues(entryValues);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        setPowerServiceStatus();

        CheckBoxPreference mCheckBoxPref = findPreference("battery_status");
        mCheckBoxPref.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent();
            String packageName = requireActivity().getPackageName();
            PowerManager pm = (PowerManager) requireActivity().getSystemService(POWER_SERVICE);

            if (!Objects.requireNonNull(pm).isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
            } else {
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            }
            startActivity(intent);
            return true;
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        setPowerServiceStatus();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals("scan_daily") || key.equals("scan_daily_interval")) {
            boolean dailyNotification = prefs.getBoolean("scan_daily", true);

            AlarmHelper alarm = new AlarmHelper();

            if (dailyNotification) {
                long toGoesOffAt = prefs.getLong("scan_daily_interval", -1);
                alarm.setAlarm(requireContext(), toGoesOffAt);
            } else {
                alarm.cancelAlarm(requireContext());
            }
        } else if (key.equals("dark_theme")) {
            requireActivity().finish();
            final Intent intent = requireActivity().getIntent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            requireActivity().startActivity(intent);
        }
    }

    void setPowerServiceStatus() {
        CheckBoxPreference mCheckBoxPref = findPreference("battery_status");
        mCheckBoxPref.setChecked(checkPowerServiceStatus());
    }

    boolean checkPowerServiceStatus() {
        String packageName = requireActivity().getPackageName();
        PowerManager pm = (PowerManager) requireActivity().getSystemService(POWER_SERVICE);
        return Objects.requireNonNull(pm).isIgnoringBatteryOptimizations(packageName);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (getParentFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) != null) {
            return;
        }

        final DialogFragment f;

        if (preference instanceof NumberPickerPreference) {
            f = NumberPickerFragment.newInstance(preference.getKey());
        } else if (preference instanceof TimePickerPreference) {
            f = TimePickerFragment.newInstance(preference.getKey());
        } else {
            f = null;
        }

        if (f != null) {
            f.setTargetFragment(this, 0);
            f.show(getParentFragmentManager(), DIALOG_FRAGMENT_TAG);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
