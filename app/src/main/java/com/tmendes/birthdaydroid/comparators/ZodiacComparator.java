package com.tmendes.birthdaydroid.comparators;

import com.tmendes.birthdaydroid.Contact;

import java.util.Comparator;

class ZodiacComparator implements Comparator<Contact> {
    @Override
    public int compare(Contact contactA, Contact contactB) {
        return contactA.getZodiacName().compareTo(contactB.getZodiacName());
    }
}
