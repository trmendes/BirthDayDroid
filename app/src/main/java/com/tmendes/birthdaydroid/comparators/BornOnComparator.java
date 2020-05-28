package com.tmendes.birthdaydroid.comparators;

import com.tmendes.birthdaydroid.Contact;

import java.util.Comparator;

public class BornOnComparator implements Comparator<Contact> {
    @Override
    public int compare(Contact contactA, Contact contactB) {
        return contactB.getBornOn().compareTo(contactA.getBornOn());
    }
}
