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
import java.time.Year;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class ContactFactoryTest {

    private ZodiacCalculator zodiacCalculator;
    private DateConverter dateConverter;
    private EventTypeLabelService eventTypeLabelService;
    private ContactFactory contactFactory;

    @Before
    public void setUp() {
        zodiacCalculator = mock(ZodiacCalculator.class);
        dateConverter = mock(DateConverter.class);
        eventTypeLabelService = mock(EventTypeLabelService.class);
        contactFactory = new ContactFactory(zodiacCalculator, dateConverter, eventTypeLabelService);
    }

    @Test
    public void testAndroidContactNullException() {
        final DBContact dbContact = new DBContact(
                1,
                false,
                false
        );
        Assert.assertThrows(
                ContactBuilderException.class,
                () -> contactFactory.createContact(null, dbContact)
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
                ContactBuilderException.class,
                () -> contactFactory.createContact(androidContact, dbContact)
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
                ContactBuilderException.class,
                () -> contactFactory.createContact(androidContact, dbContact)
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
                ContactBuilderException.class,
                () -> contactFactory.createContact(androidContact, dbContact)
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
                ContactBuilderException.class,
                () -> contactFactory.createContact(androidContact, dbContact)
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

        final Contact contact = contactFactory.createContact(androidContact, dbContact);

        assertThat(contact.getDbId(), is(1L));
        assertThat(contact.getKey(), is("key"));
        assertThat(contact.getName(), is("Name Name"));
        assertThat(contact.getPhotoUri(), is("uri"));
        assertThat(contact.getEventTypeLabel(), is("Birthday"));
        assertThat(contact.isFavorite(), is(false));
        assertThat(contact.isIgnore(), is(false));

        LocalDate nextBirthDay = bornOn.withYear(Year.now().getValue());
        if (nextBirthDay.isBefore(LocalDate.now())) {
            nextBirthDay = nextBirthDay.plusYears(1);
        }

        assertThat(contact.getBornOn(), is(bornOn));
        assertThat(contact.isMissingYearInfo(), is(false));
        assertThat(contact.getNextBirthday(), is(nextBirthDay));
        assertThat(contact.getAge(), is((int) ChronoUnit.YEARS.between(bornOn, LocalDate.now())));
        assertThat(contact.getDaysOld(), is((int) ChronoUnit.DAYS.between(bornOn, LocalDate.now())));
        assertThat(contact.getDaysUntilNextBirthday(), is((int) ChronoUnit.DAYS.between(LocalDate.now(), nextBirthDay)));
        assertThat(contact.getDaysSinceLastBirthday(), is((int) ChronoUnit.DAYS.between(nextBirthDay.minusYears(1), LocalDate.now())));
        assertThat(contact.getZodiac(), is(Zodiac.CAPRICORN));
    }

    @Test
    public void testSuccessfulBornInFutureCreation() {
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

        LocalDate bornOn = LocalDate.now().plusDays(2);
        instrumentDependencies(
                DateConverter.DateConverterResult.createSuccess(false, bornOn),
                "Birthday",
                Zodiac.CAPRICORN
        );

        final Contact contact = contactFactory.createContact(androidContact, dbContact);

        assertThat(contact.getDbId(), is(1L));
        assertThat(contact.getKey(), is("key"));
        assertThat(contact.getName(), is("Name Name"));
        assertThat(contact.getPhotoUri(), is("uri"));
        assertThat(contact.getEventTypeLabel(), is("Birthday"));
        assertThat(contact.isFavorite(), is(false));
        assertThat(contact.isIgnore(), is(false));

        LocalDate nextBirthDay = bornOn.withYear(Year.now().getValue());
        if (nextBirthDay.isBefore(LocalDate.now())) {
            nextBirthDay = nextBirthDay.plusYears(1);
        }

        assertThat(contact.getBornOn(), is(bornOn));
        assertThat(contact.isMissingYearInfo(), is(false));
        assertThat(contact.getNextBirthday(), is(nextBirthDay));
        assertThat(contact.getAge(), is(0));
        assertThat(contact.getDaysOld(), is(0));
        assertThat(contact.getDaysUntilNextBirthday(), is((int) ChronoUnit.DAYS.between(LocalDate.now(), nextBirthDay)));
        assertThat(contact.getDaysSinceLastBirthday(), is(0));
        assertThat(contact.getZodiac(), is(Zodiac.CAPRICORN));
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

        final Contact contact = contactFactory.createContact(androidContact, null);

        assertThat(contact.getDbId(), is(-1L));
        assertThat(contact.getKey(), is("key"));
        assertThat(contact.getName(), is("Name Name"));
        assertThat(contact.getPhotoUri(), is("uri"));
        assertThat(contact.getEventTypeLabel(), is("Birthday"));
        assertThat(contact.isFavorite(), is(false));
        assertThat(contact.isIgnore(), is(false));

        LocalDate nextBirthDay = bornOn.withYear(Year.now().getValue());
        if (nextBirthDay.isBefore(LocalDate.now())) {
            nextBirthDay = nextBirthDay.plusYears(1);
        }

        assertThat(contact.getBornOn(), is(bornOn));
        assertThat(contact.isMissingYearInfo(), is(false));
        assertThat(contact.getNextBirthday(), is(nextBirthDay));
        assertThat(contact.getAge(), is((int) ChronoUnit.YEARS.between(bornOn, LocalDate.now())));
        assertThat(contact.getDaysOld(), is((int) ChronoUnit.DAYS.between(bornOn, LocalDate.now())));
        assertThat(contact.getDaysUntilNextBirthday(), is((int) ChronoUnit.DAYS.between(LocalDate.now(), nextBirthDay)));
        assertThat(contact.getDaysSinceLastBirthday(), is((int) ChronoUnit.DAYS.between(nextBirthDay.minusYears(1), LocalDate.now())));
        assertThat(contact.getZodiac(), is(Zodiac.CAPRICORN));
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
