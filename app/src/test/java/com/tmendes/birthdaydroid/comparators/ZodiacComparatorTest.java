package com.tmendes.birthdaydroid.comparators;

import com.tmendes.birthdaydroid.contact.Contact;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.mockito.Mockito.mock;

public class ZodiacComparatorTest {

    private Contact contactA;
    private Contact contactB;

    @Before
    public void setUp() {
        contactA = mock(Contact.class);
        contactB = mock(Contact.class);
    }

    @Test
    public void testLess() {
        ZodiacComparator comparatorAsc = new ZodiacComparator();

        Mockito.when(contactA.getZodiacName()).thenReturn("a");
        Mockito.when(contactB.getZodiacName()).thenReturn("b");

        assertThat(comparatorAsc.compare(contactA, contactB), lessThan(0));
    }

    @Test
    public void testEquals() {
        ZodiacComparator comparatorAsc = new ZodiacComparator();

        Mockito.when(contactA.getZodiacName()).thenReturn("a");
        Mockito.when(contactB.getZodiacName()).thenReturn("a");

        assertThat(comparatorAsc.compare(contactA, contactB), is(0));
    }

    @Test
    public void testGreater() {
        ZodiacComparator comparatorAsc = new ZodiacComparator();

        Mockito.when(contactA.getZodiacName()).thenReturn("b");
        Mockito.when(contactB.getZodiacName()).thenReturn("a");

        assertThat(comparatorAsc.compare(contactA, contactB), greaterThan(0));
    }
}
