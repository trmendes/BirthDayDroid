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

    private final int orderType;
    private final int sortType;

    public BirthDayComparator(int order, int sortType) {
        this.orderType = order;
        this.sortType = sortType;
    }

    public int compare(Contact contactA, Contact contactB) {
        final int ORDER_DAYS_UNTIL_BIRTHDAY = 0;
        final int ORDER_AGE = 1;
        final int ORDER_NAME = 2;
        final int ORDER_SIGN = 3;
        final int SORT_ASC = 0;

        int res;

        switch (orderType) {
            case ORDER_AGE:
                if (sortType == SORT_ASC) {
                    if ( contactA.getBornOn().before(contactB.getBornOn()) ) res = 1;
                    else res = -1;
                } else {
                    if ( contactB.getBornOn().after(contactA.getBornOn()) ) res = -1;
                    else res = 1;
                }
                break;
            case ORDER_DAYS_UNTIL_BIRTHDAY:
                int cADaysToGo = contactA.getDaysUntilNextBirthday();
                int cBDaysToGo = contactB.getDaysUntilNextBirthday();
                if (cADaysToGo < 0) {
                    cADaysToGo = cADaysToGo + Calendar.getInstance().getActualMaximum(Calendar.YEAR);
                }
                if (cBDaysToGo < 0) {
                    cBDaysToGo = cBDaysToGo + Calendar.getInstance().getActualMaximum(Calendar.YEAR);
                }
                if (sortType == SORT_ASC) {
                    if (cADaysToGo - cBDaysToGo >= 0) res = 1;
                    else res = -1;
                } else {
                    if (cBDaysToGo - cADaysToGo <= 0) res = -1;
                    else res = 1;
                }
                break;
            case ORDER_SIGN:
                if (sortType == SORT_ASC) {
                    res = (contactA.getZodiacName()).compareTo(contactB.getZodiacName());
                } else {
                    res = (contactB.getZodiacName()).compareTo(contactA.getZodiacName());
                }
                break;
            case ORDER_NAME:
                if (sortType == SORT_ASC) {
                    res = (contactA.getName()).compareTo(contactB.getName());
                } else {
                    res = (contactB.getName()).compareTo(contactA.getName());
                }
                break;

            default:
                res = 0;
        }

        return res;
    }

}
