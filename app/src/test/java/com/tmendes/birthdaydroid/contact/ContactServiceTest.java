package com.tmendes.birthdaydroid.contact;

import android.provider.ContactsContract;

import com.tmendes.birthdaydroid.contact.android.AndroidContact;
import com.tmendes.birthdaydroid.contact.android.AndroidContactService;
import com.tmendes.birthdaydroid.contact.db.DBContact;
import com.tmendes.birthdaydroid.contact.db.DBContactService;
import com.tmendes.birthdaydroid.cursor.CloseableIterator;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ContactServiceTest {
    private ContactService contactService;
    private DBContactService dbContactService;
    private AndroidContactService androidContactService;
    private ContactFactory contactFactory;

    @Before
    public void setUp() {
        dbContactService = mock(DBContactService.class);
        androidContactService = mock(AndroidContactService.class);
        contactFactory = mock(ContactFactory.class);

        contactService = new ContactService(dbContactService, androidContactService, contactFactory);
    }

    @Test
    public void testWithoutIgnoreHiddenAndOnlyBirthday() {
        final Map<String, DBContact> dbContactMap = new HashMap<>();
        final DBContact dbContact1 = new DBContact(2, false, true);
        dbContactMap.put("c2", dbContact1);
        final DBContact dbContact2 = new DBContact(3, true, false);
        dbContactMap.put("c3", dbContact2);
        doReturn(dbContactMap).when(dbContactService).getAllContacts();

        final AndroidContact androidContact1 = new AndroidContact("c1", null, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY, null, null, null);
        final AndroidContact androidContact2 = new AndroidContact("c2", null, ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY, null, null, null);
        final CloseableIterator<AndroidContact> iterator = new TestClosableIteratorDummy<>(Arrays.asList(androidContact1, androidContact2));
        doReturn(iterator).when(androidContactService).getAndroidContacts();

        final Contact contact1 = mock(Contact.class);
        final Contact contact2 = mock(Contact.class);
        doReturn(contact1).when(contactFactory).createContact(androidContact1, null);
        doReturn(contact2).when(contactFactory).createContact(androidContact2, dbContact1);

        final List<Contact> allContacts = contactService.getAllContacts(false, false);

        assertThat(allContacts.size(), is(2));
        assertThat(allContacts.contains(contact1), is(true));
        assertThat(allContacts.contains(contact2), is(true));
        verify(contactFactory, times(1)).createContact(androidContact1, null);
        verify(contactFactory, times(1)).createContact(androidContact2, dbContact1);

        assertThat(dbContactMap.size(), is(1));
        assertThat(dbContactMap.get("c3"), is(dbContact2));
        verify(dbContactService, times(1)).cleanDb(dbContactMap);
    }

    @Test
    public void testWithIgnoreHidden() {
        final Map<String, DBContact> dbContactMap = new HashMap<>();
        final DBContact dbContact1 = new DBContact(1, false, true);
        dbContactMap.put("c1", dbContact1);
        final DBContact dbContact2 = new DBContact(2, false, false);
        dbContactMap.put("c2", dbContact2);
        doReturn(dbContactMap).when(dbContactService).getAllContacts();

        final AndroidContact androidContact1 = new AndroidContact("c1", null, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY, null, null, null);
        final AndroidContact androidContact2 = new AndroidContact("c2", null, ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY, null, null, null);
        final CloseableIterator<AndroidContact> iterator = new TestClosableIteratorDummy<>(Arrays.asList(androidContact1, androidContact2));
        doReturn(iterator).when(androidContactService).getAndroidContacts();

        final Contact contact1 = mock(Contact.class);
        final Contact contact2 = mock(Contact.class);
        doReturn(contact1).when(contactFactory).createContact(androidContact1, dbContact1);
        doReturn(contact2).when(contactFactory).createContact(androidContact2, dbContact2);

        final List<Contact> allContacts = contactService.getAllContacts(true, false);

        assertThat(allContacts.size(), is(1));
        assertThat(allContacts.contains(contact2), is(true));
        verify(contactFactory, times(0)).createContact(androidContact1, dbContact1);
        verify(contactFactory, times(1)).createContact(androidContact2, dbContact2);

        assertThat(dbContactMap.size(), is(0));
        verify(dbContactService, times(1)).cleanDb(dbContactMap);
    }

    @Test
    public void testWithBirthdayOnly() {
        final Map<String, DBContact> dbContactMap = new HashMap<>();
        final DBContact dbContact1 = new DBContact(1, false, true);
        dbContactMap.put("c1", dbContact1);
        final DBContact dbContact2 = new DBContact(2, false, false);
        dbContactMap.put("c2", dbContact2);
        doReturn(dbContactMap).when(dbContactService).getAllContacts();

        final AndroidContact androidContact1 = new AndroidContact("c1", null, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY, null, null, null);
        final AndroidContact androidContact2 = new AndroidContact("c2", null, ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY, null, null, null);
        final CloseableIterator<AndroidContact> iterator = new TestClosableIteratorDummy<>(Arrays.asList(androidContact1, androidContact2));
        doReturn(iterator).when(androidContactService).getAndroidContacts();

        final Contact contact1 = mock(Contact.class);
        final Contact contact2 = mock(Contact.class);
        doReturn(contact1).when(contactFactory).createContact(androidContact1, dbContact1);
        doReturn(contact2).when(contactFactory).createContact(androidContact2, dbContact2);

        final List<Contact> allContacts = contactService.getAllContacts(false, true);

        assertThat(allContacts.size(), is(1));
        assertThat(allContacts.contains(contact1), is(true));
        verify(contactFactory, times(1)).createContact(androidContact1, dbContact1);
        verify(contactFactory, times(0)).createContact(androidContact2, dbContact2);

        assertThat(dbContactMap.size(), is(0));
        verify(dbContactService, times(1)).cleanDb(dbContactMap);
    }

    @Test
    public void testWithHiddenIgnoredBirthdayOnly() {
        final Map<String, DBContact> dbContactMap = new HashMap<>();
        final DBContact dbContact1 = new DBContact(1, false, true);
        dbContactMap.put("c1", dbContact1);
        final DBContact dbContact2 = new DBContact(2, false, false);
        dbContactMap.put("c2", dbContact2);
        doReturn(dbContactMap).when(dbContactService).getAllContacts();

        final AndroidContact androidContact1 = new AndroidContact("c1", null, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY, null, null, null);
        final AndroidContact androidContact2 = new AndroidContact("c2", null, ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY, null, null, null);
        final CloseableIterator<AndroidContact> iterator = new TestClosableIteratorDummy<>(Arrays.asList(androidContact1, androidContact2));
        doReturn(iterator).when(androidContactService).getAndroidContacts();

        final Contact contact1 = mock(Contact.class);
        final Contact contact2 = mock(Contact.class);
        doReturn(contact1).when(contactFactory).createContact(androidContact1, dbContact1);
        doReturn(contact2).when(contactFactory).createContact(androidContact2, dbContact2);

        final List<Contact> allContacts = contactService.getAllContacts(true, true);

        assertThat(allContacts.size(), is(0));
        verify(contactFactory, times(0)).createContact(androidContact1, dbContact1);
        verify(contactFactory, times(0)).createContact(androidContact2, dbContact2);

        assertThat(dbContactMap.size(), is(0));
        verify(dbContactService, times(1)).cleanDb(dbContactMap);
    }
}
