package com.tmendes.birthdaydroid.comparators;

import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.zodiac.ZodiacResourceHelper;

import java.util.Comparator;

class ZodiacComparator implements Comparator<Contact> {
    private final ZodiacResourceHelper zodiacResourceHelper;

    ZodiacComparator(ZodiacResourceHelper zodiacResourceHelper) {
        this.zodiacResourceHelper = zodiacResourceHelper;
    }

    @Override
    public int compare(Contact contactA, Contact contactB) {
        return zodiacResourceHelper.getZodiacName(contactA.getZodiac())
                .compareTo(zodiacResourceHelper.getZodiacName(contactB.getZodiac()));
    }
}
