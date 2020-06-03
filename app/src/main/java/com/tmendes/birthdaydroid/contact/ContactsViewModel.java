package com.tmendes.birthdaydroid.contact;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.contact.ContactService;
import com.tmendes.birthdaydroid.contact.android.AndroidContactService;
import com.tmendes.birthdaydroid.contact.db.DBContactService;
import com.tmendes.birthdaydroid.date.DateConverter;
import com.tmendes.birthdaydroid.permission.PermissionHelper;
import com.tmendes.birthdaydroid.zodiac.ZodiacCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ContactsViewModel extends AndroidViewModel {
    private MutableLiveData<List<Contact>> contacts;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ContactsViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<Contact>> getContacts() {
        if(contacts == null) {
            contacts = new MutableLiveData<>();
            contacts.postValue(new ArrayList<>());
            reloadContactsAsync();
        }
        return contacts;
    }

    private Context getContext() {
        return this.getApplication().getApplicationContext();
    }

    public void reloadContacts() {
        final Context context = getContext();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean hideIgnoredContacts = prefs.getBoolean("hide_ignored_contacts", false);
        final boolean showBirthdayTypeOnly = prefs.getBoolean("show_birthday_type_only", false);

        final PermissionHelper permissionHelper = new PermissionHelper(context);
        final DBContactService dbContactService = new DBContactService(context);
        final AndroidContactService androidContactService = new AndroidContactService(context);
        final ZodiacCalculator zodiacCalculator = new ZodiacCalculator();
        final DateConverter dateConverter = new DateConverter();
        final ContactService contactService = new ContactService(
                permissionHelper,
                dbContactService,
                androidContactService,
                zodiacCalculator,
                dateConverter,
                context
        );
        final List<Contact> allContacts = contactService.getAllContacts(hideIgnoredContacts, showBirthdayTypeOnly);
        dbContactService.close();
        contacts.postValue(allContacts);
    }

    public void reloadContactsAsync() {
        executor.submit(this::reloadContacts);
    }

    @Override
    protected void onCleared() {
        executor.shutdownNow();
        try {
            executor.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.i(this.getClass().toString(), "Error by shutdown ViewModel", e);
        }
        contacts = null;

        super.onCleared();
    }
}
