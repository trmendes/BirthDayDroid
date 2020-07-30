package com.tmendes.birthdaydroid;

import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;

import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;

public class DrawerMenuPreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final NavigationView navView;
    private final MenuItem[] zodiacDrawerMenuItems;

    public DrawerMenuPreferenceChangeListener(NavigationView navView, MenuItem[] zodiacDrawerMenuItems) {
        this.navView = navView;
        this.zodiacDrawerMenuItems = zodiacDrawerMenuItems;
        refreshMenuItems(PreferenceManager.getDefaultSharedPreferences(navView.getContext()));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        switch (key) {
            case "hide_zodiac":
            case "settings_statistics_as_text":
                this.refreshMenuItems(prefs);
                break;
        }
    }

    private void refreshMenuItems(SharedPreferences prefs) {
        final boolean statisticsAsText = prefs.getBoolean("settings_statistics_as_text", false);
        Menu menu = this.navView.getMenu();
        menu.setGroupVisible(R.id.group_statistics_text, statisticsAsText);
        menu.setGroupVisible(R.id.group_statistics_diagram, !statisticsAsText);

        final boolean hide_zodiac = prefs.getBoolean("hide_zodiac", false);
        if (hide_zodiac) {
            for (MenuItem zodiacDrawerMenuItem : this.zodiacDrawerMenuItems) {
                if (zodiacDrawerMenuItem.isVisible()) {
                    zodiacDrawerMenuItem.setVisible(false);
                }
            }
        }
    }
}
