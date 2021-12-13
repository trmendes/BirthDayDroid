package com.tmendes.birthdaydroid.contact.android;

import android.database.Cursor;
import android.provider.ContactsContract;

import com.tmendes.birthdaydroid.cursor.CursorIterator;

public class AndroidContactCursorIterator extends CursorIterator<AndroidContact> {
    private int keyColumnIndex = -1;
    private int dateColumnIndex = -1;
    private int nameColumnIndex = -1;
    private int photoColumnIndex = -1;
    private int typeColumnIndex = -1;
    private int typeLabelColumnIndex = -1;

    public AndroidContactCursorIterator(Cursor cursor) {
        super(cursor);
        if (hasNext()) {
            keyColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
            dateColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
            nameColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            photoColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI);
            typeColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.TYPE);
            typeLabelColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.LABEL);
        }
    }

    @Override
    public AndroidContact convertCursor(Cursor cursor) {
        return new AndroidContact(
            cursor.getString(keyColumnIndex),
            cursor.getString(dateColumnIndex),
            cursor.getInt(typeColumnIndex),
            cursor.getString(typeLabelColumnIndex),
            cursor.getString(photoColumnIndex),
            cursor.getString(nameColumnIndex)
        );
    }
}
