package com.tmendes.birthdaydroid.comparators;

import com.tmendes.birthdaydroid.Contact;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Comparator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;

public class AgeComparatorTest {
    private Contact contactA;
    private Contact contactB;

    @Before
    public void setUp() {
        contactA = mock(Contact.class);
        contactB = mock(Contact.class);
    }

    @Test
    public void testLess() {
        Comparator<Contact> comparatorAsc = new AgeComparator();

        Mockito.when(contactA.getBornOn()).thenReturn(createLocalDate(2020, 1, 2));
        Mockito.when(contactB.getBornOn()).thenReturn(createLocalDate(2020, 1, 1));

        assertThat(comparatorAsc.compare(contactA, contactB), lessThan(0));
    }

    @Test
    public void testEquals() {
        Comparator<Contact> comparatorAsc = new AgeComparator();

        Mockito.when(contactA.getBornOn()).thenReturn(createLocalDate(2020, 1, 1));
        Mockito.when(contactB.getBornOn()).thenReturn(createLocalDate(2020, 1, 1));

        assertThat(comparatorAsc.compare(contactA, contactB), is(0));
    }

    @Test
    public void testGreater() {
        Comparator<Contact> comparatorAsc = new AgeComparator();

        Mockito.when(contactA.getBornOn()).thenReturn(createLocalDate(2020, 1, 1));
        Mockito.when(contactB.getBornOn()).thenReturn(createLocalDate(2020, 1, 2));

        assertThat(comparatorAsc.compare(contactA, contactB), greaterThan(0));
    }

    /**
     * @param year  year
     * @param month 1-12
     * @param day   1-31
     */
    private LocalDate createLocalDate(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }
}
