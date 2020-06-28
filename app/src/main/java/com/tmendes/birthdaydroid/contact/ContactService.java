package com.tmendes.birthdaydroid.contact;

import android.provider.ContactsContract;
import android.util.Log;

import com.tmendes.birthdaydroid.contact.android.AndroidContact;
import com.tmendes.birthdaydroid.contact.android.AndroidContactService;
import com.tmendes.birthdaydroid.contact.db.DBContact;
import com.tmendes.birthdaydroid.contact.db.DBContactService;
import com.tmendes.birthdaydroid.cursor.CloseableIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        final Map<String, DBContact> dbContacts = dbContactService.getAllContacts();

        try (CloseableIterator<AndroidContact> cursorIterator = androidContactService.getAndroidContacts()) {
            while (cursorIterator.hasNext()) {
                final AndroidContact androidContact = cursorIterator.next();
                final DBContact dbContact = dbContacts.remove(androidContact.getLookupKey());

                if (showBirthdayTypeOnly && androidContact.getEventType() != ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY) {
                    continue;
                }
                if (hideIgnoredContacts && dbContact != null && dbContact.isIgnore()) {
                    continue;
                }

                try {
                    Contact contact = contactFactory.createContact(androidContact, dbContact);
                    contacts.add(contact);
                } catch (ContactFactoryException e) {
                    Log.i(getClass().toString(), "Unable to build contact", e);
                }
            }
        } catch (Exception e) {
            Log.i(getClass().toString(), "Error by closing resource", e);
        }

        dbContactService.cleanDb(dbContacts);

        return contacts;
    }
}
