package com.tmendes.birthdaydroid.comparators;

import com.tmendes.birthdaydroid.Contact;

import java.util.Collections;
import java.util.Comparator;

public class BirthDayComparatorFactory {
    public static final int SORT_ORDER_ASC = 0;
    public static final int SORT_ORDER_DESC = 1;

    public static final int SORT_TYPE_DAYS_UNTIL_BIRTHDAY = 0;
    public static final int SORT_TYPE_AGE = 1;
    public static final int SORT_TYPE_NAME = 2;
    public static final int SORT_TYPE_ZODIAC = 3;

    public Comparator<Contact> createBirthdayComparator(int sortType, int sortOrder) {
        final Comparator<Contact> comparator;
        switch (sortType) {
            case SORT_TYPE_AGE:
                comparator = new AgeComparator();
                break;
            case SORT_TYPE_DAYS_UNTIL_BIRTHDAY:
                comparator = new DaysUntilBirthdayComparator();
                break;
            case SORT_TYPE_ZODIAC:
                comparator = new ZodiacComparator();
                break;
            case SORT_TYPE_NAME:
                comparator = new NameComparator();
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown sortType: %d", sortType));
        }

        switch (sortOrder) {
            case SORT_ORDER_ASC:
                return comparator;
            case SORT_ORDER_DESC:
                return Collections.reverseOrder(comparator);
            default:
                throw new IllegalArgumentException(String.format("Unknown sortOrder: %d", sortOrder));
        }
    }
}
