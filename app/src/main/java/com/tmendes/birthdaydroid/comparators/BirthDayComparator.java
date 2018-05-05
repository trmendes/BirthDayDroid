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

import java.util.Comparator;

public class BirthDayComparator implements Comparator<Contact> {

    private final int orderType;
    private final int sortType;

    public BirthDayComparator(int order, int sortType) {
        this.orderType = order;
        this.sortType = sortType;
    }

    public int compare(Contact a, Contact b) {

        int res = 0;
        final int ORDER_DAYS_UNTIL_BIRTHDAY = 0;
        final int ORDER_AGE = 1;
        final int ORDER_NAME = 2;
        final int ORDER_SIGN = 3;
        final int SORT_ASC = 0;

        switch (orderType) {
            case ORDER_AGE:
                if (sortType == SORT_ASC) {
                    if ( a.getBirthday().before(b) ) res = 1;
                    else res = -1;
                } else {
                    if ( b.getBirthday().after(a) ) res = -1;
                    else res = 1;
                }
                break;
            case ORDER_DAYS_UNTIL_BIRTHDAY:
                if (sortType == SORT_ASC) {
                    res = (a.getDaysUntilNextBirthDay()).compareTo(b.getDaysUntilNextBirthDay());
                } else {
                    res = (b.getDaysUntilNextBirthDay()).compareTo(a.getDaysUntilNextBirthDay());
                }
                break;
            case ORDER_SIGN:
                if (sortType == SORT_ASC) {
                    res = (a.getSign()).compareTo(b.getSign());
                } else {
                    res = (b.getSign()).compareTo(a.getSign());
                }
                break;
            case ORDER_NAME:
                if (sortType == SORT_ASC) {
                    res = (a.getName()).compareTo(b.getName());
                } else {
                    res = (b.getName()).compareTo(a.getName());
                }
                break;
        }

        return res;
    }

}
