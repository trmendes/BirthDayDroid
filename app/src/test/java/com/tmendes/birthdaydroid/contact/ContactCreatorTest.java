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
        assertThat(contact.getBornOn(), is(bornOn));
        assertThat(contact.isMissingYearInfo(), is(false));
        assertThat(contact.getZodiac(), is(Zodiac.CAPRICORN));

        // calculated date dependent information. is tested in other tests explicitly and exacter.
        assertThat(contact.isBornInFuture(), is(false));
        assertThat(contact.getNextBirthday(), notNullValue());
        assertThat(contact.getAgeInYears(), greaterThanOrEqualTo(0));
        assertThat(contact.getAgeInDays(), greaterThanOrEqualTo(0));
        assertThat(contact.getDaysUntilNextBirthday(), greaterThanOrEqualTo(0));
        assertThat(contact.getDaysSinceLastBirthday(), greaterThanOrEqualTo(0));
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
        assertThat(contact.getBornOn(), is(bornOn));
        assertThat(contact.isMissingYearInfo(), is(false));
        assertThat(contact.getZodiac(), is(Zodiac.CAPRICORN));

        // calculated date dependent information. is tested in other tests explicitly and exacter.
        assertThat(contact.isBornInFuture(), is(false));
        assertThat(contact.getNextBirthday(), notNullValue());
        assertThat(contact.getAgeInYears(), greaterThanOrEqualTo(0));
        assertThat(contact.getAgeInDays(), greaterThanOrEqualTo(0));
        assertThat(contact.getDaysUntilNextBirthday(), greaterThanOrEqualTo(0));
        assertThat(contact.getDaysSinceLastBirthday(), greaterThanOrEqualTo(0));
    }

    @Test
    public void testCalculatedBirthdayInformationBirthday() {
        final LocalDate now = LocalDate.of(2020, 6, 7);
        final Contact contact = new Contact();
        contact.setBornOn(now.minusYears(1));
        contact.setMissingYearInfo(false);

        final Contact resultContact = contactCreator.calculateTimeDependentData(contact, now);

        assertThat(resultContact.isBirthdayToday(), is(true));
        assertThat(resultContact.isBornInFuture(), is(false));
        assertThat(resultContact.getAgeInYears(), is(1));
        assertThat(resultContact.getAgeInDays(), is(366)); // leapyear 29. February
        assertThat(resultContact.getNextBirthday(), is(now));
        assertThat(resultContact.getDaysUntilNextBirthday(), is(0));
        assertThat(resultContact.getDaysSinceLastBirthday(), is(366)); // leapyear 29. February
    }

    @Test
    public void testCalculatedBirthdayInformationBornInFuture() {
        final LocalDate now = LocalDate.of(2020, 6, 7);
        final Contact contact = new Contact();
        contact.setBornOn(now.plusDays(3));
        contact.setMissingYearInfo(false);

        final Contact resultContact = contactCreator.calculateTimeDependentData(contact, now);

        assertThat(resultContact.isBirthdayToday(), is(false));
        assertThat(resultContact.isBornInFuture(), is(true));
        assertThat(resultContact.getAgeInYears(), is(0));
        assertThat(resultContact.getAgeInDays(), is(0));
        assertThat(resultContact.getNextBirthday(), is(now.plusDays(3)));
        assertThat(resultContact.getDaysUntilNextBirthday(), is(3));
        assertThat(resultContact.getDaysSinceLastBirthday(), is(0));
    }

    @Test
    public void testCalculatedBirthdayInformationNoBirthday() {
        final LocalDate now = LocalDate.of(2020, 6, 7);
        final Contact contact = new Contact();
        contact.setBornOn(now.minusYears(1).minusDays(1));
        contact.setMissingYearInfo(false);

        final Contact resultContact = contactCreator.calculateTimeDependentData(contact, now);

        assertThat(resultContact.isBirthdayToday(), is(false));
        assertThat(resultContact.isBornInFuture(), is(false));
        assertThat(resultContact.getAgeInYears(), is(1));
        assertThat(resultContact.getAgeInDays(), is(367)); // leapyear 29. February
        assertThat(resultContact.getNextBirthday(), is(now.minusDays(1).plusYears(1)));
        assertThat(resultContact.getDaysUntilNextBirthday(), is(364));
        assertThat(resultContact.getDaysSinceLastBirthday(), is(1));
    }

    @Test
    public void testCalculatedBirthdayInformationMissingYearInfoAfterNow() {
        final LocalDate now = LocalDate.of(2020, 6, 7);
        final Contact contact = new Contact();
        contact.setBornOn(now.plusDays(1));
        contact.setMissingYearInfo(true);

        final Contact resultContact = contactCreator.calculateTimeDependentData(contact, now);

        assertThat(resultContact.isBirthdayToday(), is(false));
        assertThat(resultContact.isBornInFuture(), is(false));
        assertThat(resultContact.getAgeInYears(), is(0));
        assertThat(resultContact.getAgeInDays(), is(0));
        assertThat(resultContact.getNextBirthday(), is(now.plusDays(1)));
        assertThat(resultContact.getDaysUntilNextBirthday(), is(1));
        assertThat(resultContact.getDaysSinceLastBirthday(), is(365)); // leapyear 29. February
    }

    @Test
    public void testCalculatedBirthdayInformationMissingYearInfoBeforeNow() {
        final LocalDate now = LocalDate.of(2020, 6, 7);
        final Contact contact = new Contact();
        contact.setBornOn(now.minusDays(1));
        contact.setMissingYearInfo(true);

        final Contact resultContact = contactCreator.calculateTimeDependentData(contact, now);

        assertThat(resultContact.isBirthdayToday(), is(false));
        assertThat(resultContact.isBornInFuture(), is(false));
        assertThat(resultContact.getAgeInYears(), is(0));
        assertThat(resultContact.getAgeInDays(), is(0));
        assertThat(resultContact.getNextBirthday(), is(now.minusDays(1).plusYears(1)));
        assertThat(resultContact.getDaysUntilNextBirthday(), is(364));
        assertThat(resultContact.getDaysSinceLastBirthday(), is(1));
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
