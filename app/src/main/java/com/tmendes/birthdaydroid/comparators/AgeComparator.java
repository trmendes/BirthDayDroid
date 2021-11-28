package com.tmendes.birthdaydroid.comparators;

import com.tmendes.birthdaydroid.contact.Contact;

import java.util.Comparator;

public class AgeComparator implements Comparator<Contact> {
    @Override
    public int compare(Contact contactA, Contact contactB) {
        return contactB.getEventOriginalDate().compareTo(contactA.getEventOriginalDate());
    }
}
