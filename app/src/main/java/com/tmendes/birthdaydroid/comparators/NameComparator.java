package com.tmendes.birthdaydroid.comparators;

import com.tmendes.birthdaydroid.contact.Contact;

import java.util.Comparator;

class NameComparator implements Comparator<Contact> {
    @Override
    public int compare(Contact contactA, Contact contactB) {
        return contactA.getName().compareToIgnoreCase(contactB.getName());
    }
}
