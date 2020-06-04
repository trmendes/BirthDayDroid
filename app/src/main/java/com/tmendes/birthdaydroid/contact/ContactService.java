package com.tmendes.birthdaydroid.contact;

import android.provider.ContactsContract;
import android.util.Log;

import com.tmendes.birthdaydroid.contact.android.AndroidContact;
import com.tmendes.birthdaydroid.contact.android.AndroidContactService;
import com.tmendes.birthdaydroid.contact.db.DBContact;
import com.tmendes.birthdaydroid.contact.db.DBContactService;
import com.tmendes.birthdaydroid.cursor.CursorIterator;
import com.tmendes.birthdaydroid.permission.PermissionHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactService {
    private final PermissionHelper permissionHelper;
    private final DBContactService dbContactService;
    private final AndroidContactService androidContactService;
    private final ContactFactory contactFactory;

    public ContactService(PermissionHelper permissionHelper, DBContactService dbContactService, AndroidContactService androidContactService, ContactFactory contactFactory) {
        this.permissionHelper = permissionHelper;
        this.dbContactService = dbContactService;
        this.androidContactService = androidContactService;
        this.contactFactory = contactFactory;
    }

    public List<Contact> getAllContacts(boolean hideIgnoredContacts, boolean showBirthdayTypeOnly) {
        final List<Contact> contacts = new ArrayList<>();
        if (permissionHelper.checkReadContactsPermission()) {
            final HashMap<String, DBContact> dbContacts = dbContactService.getAllContacts();

            try (CursorIterator<AndroidContact> cursorIterator = androidContactService.getAndroidContacts()) {
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
            }
        }

        return contacts;
    }
}
