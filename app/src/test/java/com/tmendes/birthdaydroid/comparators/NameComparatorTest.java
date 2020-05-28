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

public class NameComparatorTest {
    private Contact contactA;
    private Contact contactB;

    @Before
    public void setUp() {
        contactA = mock(Contact.class);
        contactB = mock(Contact.class);
    }

    @Test
    public void testLess() {
        Comparator<Contact> comparatorAsc = new NameComparator();

        Mockito.when(contactA.getName()).thenReturn("a");
        Mockito.when(contactB.getName()).thenReturn("b");

        assertThat(comparatorAsc.compare(contactA, contactB), lessThan(0));
    }

    @Test
    public void testEquals() {
        Comparator<Contact> comparatorAsc = new NameComparator();

        Mockito.when(contactA.getName()).thenReturn("a");
        Mockito.when(contactB.getName()).thenReturn("a");

        assertThat(comparatorAsc.compare(contactA, contactB), is(0));
    }

    @Test
    public void testGreater() {
        Comparator<Contact> comparatorAsc = new NameComparator();

        Mockito.when(contactA.getName()).thenReturn("b");
        Mockito.when(contactB.getName()).thenReturn("a");

        assertThat(comparatorAsc.compare(contactA, contactB), greaterThan(0));
    }

    @Test
    public void testEqualsCaseInsensitive() {
        Comparator<Contact> comparatorAsc = new NameComparator();

        Mockito.when(contactA.getName()).thenReturn("A");
        Mockito.when(contactB.getName()).thenReturn("a");

        assertThat(comparatorAsc.compare(contactA, contactB), is(0));
    }
}
