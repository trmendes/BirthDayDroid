package com.tmendes.birthdaydroid.date;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class DateLocaleHelperTranslationsTest {

    private DateLocaleHelper dateLocaleHelper;

    @Before
    public void setUp() {
        dateLocaleHelper = spy(DateLocaleHelper.class);
    }

    @Test
    public void testGermanMonths() {
        doReturn(Locale.GERMANY).when(dateLocaleHelper).getCurrentLocale(nullable(Context.class));

        assertThat(dateLocaleHelper.getMonthString(Month.JANUARY, null), is("Januar"));
        assertThat(dateLocaleHelper.getMonthString(Month.FEBRUARY, null), is("Februar"));
        assertThat(dateLocaleHelper.getMonthString(Month.MARCH, null), is("März"));
        assertThat(dateLocaleHelper.getMonthString(Month.APRIL, null), is("April"));
        assertThat(dateLocaleHelper.getMonthString(Month.MAY, null), is("Mai"));
        assertThat(dateLocaleHelper.getMonthString(Month.JUNE, null), is("Juni"));
        assertThat(dateLocaleHelper.getMonthString(Month.JULY, null), is("Juli"));
        assertThat(dateLocaleHelper.getMonthString(Month.AUGUST, null), is("August"));
        assertThat(dateLocaleHelper.getMonthString(Month.SEPTEMBER, null), is("September"));
        assertThat(dateLocaleHelper.getMonthString(Month.OCTOBER, null), is("Oktober"));
        assertThat(dateLocaleHelper.getMonthString(Month.NOVEMBER, null), is("November"));
        assertThat(dateLocaleHelper.getMonthString(Month.DECEMBER, null), is("Dezember"));
    }

    @Test
    public void testEnglishMonths() {
        doReturn(Locale.US).when(dateLocaleHelper).getCurrentLocale(nullable(Context.class));

        assertThat(dateLocaleHelper.getMonthString(Month.JANUARY, null), is("January"));
        assertThat(dateLocaleHelper.getMonthString(Month.FEBRUARY, null), is("February"));
        assertThat(dateLocaleHelper.getMonthString(Month.MARCH, null), is("March"));
        assertThat(dateLocaleHelper.getMonthString(Month.APRIL, null), is("April"));
        assertThat(dateLocaleHelper.getMonthString(Month.MAY, null), is("May"));
        assertThat(dateLocaleHelper.getMonthString(Month.JUNE, null), is("June"));
        assertThat(dateLocaleHelper.getMonthString(Month.JULY, null), is("July"));
        assertThat(dateLocaleHelper.getMonthString(Month.AUGUST, null), is("August"));
        assertThat(dateLocaleHelper.getMonthString(Month.SEPTEMBER, null), is("September"));
        assertThat(dateLocaleHelper.getMonthString(Month.OCTOBER, null), is("October"));
        assertThat(dateLocaleHelper.getMonthString(Month.NOVEMBER, null), is("November"));
        assertThat(dateLocaleHelper.getMonthString(Month.DECEMBER, null), is("December"));
    }

    @Test
    public void testGermanDayOfWeeks() {
        doReturn(Locale.GERMANY).when(dateLocaleHelper).getCurrentLocale(nullable(Context.class));

        assertThat(dateLocaleHelper.getDayOfWeek(DayOfWeek.MONDAY, null), is("Montag"));
        assertThat(dateLocaleHelper.getDayOfWeek(DayOfWeek.TUESDAY, null), is("Dienstag"));
        assertThat(dateLocaleHelper.getDayOfWeek(DayOfWeek.WEDNESDAY, null), is("Mittwoch"));
        assertThat(dateLocaleHelper.getDayOfWeek(DayOfWeek.THURSDAY, null), is("Donnerstag"));
        assertThat(dateLocaleHelper.getDayOfWeek(DayOfWeek.FRIDAY, null), is("Freitag"));
        assertThat(dateLocaleHelper.getDayOfWeek(DayOfWeek.SATURDAY, null), is("Samstag"));
        assertThat(dateLocaleHelper.getDayOfWeek(DayOfWeek.SUNDAY, null), is("Sonntag"));
    }

    @Test
    public void testEnglishDayOfWeeks() {
        doReturn(Locale.US).when(dateLocaleHelper).getCurrentLocale(nullable(Context.class));

        assertThat(dateLocaleHelper.getDayOfWeek(DayOfWeek.MONDAY, null), is("Monday"));
        assertThat(dateLocaleHelper.getDayOfWeek(DayOfWeek.TUESDAY, null), is("Tuesday"));
        assertThat(dateLocaleHelper.getDayOfWeek(DayOfWeek.WEDNESDAY, null), is("Wednesday"));
        assertThat(dateLocaleHelper.getDayOfWeek(DayOfWeek.THURSDAY, null), is("Thursday"));
        assertThat(dateLocaleHelper.getDayOfWeek(DayOfWeek.FRIDAY, null), is("Friday"));
        assertThat(dateLocaleHelper.getDayOfWeek(DayOfWeek.SATURDAY, null), is("Saturday"));
        assertThat(dateLocaleHelper.getDayOfWeek(DayOfWeek.SUNDAY, null), is("Sunday"));
    }
}
