package com.tmendes.birthdaydroid.comparators;

import com.tmendes.birthdaydroid.Contact;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Comparator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.mockito.Mockito.mock;

public class DaysUntilBirthdayComparatorTest {
    private Contact contactA;
    private Contact contactB;

    @Before
    public void setUp() {
        contactA = mock(Contact.class);
        contactB = mock(Contact.class);
    }

    @Test
    public void testLessWithPositiveValues() {
        Comparator<Contact> comparatorAsc = new DaysUntilBirthdayComparator();

        Mockito.when(contactA.getDaysUntilNextBirthday()).thenReturn(1);
        Mockito.when(contactB.getDaysUntilNextBirthday()).thenReturn(2);

        assertThat(comparatorAsc.compare(contactA, contactB), lessThan(0));
    }

    @Test
    public void testLessWithNegativeValues() {
        Comparator<Contact> comparatorAsc = new DaysUntilBirthdayComparator();

        Mockito.when(contactA.getDaysUntilNextBirthday()).thenReturn(-2);
        Mockito.when(contactB.getDaysUntilNextBirthday()).thenReturn(-1);

        assertThat(comparatorAsc.compare(contactA, contactB), lessThan(0));
    }

    @Test
    public void testLessMixedValues() {
        Comparator<Contact> comparatorAsc = new DaysUntilBirthdayComparator();

        Mockito.when(contactA.getDaysUntilNextBirthday()).thenReturn(1);
        Mockito.when(contactB.getDaysUntilNextBirthday()).thenReturn(-1);

        assertThat(comparatorAsc.compare(contactA, contactB), lessThan(0));
    }

    @Test
    public void testEquals() {
        Comparator<Contact> comparatorAsc = new DaysUntilBirthdayComparator();

        Mockito.when(contactA.getDaysUntilNextBirthday()).thenReturn(1);
        Mockito.when(contactB.getDaysUntilNextBirthday()).thenReturn(1);

        assertThat(comparatorAsc.compare(contactA, contactB), is(0));
    }

    @Test
    public void testGreaterWithPositiveValues() {
        Comparator<Contact> comparatorAsc = new DaysUntilBirthdayComparator();

        Mockito.when(contactA.getDaysUntilNextBirthday()).thenReturn(2);
        Mockito.when(contactB.getDaysUntilNextBirthday()).thenReturn(1);

        assertThat(comparatorAsc.compare(contactA, contactB), greaterThan(0));
    }

    @Test
    public void testGreaterWithNegativeValues() {
        Comparator<Contact> comparatorAsc = new DaysUntilBirthdayComparator();

        Mockito.when(contactA.getDaysUntilNextBirthday()).thenReturn(-1);
        Mockito.when(contactB.getDaysUntilNextBirthday()).thenReturn(-2);

        assertThat(comparatorAsc.compare(contactA, contactB), greaterThan(0));
    }

    @Test
    public void testGreaterWithMixedValues() {
        Comparator<Contact> comparatorAsc = new DaysUntilBirthdayComparator();

        Mockito.when(contactA.getDaysUntilNextBirthday()).thenReturn(-1);
        Mockito.when(contactB.getDaysUntilNextBirthday()).thenReturn(1);

        assertThat(comparatorAsc.compare(contactA, contactB), greaterThan(0));
    }

    @Test
    public void testTodayBirthdayIsFirst() {
        Comparator<Contact> comparatorAsc = new DaysUntilBirthdayComparator();

        Mockito.when(contactA.getDaysUntilNextBirthday()).thenReturn(0);
        Mockito.when(contactB.getDaysUntilNextBirthday()).thenReturn(-1);

        assertThat(comparatorAsc.compare(contactA, contactB), lessThan(0));

        Mockito.when(contactA.getDaysUntilNextBirthday()).thenReturn(0);
        Mockito.when(contactB.getDaysUntilNextBirthday()).thenReturn(1);

        assertThat(comparatorAsc.compare(contactA, contactB), lessThan(0));
    }
}