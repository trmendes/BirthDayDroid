package com.tmendes.birthdaydroid.contact.android;

import android.database.Cursor;
import android.provider.ContactsContract;

import com.tmendes.birthdaydroid.cursor.CursorIterator;

public class AndroidContactCursorIterator extends CursorIterator<AndroidContact> {
    private final int keyColumn;
    private final int dateColumn;
    private final int nameColumn;
    private final int photoColumn;
    private final int typeColumn;
    private final int typeLabelColumn;

    protected AndroidContactCursorIterator(Cursor cursor) {
        super(cursor);
        keyColumn = cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
        dateColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
        nameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        photoColumn = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI);
        typeColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.TYPE);
        typeLabelColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.LABEL);
    }

    @Override
    public AndroidContact convertCursor(Cursor cursor) {
        return new AndroidContact(
            cursor.getString(keyColumn),
            cursor.getString(dateColumn),
            cursor.getInt(typeColumn),
            cursor.getString(typeLabelColumn),
            cursor.getString(photoColumn),
            cursor.getString(nameColumn)
        );
    }
}
