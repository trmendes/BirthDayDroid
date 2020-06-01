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

public class DateLocalHelperTranslationsTest {

    private DateLocalHelper dateLocalHelper;

    @Before
    public void setUp() {
        dateLocalHelper = spy(DateLocalHelper.class);
    }

    @Test
    public void testGermanMonths() {
        doReturn(Locale.GERMANY).when(dateLocalHelper).getCurrentLocale(nullable(Context.class));

        assertThat(dateLocalHelper.getMonthString(Month.JANUARY, null), is("Januar"));
        assertThat(dateLocalHelper.getMonthString(Month.FEBRUARY, null), is("Februar"));
        assertThat(dateLocalHelper.getMonthString(Month.MARCH, null), is("März"));
        assertThat(dateLocalHelper.getMonthString(Month.APRIL, null), is("April"));
        assertThat(dateLocalHelper.getMonthString(Month.MAY, null), is("Mai"));
        assertThat(dateLocalHelper.getMonthString(Month.JUNE, null), is("Juni"));
        assertThat(dateLocalHelper.getMonthString(Month.JULY, null), is("Juli"));
        assertThat(dateLocalHelper.getMonthString(Month.AUGUST, null), is("August"));
        assertThat(dateLocalHelper.getMonthString(Month.SEPTEMBER, null), is("September"));
        assertThat(dateLocalHelper.getMonthString(Month.OCTOBER, null), is("Oktober"));
        assertThat(dateLocalHelper.getMonthString(Month.NOVEMBER, null), is("November"));
        assertThat(dateLocalHelper.getMonthString(Month.DECEMBER, null), is("Dezember"));
    }

    @Test
    public void testEnglishMonths() {
        doReturn(Locale.US).when(dateLocalHelper).getCurrentLocale(nullable(Context.class));

        assertThat(dateLocalHelper.getMonthString(Month.JANUARY, null), is("January"));
        assertThat(dateLocalHelper.getMonthString(Month.FEBRUARY, null), is("February"));
        assertThat(dateLocalHelper.getMonthString(Month.MARCH, null), is("March"));
        assertThat(dateLocalHelper.getMonthString(Month.APRIL, null), is("April"));
        assertThat(dateLocalHelper.getMonthString(Month.MAY, null), is("May"));
        assertThat(dateLocalHelper.getMonthString(Month.JUNE, null), is("June"));
        assertThat(dateLocalHelper.getMonthString(Month.JULY, null), is("July"));
        assertThat(dateLocalHelper.getMonthString(Month.AUGUST, null), is("August"));
        assertThat(dateLocalHelper.getMonthString(Month.SEPTEMBER, null), is("September"));
        assertThat(dateLocalHelper.getMonthString(Month.OCTOBER, null), is("October"));
        assertThat(dateLocalHelper.getMonthString(Month.NOVEMBER, null), is("November"));
        assertThat(dateLocalHelper.getMonthString(Month.DECEMBER, null), is("December"));
    }

    @Test
    public void testGermanDayOfWeeks() {
        doReturn(Locale.GERMANY).when(dateLocalHelper).getCurrentLocale(nullable(Context.class));

        assertThat(dateLocalHelper.getDayOfWeek(DayOfWeek.MONDAY, null), is("Montag"));
        assertThat(dateLocalHelper.getDayOfWeek(DayOfWeek.TUESDAY, null), is("Dienstag"));
        assertThat(dateLocalHelper.getDayOfWeek(DayOfWeek.WEDNESDAY, null), is("Mittwoch"));
        assertThat(dateLocalHelper.getDayOfWeek(DayOfWeek.THURSDAY, null), is("Donnerstag"));
        assertThat(dateLocalHelper.getDayOfWeek(DayOfWeek.FRIDAY, null), is("Freitag"));
        assertThat(dateLocalHelper.getDayOfWeek(DayOfWeek.SATURDAY, null), is("Samstag"));
        assertThat(dateLocalHelper.getDayOfWeek(DayOfWeek.SUNDAY, null), is("Sonntag"));
    }

    @Test
    public void testEnglishDayOfWeeks() {
        doReturn(Locale.US).when(dateLocalHelper).getCurrentLocale(nullable(Context.class));

        assertThat(dateLocalHelper.getDayOfWeek(DayOfWeek.MONDAY, null), is("Monday"));
        assertThat(dateLocalHelper.getDayOfWeek(DayOfWeek.TUESDAY, null), is("Tuesday"));
        assertThat(dateLocalHelper.getDayOfWeek(DayOfWeek.WEDNESDAY, null), is("Wednesday"));
        assertThat(dateLocalHelper.getDayOfWeek(DayOfWeek.THURSDAY, null), is("Thursday"));
        assertThat(dateLocalHelper.getDayOfWeek(DayOfWeek.FRIDAY, null), is("Friday"));
        assertThat(dateLocalHelper.getDayOfWeek(DayOfWeek.SATURDAY, null), is("Saturday"));
        assertThat(dateLocalHelper.getDayOfWeek(DayOfWeek.SUNDAY, null), is("Sunday"));
    }
}
