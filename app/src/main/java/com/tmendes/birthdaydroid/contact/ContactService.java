package com.tmendes.birthdaydroid.contact;

import android.provider.ContactsContract;
import android.util.Log;

import com.tmendes.birthdaydroid.contact.android.AndroidContact;
import com.tmendes.birthdaydroid.contact.android.AndroidContactService;
import com.tmendes.birthdaydroid.contact.db.DBContact;
import com.tmendes.birthdaydroid.contact.db.DBContactService;
import com.tmendes.birthdaydroid.cursor.CloseableIterator;
import com.tmendes.birthdaydroid.permission.PermissionHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactService {
    private final DBContactService dbContactService;
    private final AndroidContactService androidContactService;
    private final ContactFactory contactFactory;

    public ContactService(DBContactService dbContactService, AndroidContactService androidContactService, ContactFactory contactFactory) {
        this.dbContactService = dbContactService;
        this.androidContactService = androidContactService;
        this.contactFactory = contactFactory;
    }

    public List<Contact> getAllContacts(boolean hideIgnoredContacts, boolean showBirthdayTypeOnly) {
        final List<Contact> contacts = new ArrayList<>();
        final HashMap<String, DBContact> dbContacts = dbContactService.getAllContacts();

        try (CloseableIterator<AndroidContact> cursorIterator = androidContactService.getAndroidContacts()) {
            while (cursorIterator.hasNext()) {
                AndroidContact androidContact = cursorIterator.next();

                final boolean parseContacts = !showBirthdayTypeOnly ||
                        androidContact.getEventType() == ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;

                if (parseContacts) {

                    DBContact dbContact = dbContacts.remove(androidContact.getLookupKey());
                    if (hideIgnoredContacts && dbContact != null && dbContact.isIgnore()) {
                        continue;
                    }

                    try {
                        Contact contact = contactFactory.createContact(androidContact, dbContact);
                        contacts.add(contact);
                    } catch (ContactBuilderException e) {
                        Log.i(getClass().toString(), "Unable to build contact", e);
                    }
                }

                dbContactService.cleanDb(dbContacts);
            }
        } catch (Exception e) {
            Log.i(getClass().toString(), "Error by closing resource", e);
        }

        return contacts;
    }
}
