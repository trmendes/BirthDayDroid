package com.tmendes.birthdaydroid.comparators;

import com.tmendes.birthdaydroid.Contact;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Comparator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static com.tmendes.birthdaydroid.comparators.BirthDayComparator.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.mockito.Mockito.mock;

public class BirthDayComparatorTest {

    private Contact contactA;
    private Contact contactB;

    @Before
    public void setUp() {
        contactA = mock(Contact.class);
        contactB = mock(Contact.class);
    }

    @Test
    public void testCompareByNameLess() {
        Comparator<Contact> comparatorAsc = new BirthDayComparator(SORT_TYPE_NAME, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = new BirthDayComparator(SORT_TYPE_NAME, SORT_ORDER_DESC);

        Mockito.when(contactA.getName()).thenReturn("a");
        Mockito.when(contactB.getName()).thenReturn("b");

        assertThat(comparatorAsc.compare(contactA, contactB), lessThan(0));
        assertThat(comparatorDesc.compare(contactA, contactB), greaterThan(0));
    }

    @Test
    public void testCompareByNameEquals() {
        Comparator<Contact> comparatorAsc = new BirthDayComparator(SORT_TYPE_NAME, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = new BirthDayComparator(SORT_TYPE_NAME, SORT_ORDER_DESC);

        Mockito.when(contactA.getName()).thenReturn("a");
        Mockito.when(contactB.getName()).thenReturn("a");

        assertThat(comparatorAsc.compare(contactA, contactB), is(0));
        assertThat(comparatorDesc.compare(contactA, contactB), is(0));
    }

    @Test
    public void testCompareByNameGreater() {
        Comparator<Contact> comparatorAsc = new BirthDayComparator(SORT_TYPE_NAME, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = new BirthDayComparator(SORT_TYPE_NAME, SORT_ORDER_DESC);

        Mockito.when(contactA.getName()).thenReturn("b");
        Mockito.when(contactB.getName()).thenReturn("a");

        assertThat(comparatorAsc.compare(contactA, contactB), greaterThan(0));
        assertThat(comparatorDesc.compare(contactA, contactB), lessThan(0));
    }

    @Test
    public void testCompareByNameCaseInsensitive() {
        Comparator<Contact> comparatorAsc = new BirthDayComparator(SORT_TYPE_NAME, SORT_ORDER_ASC);
        Comparator<Contact> comparatorDesc = new BirthDayComparator(SORT_TYPE_NAME, SORT_ORDER_DESC);

        Mockito.when(contactA.getName()).thenReturn("A");
        Mockito.when(contactB.getName()).thenReturn("a");

        assertThat(comparatorAsc.compare(contactA, contactB), is(0));
        assertThat(comparatorDesc.compare(contactA, contactB), is(0));
    }
}
