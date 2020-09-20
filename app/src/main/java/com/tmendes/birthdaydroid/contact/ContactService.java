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

public class ContactService implements AutoCloseable{
    private final DBContactService dbContactService;
    private final AndroidContactService androidContactService;
    private final ContactCreator contactCreator;

    public ContactService(DBContactService dbContactService, AndroidContactService androidContactService, ContactCreator contactCreator) {
        this.dbContactService = dbContactService;
        this.androidContactService = androidContactService;
        this.contactCreator = contactCreator;
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
                    Contact contact = contactCreator.createContact(androidContact, dbContact);
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

    @Override
    public void close() {
        if(dbContactService != null) {
            dbContactService.close();
        }
    }
}
