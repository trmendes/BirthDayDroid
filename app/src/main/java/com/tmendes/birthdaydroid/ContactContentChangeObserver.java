package com.tmendes.birthdaydroid;

import android.database.ContentObserver;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ContactContentChangeObserver extends ContentObserver {
    private final FragmentActivity activity;
    private final ScheduledExecutorService executor;
    private ScheduledFuture<?> lastScheduledFuture;

    public ContactContentChangeObserver(FragmentActivity activity) {
        super(null);
        this.activity = activity;
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public synchronized void onChange(boolean selfChange) {
        if(lastScheduledFuture != null) {
            lastScheduledFuture.cancel(false);
        }
        lastScheduledFuture = executor.schedule(
                () ->
                        ViewModelProviders.of(activity).get(ContactsViewModel.class).reloadContacts()
                , 1, TimeUnit.SECONDS);
    }
}
