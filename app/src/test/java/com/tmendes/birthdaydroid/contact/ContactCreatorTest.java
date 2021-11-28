package com.tmendes.birthdaydroid.contact;

import android.provider.ContactsContract;

import com.tmendes.birthdaydroid.contact.android.AndroidContact;
import com.tmendes.birthdaydroid.contact.db.DBContact;
import com.tmendes.birthdaydroid.date.DateConverter;
import com.tmendes.birthdaydroid.zodiac.Zodiac;
import com.tmendes.birthdaydroid.zodiac.ZodiacCalculator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ContactCreatorTest {

    private ZodiacCalculator zodiacCalculator;
    private DateConverter dateConverter;
    private EventTypeLabelService eventTypeLabelService;
    private ContactCreator contactCreator;

    @Before
    public void setUp() {
        zodiacCalculator = mock(ZodiacCalculator.class);
        dateConverter = mock(DateConverter.class);
        eventTypeLabelService = mock(EventTypeLabelService.class);
        contactCreator = new ContactCreator(zodiacCalculator, dateConverter, eventTypeLabelService);
    }

    @Test
    public void testAndroidContactNullException() {
        final DBContact dbContact = new DBContact(
                1,
                false,
                false
        );
        Assert.assertThrows(
                ContactFactoryException.class,
                () -> contactCreator.createContact(null, dbContact)
        );
    }

    @Test
    public void testKeyNullException() {
        final AndroidContact androidContact = new AndroidContact(
                null,
                "2020-01-01",
                ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY,
                null,
                "uri",
                "Name Name"
        );

        final DBContact dbContact = new DBContact(
                1,
                false,
                false
        );

        Assert.assertThrows(
                ContactFactoryException.class,
                () -> contactCreator.createContact(androidContact, dbContact)
        );
    }

    @Test
    public void testNameNullException() {
        final AndroidContact androidContact = new AndroidContact(
                "key",
                "2020-01-01",
                ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY,
                null,
                "uri",
                null
        );

        final DBContact dbContact = new DBContact(
                1,
                false,
                false
        );

        Assert.assertThrows(
                ContactFactoryException.class,
                () -> contactCreator.createContact(androidContact, dbContact)
        );
    }

    @Test
    public void testStartDateStringNullException() {
        final AndroidContact androidContact = new AndroidContact(
                "key",
                null,
                ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY,
                null,
                "uri",
                "Name Name"
        );

        final DBContact dbContact = new DBContact(
                1,
                false,
                false
        );

        Assert.assertThrows(
                ContactFactoryException.class,
                () -> contactCreator.createContact(androidContact, dbContact)
        );
    }

    @Test
    public void testDateConvertingFails() {
        final AndroidContact androidContact = new AndroidContact(
                "key",
                "2020-01-01",
                ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY,
                null,
                "uri",
                "Name Name"
        );
        final DBContact dbContact = new DBContact(
                1,
                false,
                false
        );

        instrumentDependencies(
                DateConverter.DateConverterResult.createNotSuccess(),
                "Birthday",
                Zodiac.CAPRICORN
        );

        Assert.assertThrows(
                ContactFactoryException.class,
                () -> contactCreator.createContact(androidContact, dbContact)
        );
    }

    @Test
    public void testSuccessfulCreation() {
        final AndroidContact androidContact = new AndroidContact(
                "key",
                "2020-01-01",
                ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY,
                null,
                "uri",
                "Name Name"
        );
        final DBContact dbContact = new DBContact(
                1,
                false,
                false
        );

        LocalDate bornOn = LocalDate.of(2019, 1, 1);
        instrumentDependencies(
                DateConverter.DateConverterResult.createSuccess(false, bornOn),
                "Birthday",
                Zodiac.CAPRICORN
        );

        final Contact contact = contactCreator.createContact(androidContact, dbContact);

        assertThat(contact.getDbId(), is(1L));
        assertThat(contact.getKey(), is("key"));
        assertThat(contact.getName(), is("Name Name"));
        assertThat(contact.getPhotoUri(), is("uri"));
        assertThat(contact.getEventTypeLabel(), is("Birthday"));
        assertThat(contact.isFavorite(), is(false));
        assertThat(contact.isIgnore(), is(false));
        assertThat(contact.getEventOriginalDate(), is(bornOn));
        assertThat(contact.isEventMissingYear(), is(false));
        assertThat(contact.getZodiac(), is(Zodiac.CAPRICORN));

        // calculated date dependent information. is tested in other tests explicitly and exacter.
        assertThat(contact.isFromFuture(), is(false));
        assertThat(contact.getNextYearEvent(), notNullValue());
        assertThat(contact.getAgeInYears(), greaterThanOrEqualTo(0));
        assertThat(contact.getAgeInDays(), greaterThanOrEqualTo(0));
        assertThat(contact.getDaysUntilNextEvent(), greaterThanOrEqualTo(0));
        assertThat(contact.getDaysSinceLastEvent(), greaterThanOrEqualTo(0));
    }

    @Test
    public void testDbContactNull() {
        final AndroidContact androidContact = new AndroidContact(
                "key",
                "2020-01-01",
                ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY,
                null,
                "uri",
                "Name Name"
        );

        LocalDate bornOn = LocalDate.of(2019, 1, 1);
        instrumentDependencies(
                DateConverter.DateConverterResult.createSuccess(false, bornOn),
                "Birthday",
                Zodiac.CAPRICORN
        );

        final Contact contact = contactCreator.createContact(androidContact, null);

        assertThat(contact.getDbId(), is(-1L));
        assertThat(contact.getKey(), is("key"));
        assertThat(contact.getName(), is("Name Name"));
        assertThat(contact.getPhotoUri(), is("uri"));
        assertThat(contact.getEventTypeLabel(), is("Birthday"));
        assertThat(contact.isFavorite(), is(false));
        assertThat(contact.isIgnore(), is(false));
        assertThat(contact.getEventOriginalDate(), is(bornOn));
        assertThat(contact.isEventMissingYear(), is(false));
        assertThat(contact.getZodiac(), is(Zodiac.CAPRICORN));

        // calculated date dependent information. is tested in other tests explicitly and exacter.
        assertThat(contact.isFromFuture(), is(false));
        assertThat(contact.getNextYearEvent(), notNullValue());
        assertThat(contact.getAgeInYears(), greaterThanOrEqualTo(0));
        assertThat(contact.getAgeInDays(), greaterThanOrEqualTo(0));
        assertThat(contact.getDaysUntilNextEvent(), greaterThanOrEqualTo(0));
        assertThat(contact.getDaysSinceLastEvent(), greaterThanOrEqualTo(0));
    }

    @Test
    public void testCalculatedBirthdayInformationBirthday() {
        final LocalDate now = LocalDate.of(2020, 6, 7);
        final LocalDate eventOriginalDate = now.minusYears(1);
        final Contact contact = new Contact();
        contact.setEventData(eventOriginalDate, now, false);

        assertThat(contact.isCelebrationToday(), is(true));
        assertThat(contact.isFromFuture(), is(false));
        assertThat(contact.getAgeInYears(), is(1));
        assertThat(contact.getAgeInDays(), is(366)); // leapyear 29. February
        assertThat(contact.getCurrentYearEvent(), is(now));
        assertThat(contact.getNextYearEvent(), is(now.plusYears(1)));
        assertThat(contact.getDaysUntilNextEvent(), is(0));
        assertThat(contact.getDaysSinceLastEvent(), is(366)); // leapyear 29. February
    }

    @Test
    public void testCalculatedBirthdayInformationBornInFuture() {
        final LocalDate now = LocalDate.of(2020, 6, 7);
        final LocalDate eventOriginalDate = now.plusDays(3);
        final Contact contact = new Contact();
        contact.setEventData(eventOriginalDate, now,false);

        assertThat(contact.isCelebrationToday(), is(false));
        assertThat(contact.isFromFuture(), is(true));
        assertThat(contact.getAgeInYears(), is(0));
        assertThat(contact.getAgeInDays(), is(0));
        assertThat(contact.getCurrentYearEvent(), is(eventOriginalDate));
        assertThat(contact.getNextYearEvent(), is(eventOriginalDate.plusYears(1)));
        assertThat(contact.getDaysUntilNextEvent(), is(3));
        assertThat(contact.getDaysSinceLastEvent(), is(0));
    }

    @Test
    public void testCalculatedBirthdayInformationNoBirthday() {
        final LocalDate now = LocalDate.of(2020, 6, 7);
        final LocalDate eventOriginalDate = now.minusYears(1).minusDays(1);
        final Contact contact = new Contact();
        contact.setEventData(eventOriginalDate, now,false);

        assertThat(contact.isCelebrationToday(), is(false));
        assertThat(contact.isFromFuture(), is(false));
        assertThat(contact.getAgeInYears(), is(1));
        assertThat(contact.getAgeInDays(), is(367)); // leapyear 29. February
        assertThat(contact.getCurrentYearEvent(), is(now.minusDays(1)));
        assertThat(contact.getNextYearEvent(), is(now.minusDays(1).plusYears(1)));
        assertThat(contact.getDaysUntilNextEvent(), is(364));
        assertThat(contact.getDaysSinceLastEvent(), is(1));
    }

    @Test
    public void testCalculatedBirthdayInformationMissingYearInfoAfterNow() {
        final LocalDate now = LocalDate.of(2020, 6, 7);
        final LocalDate eventOriginalDate = now.plusDays(1);
        final Contact contact = new Contact();
        contact.setEventData(eventOriginalDate, now,true);

        assertThat(contact.isCelebrationToday(), is(false));
        assertThat(contact.isFromFuture(), is(false));
        assertThat(contact.getAgeInYears(), is(0));
        assertThat(contact.getAgeInDays(), is(0));
        assertThat(contact.getCurrentYearEvent(), is(eventOriginalDate));
        assertThat(contact.getNextYearEvent(), is(eventOriginalDate.plusYears(1)));
        assertThat(contact.getDaysUntilNextEvent(), is(1));
        assertThat(contact.getDaysSinceLastEvent(), is(365)); // leapyear 29. February
    }

    @Test
    public void testCalculatedBirthdayInformationMissingYearInfoBeforeNow() {
        final LocalDate now = LocalDate.of(2020, 6, 7);
        final LocalDate eventOriginalDate = now.minusDays(1);
        final Contact contact = new Contact();
        contact.setEventData(eventOriginalDate, now,true);

        assertThat(contact.isCelebrationToday(), is(false));
        assertThat(contact.isFromFuture(), is(false));
        assertThat(contact.getAgeInYears(), is(0));
        assertThat(contact.getAgeInDays(), is(0));
        assertThat(contact.getCurrentYearEvent(), is(eventOriginalDate));
        assertThat(contact.getNextYearEvent(), is(eventOriginalDate.plusYears(1)));
        assertThat(contact.getDaysUntilNextEvent(), is(364));
        assertThat(contact.getDaysSinceLastEvent(), is(1));
    }

    private void instrumentDependencies(DateConverter.DateConverterResult dateConverterResult,
                                        String eventTypeLabelResult,
                                        @Zodiac int zodiacResult) {
        doReturn(dateConverterResult)
                .when(dateConverter)
                .convert(nullable(String.class));
        doReturn(eventTypeLabelResult)
                .when(eventTypeLabelService)
                .getEventTypeLabel(any(int.class), nullable(String.class));
        doReturn(zodiacResult)
                .when(zodiacCalculator)
                .calculateZodiac(any(LocalDate.class));
    }
}
