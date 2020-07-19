package com.tmendes.birthdaydroid.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.tmendes.birthdaydroid.contact.ContactsViewModel;

import java.time.LocalDate;

public class LocalDateNowChangeReceiver extends BroadcastReceiver {
    private final FragmentActivity activity;
    private LocalDate lastLocalDate;

    public LocalDateNowChangeReceiver(FragmentActivity activity) {
        this.activity = activity;
        this.lastLocalDate = LocalDate.now();
    }

    @Override
    public synchronized void onReceive(Context context, Intent intent) {
        final LocalDate now = LocalDate.now();
        if(!lastLocalDate.equals(now)) {
            lastLocalDate = now;
            ViewModelProviders.of(activity).get(ContactsViewModel.class).reloadTimeDependentDataInContacts();
        }
    }
}