package com.tmendes.birthdaydroid.comparators;

import com.tmendes.birthdaydroid.contact.Contact;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.util.Collections;
import java.util.Comparator;

import static com.tmendes.birthdaydroid.comparators.BirthDayComparatorFactory.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

public class BirthDayComparatorFactoryTest {

    private BirthDayComparatorFactory factory;

    @Before
    public void setUp() {
        factory = new BirthDayComparatorFactory();
    }

    @Test
    public void testAgeComparatorASC() {
        Comparator<Contact> comparator = factory.createBirthdayComparator(SORT_TYPE_AGE, SORT_ORDER_ASC);
        assertThat(comparator, instanceOf(AgeComparator.class));
    }

    @Test
    public void testAgeComparatorDESC() {
        Comparator<Contact> comparator = factory.createBirthdayComparator(SORT_TYPE_AGE, SORT_ORDER_DESC);
        assertThat(comparator, not(instanceOf(AgeComparator.class)));

        comparator = Collections.reverseOrder(comparator);
        assertThat(comparator, instanceOf(AgeComparator.class));
    }

    @Test
    public void testZodiacComparatorASC() {
        Comparator<Contact> comparator = factory.createBirthdayComparator(SORT_TYPE_ZODIAC, SORT_ORDER_ASC);
        assertThat(comparator, instanceOf(ZodiacComparator.class));
    }

    @Test
    public void testZodiacComparatorDESC() {
        Comparator<Contact> comparator = factory.createBirthdayComparator(SORT_TYPE_ZODIAC, SORT_ORDER_DESC);
        assertThat(comparator, not(instanceOf(ZodiacComparator.class)));

        comparator = Collections.reverseOrder(comparator);
        assertThat(comparator, instanceOf(ZodiacComparator.class));
    }

    @Test
    public void testNameComparatorASC() {
        Comparator<Contact> comparator = factory.createBirthdayComparator(SORT_TYPE_NAME, SORT_ORDER_ASC);
        assertThat(comparator, instanceOf(NameComparator.class));
    }

    @Test
    public void testNameComparatorDESC() {
        Comparator<Contact> comparator = factory.createBirthdayComparator(SORT_TYPE_NAME, SORT_ORDER_DESC);
        assertThat(comparator, not(instanceOf(NameComparator.class)));

        comparator = Collections.reverseOrder(comparator);
        assertThat(comparator, instanceOf(NameComparator.class));
    }

    @Test
    public void testDaysUntilBirthdayComparatorASC() {
        Comparator<Contact> comparator = factory.createBirthdayComparator(SORT_TYPE_DAYS_UNTIL_BIRTHDAY, SORT_ORDER_ASC);
        assertThat(comparator, instanceOf(DaysUntilBirthdayComparator.class));
    }

    @Test
    public void testDaysUntilBirthdayComparatorDESC() {
        Comparator<Contact> comparator = factory.createBirthdayComparator(SORT_TYPE_DAYS_UNTIL_BIRTHDAY, SORT_ORDER_DESC);
        assertThat(comparator, not(instanceOf(DaysUntilBirthdayComparator.class)));

        comparator = Collections.reverseOrder(comparator);
        assertThat(comparator, instanceOf(DaysUntilBirthdayComparator.class));
    }

    @Test
    public void testUnknownSortType() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> factory.createBirthdayComparator(-42, SORT_ORDER_DESC));
    }

    @Test
    public void testUnknownSortOrder() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> factory.createBirthdayComparator(SORT_TYPE_DAYS_UNTIL_BIRTHDAY, -42));
    }
}
