package com.tmendes.birthdaydroid.contact;

import android.content.Context;
import android.content.res.Resources;
import android.provider.ContactsContract;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ContactsContract.CommonDataKinds.Event.class)
public class EventTypeLabelServiceTest {
    private Resources resources;
    private EventTypeLabelService service;

    @Before
    public void setUp() {
        Context context = mock(Context.class);
        resources = mock(Resources.class);
        doReturn(resources).when(context).getResources();

        PowerMockito.mockStatic(ContactsContract.CommonDataKinds.Event.class);
        service = new EventTypeLabelService(context);
    }

    @Test
    public void testBirthday() {
        testDefaultEventType(
                ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY,
                "Birthday", null, "Birthday"
        );

        testDefaultEventType(
                ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY,
                "Birthday", "42", "Birthday"
        );
    }

    @Test
    public void testBirthdayCapitalize() {
        testDefaultEventType(
                ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY,
                "bIRTHday", null,"Birthday"
        );

        testDefaultEventType(
                ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY,
                "bIRTHday", "42","Birthday"
        );
    }

    @Test
    public void testAnniversary() {
        testDefaultEventType(
                ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY,
                "Anniversar", null, "Anniversar"
        );

        testDefaultEventType(
                ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY,
                "Anniversar", "42", "Anniversar"
        );
    }

    @Test
    public void testAnniversaryCapitalize() {
        testDefaultEventType(
                ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY,
                "aNNIVERsar", null, "Anniversar"
        );

        testDefaultEventType(
                ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY,
                "aNNIVERsar", "42", "Anniversar"
        );
    }

    @Test
    public void testOther() {
        testDefaultEventType(
                ContactsContract.CommonDataKinds.Event.TYPE_OTHER,
                "Other", null, "Other"
        );

        testDefaultEventType(
                ContactsContract.CommonDataKinds.Event.TYPE_OTHER,
                "Other", "42", "Other"
        );
    }

    @Test
    public void testOtherCapitalize() {
        testDefaultEventType(
                ContactsContract.CommonDataKinds.Event.TYPE_OTHER,
                "oTHer", null, "Other"
        );

        testDefaultEventType(
                ContactsContract.CommonDataKinds.Event.TYPE_OTHER,
                "oTHer", "42", "Other"
        );
    }

    @Test
    public void testCustom() {
        PowerMockito.verifyStatic(ContactsContract.CommonDataKinds.Event.class, never());
        ContactsContract.CommonDataKinds.Event.getTypeLabel(nullable(Resources.class), nullable(int.class), nullable(String.class));

        final String label = service.getEventTypeLabel(ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM, "Label");
        assertThat(label, is("Label"));
    }

    @Test
    public void testCustomNotCapitalized() {
        PowerMockito.verifyStatic(ContactsContract.CommonDataKinds.Event.class, never());
        ContactsContract.CommonDataKinds.Event.getTypeLabel(nullable(Resources.class), nullable(int.class), nullable(String.class));

        final String label = service.getEventTypeLabel(ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM, "lABEl");
        assertThat(label, is("lABEl"));
    }

    @Test
    public void testCustomWithoutLabel() {
        testDefaultEventType(
                ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM,
                "Custom", null, "Custom"
        );

        testDefaultEventType(
                ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM,
                "Custom", "", "Custom"
        );
    }

    @Test
    public void testCustomWithoutLabelCapitalized() {
        testDefaultEventType(
                ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM,
                "cUSTom", null, "Custom"
        );

        testDefaultEventType(
                ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM,
                "cUSTom", "", "Custom"
        );
    }

    private void testDefaultEventType(int eventType, String mockOutputLabel, String inputLabel, String expectedLabel) {
        when(ContactsContract.CommonDataKinds.Event.getTypeLabel(resources, eventType, inputLabel))
                .thenReturn(mockOutputLabel);

        final String eventTypeLabel1 = service.getEventTypeLabel(eventType, inputLabel);
        assertThat(eventTypeLabel1, is(expectedLabel));
    }
}
