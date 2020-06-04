package com.tmendes.birthdaydroid.contact;

import android.content.Context;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.tmendes.birthdaydroid.contact.android.AndroidContact;
import com.tmendes.birthdaydroid.contact.android.AndroidContactService;
import com.tmendes.birthdaydroid.contact.db.DBContact;
import com.tmendes.birthdaydroid.contact.db.DBContactService;
import com.tmendes.birthdaydroid.cursor.CursorIterator;
import com.tmendes.birthdaydroid.date.DateConverter;
import com.tmendes.birthdaydroid.permission.PermissionHelper;
import com.tmendes.birthdaydroid.zodiac.ZodiacCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactService {
    private final PermissionHelper permissionHelper;
    private final DBContactService dbContactService;
    private final AndroidContactService androidContactService;
    private final ZodiacCalculator zodiacCalculator;
    private final DateConverter dateConverter;
    private final EventTypeLabelService eventTypeLabelService;

    public ContactService(PermissionHelper permissionHelper, DBContactService dbContactService, AndroidContactService androidContactService, ZodiacCalculator zodiacCalculator, DateConverter dateConverter, EventTypeLabelService eventTypeLabelService) {
        this.permissionHelper = permissionHelper;
        this.dbContactService = dbContactService;
        this.androidContactService = androidContactService;
        this.zodiacCalculator = zodiacCalculator;
        this.dateConverter = dateConverter;
        this.eventTypeLabelService = eventTypeLabelService;
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
                        boolean ignoreContact = false;
                        boolean favoriteContact = false;
                        long contactDBId = -1;

                        DBContact dbContact = dbContacts.remove(androidContact.getLookupKey());
                        if (dbContact != null) {
                            ignoreContact = dbContact.isIgnore();
                            favoriteContact = dbContact.isFavorite();
                            contactDBId = dbContact.getId();
                        }

                        if (hideIgnoredContacts && ignoreContact) {
                            continue;
                        }

                        try {
                            Contact contact = new ContactBuilder(zodiacCalculator, dateConverter)
                                    .setDbId(contactDBId)
                                    .setKey(androidContact.getLookupKey())
                                    .setName(androidContact.getDisplayName())
                                    .setPhotoUri(androidContact.getPhotoThumbnailUri())
                                    .setBirthdayString(androidContact.getStartDate())
                                    .setEventTypeLabel(eventTypeLabelService.getEventTypeLabel(androidContact.getEventType(), androidContact.getEventLabel()))
                                    .setIgnore(ignoreContact)
                                    .setFavorite(favoriteContact)
                                    .build();

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
