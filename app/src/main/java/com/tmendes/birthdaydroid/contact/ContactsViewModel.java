package com.tmendes.birthdaydroid.contact;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.tmendes.birthdaydroid.permission.PermissionChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ContactsViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Contact>> contacts = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ContactsViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Contact>> getContacts() {
        if (contacts.getValue() == null) {
            contacts.postValue(new ArrayList<>());
            reloadContacts();
        }
        return contacts;
    }

    public Future<?> reloadContacts() {
        final Context context = getApplication().getApplicationContext();
        if (new PermissionChecker(context).checkReadContactsPermission()) {
            return executor.submit(() -> contacts.postValue(calculateContacts()));
        } else {
            return executor.submit(() -> contacts.postValue(new ArrayList<>()));
        }
    }

    public Future<?> reloadTimeDependentDataInContacts() {
        return this.executor.submit(() -> {
            final Context context = getApplication().getApplicationContext();
            final ContactCreator contactCreator = new ContactCreatorFactory().createContactCreator(context);
            final List<Contact> contacts = this.contacts.getValue();
            if (contacts != null) {
                this.contacts.postValue(
                        contacts.stream()
                                .map(c -> {c.updateEventData(); return c;})
                                .collect(Collectors.toList())
                );
            }
        });
    }

    private List<Contact> calculateContacts() {
        final Context context = getApplication().getApplicationContext();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean hideIgnoredContacts = prefs.getBoolean("hide_ignored_contacts", false);
        final boolean showBirthdayTypeOnly = prefs.getBoolean("show_birthday_type_only", false);

        ContactService contactService = new ContactServiceFactory().createContactService(context);
        final List<Contact> allContacts = contactService.getAllContacts(hideIgnoredContacts, showBirthdayTypeOnly);
        contactService.close();
        return allContacts;
    }

    @Override
    protected void onCleared() {
        executor.shutdownNow();
        try {
            executor.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.i(this.getClass().toString(), "Error by shutdown ViewModel", e);
        }
        super.onCleared();
    }
}
