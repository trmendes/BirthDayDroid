package com.tmendes.birthdaydroid.comparators;

import com.tmendes.birthdaydroid.Contact;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Calendar;
import java.util.Comparator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static com.tmendes.birthdaydroid.comparators.BirthDayComparatorFactory.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.mockito.Mockito.mock;

public class BirthDayComparatorTest {

    private Contact contactA;
    private Contact contactB;
    private BirthDayComparatorFactory factory;

    @Before
    public void setUp() {
        contactA = mock(Contact.class);
        contactB = mock(Contact.class);
        factory = new BirthDayComparatorFactory();
    }

    @Test
    public void testCompareByNameLess() {
        Comparator<Contact> comparatorAsc = factory.createBirthdayComparator(SORT_TYPE_NAME, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = factory.createBirthdayComparator(SORT_TYPE_NAME, SORT_ORDER_DESC);

        Mockito.when(contactA.getName()).thenReturn("a");
        Mockito.when(contactB.getName()).thenReturn("b");

        assertThat(comparatorAsc.compare(contactA, contactB), lessThan(0));
        assertThat(comparatorDesc.compare(contactA, contactB), greaterThan(0));
    }

    @Test
    public void testCompareByNameEquals() {
        Comparator<Contact> comparatorAsc = factory.createBirthdayComparator(SORT_TYPE_NAME, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = factory.createBirthdayComparator(SORT_TYPE_NAME, SORT_ORDER_DESC);

        Mockito.when(contactA.getName()).thenReturn("a");
        Mockito.when(contactB.getName()).thenReturn("a");

        assertThat(comparatorAsc.compare(contactA, contactB), is(0));
        assertThat(comparatorDesc.compare(contactA, contactB), is(0));
    }

    @Test
    public void testCompareByNameGreater() {
        Comparator<Contact> comparatorAsc = factory.createBirthdayComparator(SORT_TYPE_NAME, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = factory.createBirthdayComparator(SORT_TYPE_NAME, SORT_ORDER_DESC);

        Mockito.when(contactA.getName()).thenReturn("b");
        Mockito.when(contactB.getName()).thenReturn("a");

        assertThat(comparatorAsc.compare(contactA, contactB), greaterThan(0));
        assertThat(comparatorDesc.compare(contactA, contactB), lessThan(0));
    }

    @Test
    public void testCompareByNameCaseInsensitive() {
        Comparator<Contact> comparatorAsc = factory.createBirthdayComparator(SORT_TYPE_NAME, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = factory.createBirthdayComparator(SORT_TYPE_NAME, SORT_ORDER_DESC);

        Mockito.when(contactA.getName()).thenReturn("A");
        Mockito.when(contactB.getName()).thenReturn("a");

        assertThat(comparatorAsc.compare(contactA, contactB), is(0));
        assertThat(comparatorDesc.compare(contactA, contactB), is(0));
    }

    @Test
    public void testCompareByZodiacLess() {
        Comparator<Contact> comparatorAsc = factory.createBirthdayComparator(SORT_TYPE_ZODIAC, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = factory.createBirthdayComparator(SORT_TYPE_ZODIAC, SORT_ORDER_DESC);

        Mockito.when(contactA.getZodiacName()).thenReturn("a");
        Mockito.when(contactB.getZodiacName()).thenReturn("b");

        assertThat(comparatorAsc.compare(contactA, contactB), lessThan(0));
        assertThat(comparatorDesc.compare(contactA, contactB), greaterThan(0));
    }

    @Test
    public void testCompareByZodiacEquals() {
        Comparator<Contact> comparatorAsc = factory.createBirthdayComparator(SORT_TYPE_ZODIAC, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = factory.createBirthdayComparator(SORT_TYPE_ZODIAC, SORT_ORDER_DESC);

        Mockito.when(contactA.getZodiacName()).thenReturn("a");
        Mockito.when(contactB.getZodiacName()).thenReturn("a");

        assertThat(comparatorAsc.compare(contactA, contactB), is(0));
        assertThat(comparatorDesc.compare(contactA, contactB), is(0));
    }

    @Test
    public void testCompareByZodiacGreater() {
        Comparator<Contact> comparatorAsc = factory.createBirthdayComparator(SORT_TYPE_ZODIAC, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = factory.createBirthdayComparator(SORT_TYPE_ZODIAC, SORT_ORDER_DESC);

        Mockito.when(contactA.getZodiacName()).thenReturn("b");
        Mockito.when(contactB.getZodiacName()).thenReturn("a");

        assertThat(comparatorAsc.compare(contactA, contactB), greaterThan(0));
        assertThat(comparatorDesc.compare(contactA, contactB), lessThan(0));
    }

    @Test
    public void testCompareByAgeLess() {
        Comparator<Contact> comparatorAsc = factory.createBirthdayComparator(SORT_TYPE_AGE, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = factory.createBirthdayComparator(SORT_TYPE_AGE, SORT_ORDER_DESC);

        Mockito.when(contactA.getBornOn()).thenReturn(createCalendarWithDate(2020, 1, 2));
        Mockito.when(contactB.getBornOn()).thenReturn(createCalendarWithDate(2020, 1, 1));

        assertThat(comparatorAsc.compare(contactA, contactB), lessThan(0));
        assertThat(comparatorDesc.compare(contactA, contactB), greaterThan(0));
    }

    @Test
    public void testCompareByAgeEquals() {
        Comparator<Contact> comparatorAsc = factory.createBirthdayComparator(SORT_TYPE_AGE, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = factory.createBirthdayComparator(SORT_TYPE_AGE, SORT_ORDER_DESC);

        Mockito.when(contactA.getBornOn()).thenReturn(createCalendarWithDate(2020, 1, 1));
        Mockito.when(contactB.getBornOn()).thenReturn(createCalendarWithDate(2020, 1, 1));

        assertThat(comparatorAsc.compare(contactA, contactB), is(0));
        assertThat(comparatorDesc.compare(contactA, contactB), is(0));
    }

    @Test
    public void testCompareByAgeGreater() {
        Comparator<Contact> comparatorAsc = factory.createBirthdayComparator(SORT_TYPE_AGE, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = factory.createBirthdayComparator(SORT_TYPE_AGE, SORT_ORDER_DESC);

        Mockito.when(contactA.getBornOn()).thenReturn(createCalendarWithDate(2020, 1, 1));
        Mockito.when(contactB.getBornOn()).thenReturn(createCalendarWithDate(2020, 1, 2));

        assertThat(comparatorAsc.compare(contactA, contactB), greaterThan(0));
        assertThat(comparatorDesc.compare(contactA, contactB), lessThan(0));
    }

    @Test
    public void testCompareByDaysUntilNextBirthdayLessPositive() {
        Comparator<Contact> comparatorAsc = factory.createBirthdayComparator(SORT_TYPE_DAYS_UNTIL_BIRTHDAY, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = factory.createBirthdayComparator(SORT_TYPE_DAYS_UNTIL_BIRTHDAY, SORT_ORDER_DESC);

        Mockito.when(contactA.getDaysUntilNextBirthday()).thenReturn(1);
        Mockito.when(contactB.getDaysUntilNextBirthday()).thenReturn(2);

        assertThat(comparatorAsc.compare(contactA, contactB), lessThan(0));
        assertThat(comparatorDesc.compare(contactA, contactB), greaterThan(0));
    }

    @Test
    public void testCompareByDaysUntilNextBirthdayLessNegative() {
        Comparator<Contact> comparatorAsc = factory.createBirthdayComparator(SORT_TYPE_DAYS_UNTIL_BIRTHDAY, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = factory.createBirthdayComparator(SORT_TYPE_DAYS_UNTIL_BIRTHDAY, SORT_ORDER_DESC);

        Mockito.when(contactA.getDaysUntilNextBirthday()).thenReturn(-2);
        Mockito.when(contactB.getDaysUntilNextBirthday()).thenReturn(-1);

        assertThat(comparatorAsc.compare(contactA, contactB), lessThan(0));
        assertThat(comparatorDesc.compare(contactA, contactB), greaterThan(0));
    }

    @Test
    public void testCompareByDaysUntilNextBirthdayLessNegativeAndPositive() {
        Comparator<Contact> comparatorAsc = factory.createBirthdayComparator(SORT_TYPE_DAYS_UNTIL_BIRTHDAY, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = factory.createBirthdayComparator(SORT_TYPE_DAYS_UNTIL_BIRTHDAY, SORT_ORDER_DESC);

        Mockito.when(contactA.getDaysUntilNextBirthday()).thenReturn(1);
        Mockito.when(contactB.getDaysUntilNextBirthday()).thenReturn(-1);

        assertThat(comparatorAsc.compare(contactA, contactB), lessThan(0));
        assertThat(comparatorDesc.compare(contactA, contactB), greaterThan(0));
    }

    @Test
    public void testCompareByDaysUntilNextBirthdayOnBirthdayFirst() {
        Comparator<Contact> comparatorAsc = factory.createBirthdayComparator(SORT_TYPE_DAYS_UNTIL_BIRTHDAY, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = factory.createBirthdayComparator(SORT_TYPE_DAYS_UNTIL_BIRTHDAY, SORT_ORDER_DESC);

        Mockito.when(contactA.getDaysUntilNextBirthday()).thenReturn(0);
        Mockito.when(contactB.getDaysUntilNextBirthday()).thenReturn(-1);

        assertThat(comparatorAsc.compare(contactA, contactB), lessThan(0));
        assertThat(comparatorDesc.compare(contactA, contactB), greaterThan(0));

        Mockito.when(contactA.getDaysUntilNextBirthday()).thenReturn(0);
        Mockito.when(contactB.getDaysUntilNextBirthday()).thenReturn(1);

        assertThat(comparatorAsc.compare(contactA, contactB), lessThan(0));
        assertThat(comparatorDesc.compare(contactA, contactB), greaterThan(0));
    }

    @Test
    public void testCompareByDaysUntilNextBirthdayEquals() {
        Comparator<Contact> comparatorAsc = factory.createBirthdayComparator(SORT_TYPE_DAYS_UNTIL_BIRTHDAY, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = factory.createBirthdayComparator(SORT_TYPE_DAYS_UNTIL_BIRTHDAY, SORT_ORDER_DESC);

        Mockito.when(contactA.getDaysUntilNextBirthday()).thenReturn(1);
        Mockito.when(contactB.getDaysUntilNextBirthday()).thenReturn(1);

        assertThat(comparatorAsc.compare(contactA, contactB), is(0));
        assertThat(comparatorDesc.compare(contactA, contactB), is(0));
    }

    @Test
    public void testCompareByDaysUntilNextBirthdayGreaterPositive() {
        Comparator<Contact> comparatorAsc = factory.createBirthdayComparator(SORT_TYPE_DAYS_UNTIL_BIRTHDAY, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = factory.createBirthdayComparator(SORT_TYPE_DAYS_UNTIL_BIRTHDAY, SORT_ORDER_DESC);

        Mockito.when(contactA.getDaysUntilNextBirthday()).thenReturn(2);
        Mockito.when(contactB.getDaysUntilNextBirthday()).thenReturn(1);

        assertThat(comparatorAsc.compare(contactA, contactB), greaterThan(0));
        assertThat(comparatorDesc.compare(contactA, contactB), lessThan(0));
    }

    @Test
    public void testCompareByDaysUntilNextBirthdayGreaterNegative() {
        Comparator<Contact> comparatorAsc = factory.createBirthdayComparator(SORT_TYPE_DAYS_UNTIL_BIRTHDAY, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = factory.createBirthdayComparator(SORT_TYPE_DAYS_UNTIL_BIRTHDAY, SORT_ORDER_DESC);

        Mockito.when(contactA.getDaysUntilNextBirthday()).thenReturn(-1);
        Mockito.when(contactB.getDaysUntilNextBirthday()).thenReturn(-2);

        assertThat(comparatorAsc.compare(contactA, contactB), greaterThan(0));
        assertThat(comparatorDesc.compare(contactA, contactB), lessThan(0));
    }

    @Test
    public void testCompareByDaysUntilNextBirthdayGreaterNegativeAndPositive() {
        Comparator<Contact> comparatorAsc = factory.createBirthdayComparator(SORT_TYPE_DAYS_UNTIL_BIRTHDAY, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = factory.createBirthdayComparator(SORT_TYPE_DAYS_UNTIL_BIRTHDAY, SORT_ORDER_DESC);

        Mockito.when(contactA.getDaysUntilNextBirthday()).thenReturn(-1);
        Mockito.when(contactB.getDaysUntilNextBirthday()).thenReturn(1);

        assertThat(comparatorAsc.compare(contactA, contactB), greaterThan(0));
        assertThat(comparatorDesc.compare(contactA, contactB), lessThan(0));
    }

    /**
     * @param year  year
     * @param month 1-12
     * @param day   1-31
     */
    private Calendar createCalendarWithDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
