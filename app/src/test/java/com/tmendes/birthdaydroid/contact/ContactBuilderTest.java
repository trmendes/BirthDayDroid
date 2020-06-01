package com.tmendes.birthdaydroid.contact;

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

public class ContactBuilderTest {

    private ZodiacCalculator zodiacCalculator;
    private DateConverter dateConverter;
    private ContactBuilder contactBuilder;

    @Before
    public void setUp() throws Exception {
        zodiacCalculator = mock(ZodiacCalculator.class);
        dateConverter = mock(DateConverter.class);
        contactBuilder = new ContactBuilder(zodiacCalculator, dateConverter);
    }

    @Test
    public void testDbIdNullException() {
        this.contactBuilder = this.contactBuilder
                .setDbId(null)
                .setKey("key")
                .setName("Name Name")
                .setPhotoUri("uri")
                .setEventTypeLabel("label")
                .setCustomEventTypeLabel(true)
                .setBirthdayString("2020-01-01")
                .setFavorite(false)
                .setIgnore(false);

        Assert.assertThrows(ContactBuilderException.class, () -> this.contactBuilder.build());
    }

    @Test
    public void testKeyNullException() {
        this.contactBuilder = this.contactBuilder
                .setDbId(-1L)
                .setKey(null)
                .setName("Name Name")
                .setPhotoUri("uri")
                .setEventTypeLabel("label")
                .setCustomEventTypeLabel(true)
                .setBirthdayString("2020-01-01")
                .setFavorite(false)
                .setIgnore(false);

        Assert.assertThrows(ContactBuilderException.class, () -> this.contactBuilder.build());
    }

    @Test
    public void testNameNullException() {
        this.contactBuilder = this.contactBuilder
                .setDbId(-1L)
                .setKey("key")
                .setName(null)
                .setPhotoUri("uri")
                .setEventTypeLabel("label")
                .setCustomEventTypeLabel(true)
                .setBirthdayString("2020-01-01")
                .setFavorite(false)
                .setIgnore(false);

        Assert.assertThrows(ContactBuilderException.class, () -> this.contactBuilder.build());
    }

    @Test
    public void testEventTypeLabelNullException() {
        this.contactBuilder = this.contactBuilder
                .setDbId(-1L)
                .setKey("key")
                .setName("Name Name")
                .setPhotoUri("uri")
                .setEventTypeLabel(null)
                .setCustomEventTypeLabel(true)
                .setBirthdayString("2020-01-01")
                .setFavorite(false)
                .setIgnore(false);

        Assert.assertThrows(ContactBuilderException.class, () -> this.contactBuilder.build());
    }

    @Test
    public void testCustomEventTypeLabelNullException() {
        this.contactBuilder = this.contactBuilder
                .setDbId(-1L)
                .setKey("key")
                .setName("Name Name")
                .setPhotoUri("uri")
                .setEventTypeLabel("label")
                .setCustomEventTypeLabel(null)
                .setBirthdayString("2020-01-01")
                .setFavorite(false)
                .setIgnore(false);

        Assert.assertThrows(ContactBuilderException.class, () -> this.contactBuilder.build());
    }

    @Test
    public void testBirthdayStringNullException() {
        this.contactBuilder = this.contactBuilder
                .setDbId(-1L)
                .setKey("key")
                .setName("Name Name")
                .setPhotoUri("uri")
                .setEventTypeLabel("label")
                .setCustomEventTypeLabel(true)
                .setBirthdayString(null)
                .setFavorite(false)
                .setIgnore(false);

        Assert.assertThrows(ContactBuilderException.class, () -> this.contactBuilder.build());
    }

    @Test
    public void testFavoriteNullException() {
        this.contactBuilder = this.contactBuilder
                .setDbId(-1L)
                .setKey("key")
                .setName("Name Name")
                .setPhotoUri("uri")
                .setEventTypeLabel("label")
                .setCustomEventTypeLabel(true)
                .setBirthdayString("2020-01-01")
                .setFavorite(null)
                .setIgnore(false);

        Assert.assertThrows(ContactBuilderException.class, () -> this.contactBuilder.build());
    }

    @Test
    public void testIgnoreNullException() {
        this.contactBuilder = this.contactBuilder
                .setDbId(-1L)
                .setKey("key")
                .setName("Name Name")
                .setPhotoUri("uri")
                .setEventTypeLabel("label")
                .setCustomEventTypeLabel(true)
                .setBirthdayString("2020-01-01")
                .setFavorite(false)
                .setIgnore(null);

        Assert.assertThrows(ContactBuilderException.class, () -> this.contactBuilder.build());
    }

    @Test
    public void testDateConvertingFails() {
        doReturn(DateConverter.DateConverterResult.createNotSuccess()).when(dateConverter).convert(nullable(String.class));

        this.contactBuilder = this.contactBuilder
                .setDbId(-1L)
                .setKey("key")
                .setName("Name Name")
                .setPhotoUri("uri")
                .setEventTypeLabel("label")
                .setCustomEventTypeLabel(true)
                .setBirthdayString("2020-01-01")
                .setFavorite(false)
                .setIgnore(false);

        Assert.assertThrows(ContactBuilderException.class, () -> this.contactBuilder.build());
    }

    @Test
    public void testSuccessfulCreation() {
        LocalDate bornOn = LocalDate.of(2019, 1, 1);

        doReturn(DateConverter.DateConverterResult.createSuccess(false, bornOn))
                .when(dateConverter).convert(any(String.class));
        doReturn(Zodiac.CAPRICORN).when(zodiacCalculator).calculateZodiac(any(LocalDate.class));

        Contact contact = this.contactBuilder
                .setDbId(-1L)
                .setKey("key")
                .setName("Name Name")
                .setPhotoUri("uri")
                .setEventTypeLabel("label")
                .setCustomEventTypeLabel(true)
                .setBirthdayString("2019-01-01")
                .setFavorite(false)
                .setIgnore(false)
                .build();

        assertThat(contact.getDbId(), is(-1L));
        assertThat(contact.getKey(), is("key"));
        assertThat(contact.getName(), is("Name Name"));
        assertThat(contact.getPhotoUri(), is("uri"));
        assertThat(contact.getEventTypeLabel(), is("label"));
        assertThat(contact.isCustomEventTypeLabel(), is(true));
        assertThat(contact.isFavorite(), is(false));
        assertThat(contact.isIgnore(), is(false));

        LocalDate nextBirthDay = bornOn.withYear(Year.now().getValue());
        if(nextBirthDay.isBefore(LocalDate.now())){
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
        LocalDate bornOn = LocalDate.now().plusDays(2);

        doReturn(DateConverter.DateConverterResult.createSuccess(false, bornOn))
                .when(dateConverter).convert(any(String.class));
        doReturn(Zodiac.CAPRICORN).when(zodiacCalculator).calculateZodiac(any(LocalDate.class));

        Contact contact = this.contactBuilder
                .setDbId(-1L)
                .setKey("key")
                .setName("Name Name")
                .setPhotoUri("uri")
                .setEventTypeLabel("label")
                .setCustomEventTypeLabel(true)
                .setBirthdayString("2019-01-01")
                .setFavorite(false)
                .setIgnore(false)
                .build();

        assertThat(contact.getDbId(), is(-1L));
        assertThat(contact.getKey(), is("key"));
        assertThat(contact.getName(), is("Name Name"));
        assertThat(contact.getPhotoUri(), is("uri"));
        assertThat(contact.getEventTypeLabel(), is("label"));
        assertThat(contact.isCustomEventTypeLabel(), is(true));
        assertThat(contact.isFavorite(), is(false));
        assertThat(contact.isIgnore(), is(false));

        LocalDate nextBirthDay = bornOn.withYear(Year.now().getValue());
        if(nextBirthDay.isBefore(LocalDate.now())){
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
}
