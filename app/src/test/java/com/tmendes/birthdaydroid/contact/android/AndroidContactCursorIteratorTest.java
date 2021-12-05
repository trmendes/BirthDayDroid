package com.tmendes.birthdaydroid.contact.android;

import android.database.Cursor;
import android.database.StaleDataException;
import android.provider.ContactsContract;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class AndroidContactCursorIteratorTest {
    @Test
    public void testConvertCursor() {
        final Cursor cursor = mock(Cursor.class);

        doReturn(0).when(cursor).getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
        doReturn("lookupKey").when(cursor).getString(0);
        doReturn(1).when(cursor).getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
        doReturn("startDate").when(cursor).getString(1);
        doReturn(2).when(cursor).getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        doReturn("displayName").when(cursor).getString(2);
        doReturn(3).when(cursor).getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI);
        doReturn("photoThumbnailUri").when(cursor).getString(3);
        doReturn(4).when(cursor).getColumnIndex(ContactsContract.CommonDataKinds.Event.TYPE);
        doReturn(1).when(cursor).getInt(4);
        doReturn(5).when(cursor).getColumnIndex(ContactsContract.CommonDataKinds.Event.LABEL);
        doReturn("eventLabel").when(cursor).getString(5);

        final AndroidContact androidContact = new AndroidContactCursorIterator(cursor).convertCursor(cursor);

        assertThat(androidContact.getLookupKey(), is("lookupKey"));
        assertThat(androidContact.getStartDate(), is("startDate"));
        assertThat(androidContact.getDisplayName(), is("displayName"));
        assertThat(androidContact.getPhotoThumbnailUri(), is("photoThumbnailUri"));
        assertThat(androidContact.getEventType(), is(1));
        assertThat(androidContact.getEventLabel(), is("eventLabel"));
    }

    @Test
    public void testConvertEmptyCursor() {
        final Cursor cursor = mock(Cursor.class);

        doReturn(false).when(cursor).moveToFirst();
        doThrow(StaleDataException.class).when(cursor).getColumnIndex(any());

        final AndroidContactCursorIterator iterator = new AndroidContactCursorIterator(cursor);
        assertThat(iterator.hasNext(), is(false));
    }
}
