package com.tmendes.birthdaydroid.comparators;

import com.tmendes.birthdaydroid.Contact;

import java.util.Comparator;

class DaysUntilBirthdayComparator implements Comparator<Contact> {
    @Override
    public int compare(Contact contactA, Contact contactB) {
        return Integer.compare(contactA.getDaysUntilNextBirthday(), contactB.getDaysUntilNextBirthday());
    }
}
