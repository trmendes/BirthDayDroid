package com.tmendes.birthdaydroid.contact;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tmendes.birthdaydroid.contact.android.AndroidContactService;
import com.tmendes.birthdaydroid.contact.db.DBContactService;
import com.tmendes.birthdaydroid.date.DateConverter;
import com.tmendes.birthdaydroid.permission.PermissionChecker;
import com.tmendes.birthdaydroid.zodiac.ZodiacCalculator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ContactsViewModel extends AndroidViewModel {
    private MutableLiveData<List<Contact>> contacts = new MutableLiveData<>();
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
            final ZodiacCalculator zodiacCalculator = new ZodiacCalculator();
            final DateConverter dateConverter = new DateConverter();
            final EventTypeLabelService eventTypeLabelService = new EventTypeLabelService(context);
            final ContactFactory contactFactory = new ContactFactory(
                    zodiacCalculator,
                    dateConverter,
                    eventTypeLabelService
            );

            final LocalDate now = LocalDate.now();
            final List<Contact> newContacts = contacts.getValue()
                    .stream()
                    .map(c -> contactFactory.calculateTimeDependentData(c, now))
                    .collect(Collectors.toList());

            contacts.postValue(newContacts);
        });
    }

    private List<Contact> calculateContacts() {
        final Context context = getApplication().getApplicationContext();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean hideIgnoredContacts = prefs.getBoolean("hide_ignored_contacts", false);
        final boolean showBirthdayTypeOnly = prefs.getBoolean("show_birthday_type_only", false);

        final DBContactService dbContactService = new DBContactService(context);
        final AndroidContactService androidContactService = new AndroidContactService(context);
        final ZodiacCalculator zodiacCalculator = new ZodiacCalculator();
        final DateConverter dateConverter = new DateConverter();
        final EventTypeLabelService eventTypeLabelService = new EventTypeLabelService(context);
        final ContactFactory contactFactory = new ContactFactory(
                zodiacCalculator,
                dateConverter,
                eventTypeLabelService
        );
        final ContactService contactService = new ContactService(
                dbContactService,
                androidContactService,
                contactFactory);
        final List<Contact> allContacts = contactService.getAllContacts(hideIgnoredContacts, showBirthdayTypeOnly);
        dbContactService.close();
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
