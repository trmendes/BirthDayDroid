package com.tmendes.birthdaydroid.comparators;

import com.tmendes.birthdaydroid.Contact;

import java.util.Comparator;

class DaysUntilBirthdayComparator implements Comparator<Contact> {
    @Override
    public int compare(Contact contactA, Contact contactB) {
        int cADaysToGo = contactA.getDaysUntilNextBirthday();
        int cBDaysToGo = contactB.getDaysUntilNextBirthday();
        if (cADaysToGo < 0) {
            cADaysToGo = cADaysToGo + 512;
        }
        if (cBDaysToGo < 0) {
            cBDaysToGo = cBDaysToGo + 512;
        }

        return Integer.compare(cADaysToGo, cBDaysToGo);
    }
}
