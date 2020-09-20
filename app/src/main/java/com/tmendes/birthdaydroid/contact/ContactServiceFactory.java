package com.tmendes.birthdaydroid.contact;

import android.content.Context;

import com.tmendes.birthdaydroid.contact.android.AndroidContactService;
import com.tmendes.birthdaydroid.contact.db.DBContactService;

public class ContactServiceFactory {
    public ContactService createContactService(Context context) {
        final DBContactService dbContactService = new DBContactService(context);
        final AndroidContactService androidContactService = new AndroidContactService(context);
        final ContactCreator contactCreator = new ContactCreatorFactory().createContactCreator(context);
        return new ContactService(dbContactService, androidContactService, contactCreator);
    }
}
