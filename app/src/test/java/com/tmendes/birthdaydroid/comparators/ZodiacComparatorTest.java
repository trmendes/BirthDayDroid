package com.tmendes.birthdaydroid.comparators;

import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.zodiac.ZodiacResourceHelper;

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
    private ZodiacResourceHelper zodiacResourceHelper;

    @Before
    public void setUp() {
        contactA = mock(Contact.class);
        contactB = mock(Contact.class);
        zodiacResourceHelper = mock(ZodiacResourceHelper.class);
    }

    @Test
    public void testLess() {
        ZodiacComparator comparatorAsc = new ZodiacComparator(zodiacResourceHelper);

        Mockito.when(contactA.getZodiac()).thenReturn(1);
        Mockito.when(contactB.getZodiac()).thenReturn(2);
        Mockito.when(zodiacResourceHelper.getZodiacName(1)).thenReturn("a");
        Mockito.when(zodiacResourceHelper.getZodiacName(2)).thenReturn("b");

        assertThat(comparatorAsc.compare(contactA, contactB), lessThan(0));
    }

    @Test
    public void testEquals() {
        ZodiacComparator comparatorAsc = new ZodiacComparator(zodiacResourceHelper);

        Mockito.when(contactA.getZodiac()).thenReturn(1);
        Mockito.when(contactB.getZodiac()).thenReturn(1);
        Mockito.when(zodiacResourceHelper.getZodiacName(1)).thenReturn("a");

        assertThat(comparatorAsc.compare(contactA, contactB), is(0));
    }

    @Test
    public void testGreater() {
        ZodiacComparator comparatorAsc = new ZodiacComparator(zodiacResourceHelper);

        Mockito.when(contactA.getZodiac()).thenReturn(1);
        Mockito.when(contactB.getZodiac()).thenReturn(2);
        Mockito.when(zodiacResourceHelper.getZodiacName(1)).thenReturn("b");
        Mockito.when(zodiacResourceHelper.getZodiacName(2)).thenReturn("a");

        assertThat(comparatorAsc.compare(contactA, contactB), greaterThan(0));
    }
}
