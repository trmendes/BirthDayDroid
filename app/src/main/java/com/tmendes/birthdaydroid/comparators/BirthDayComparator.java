/*
 * Copyright (C) 2015-2016 The Food Restriction Project Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tmendes.birthdaydroid.comparators;

import com.tmendes.birthdaydroid.Contact;

import java.util.Calendar;
import java.util.Comparator;

public class BirthDayComparator implements Comparator<Contact> {

    private final int sortType;
    private final int sortOrder;

    public static final int SORT_ORDER_ASC = 0;
    public static final int SORT_ORDER_DESC = 1;

    public static final int SORT_TYPE_DAYS_UNTIL_BIRTHDAY = 0;
    public static final int SORT_TYPE_AGE = 1;
    public static final int SORT_TYPE_NAME = 2;
    public static final int SORT_TYPE_SIGN = 3;

    public BirthDayComparator(int sortType, int sortOrder) {
        this.sortType = sortType;
        this.sortOrder = sortOrder;
    }

    public int compare(Contact contactA, Contact contactB) {
        switch (sortType) {
            case SORT_TYPE_AGE:
                return compareComparable(contactB.getBornOn(), contactA.getBornOn());
            case SORT_TYPE_DAYS_UNTIL_BIRTHDAY:
                return compareByDaysUntilBirthday(contactA, contactB);
            case SORT_TYPE_SIGN:
                return compareComparable(contactA.getZodiacName(), contactB.getZodiacName());
            case SORT_TYPE_NAME:
                return compareComparable(contactA.getName(), contactB.getName());
            default:
                return 0;
        }
    }

    private int compareByDaysUntilBirthday(Contact contactA, Contact contactB) {
        int res;
        int cADaysToGo = contactA.getDaysUntilNextBirthday();
        int cBDaysToGo = contactB.getDaysUntilNextBirthday();
        if (cADaysToGo < 0) {
            cADaysToGo = cADaysToGo + Calendar.getInstance().getActualMaximum(Calendar.YEAR);
        }
        if (cBDaysToGo < 0) {
            cBDaysToGo = cBDaysToGo + Calendar.getInstance().getActualMaximum(Calendar.YEAR);
        }
        if (sortOrder == SORT_ORDER_ASC) {
            if (cADaysToGo - cBDaysToGo >= 0) res = 1;
            else res = -1;
        } else {
            if (cBDaysToGo - cADaysToGo <= 0) res = -1;
            else res = 1;
        }
        return res;
    }

    private <T extends Comparable<T>> int compareComparable(T comparableA, T comparableB) {
        int res;
        if (sortOrder == SORT_ORDER_ASC) {
            res = comparableA.compareTo(comparableB);
        } else {
            res = comparableB.compareTo(comparableA);
        }
        return res;
    }
}
