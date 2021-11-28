package com.tmendes.birthdaydroid.views.contactlist;

import android.content.Context;

import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.date.DateLocaleHelper;
import com.tmendes.birthdaydroid.zodiac.ZodiacResourceHelper;

public class ContactFilter implements SortAndFilterRecyclerViewAdapter.Filter<Contact> {

    private final Context context;
    private final DateLocaleHelper dateLocaleHelper;
    private final ZodiacResourceHelper zodiacResourceHelper;

    public ContactFilter(Context context, DateLocaleHelper dateLocaleHelper, ZodiacResourceHelper zodiacResourceHelper) {
        this.context = context;
        this.dateLocaleHelper = dateLocaleHelper;
        this.zodiacResourceHelper = zodiacResourceHelper;
    }

    @Override
    public boolean filter(Contact contact, CharSequence filterTerm) {
        final String filterString = filterTerm.toString();

        final String name = contact.getName().toLowerCase();
        final String monthName = dateLocaleHelper.getMonthString(contact.getEventOriginalDate().getMonth(), context).toLowerCase();
        final String zodiacName = zodiacResourceHelper.getZodiacName(contact.getZodiac()).toLowerCase();
        final String zodiacElement = zodiacResourceHelper.getZodiacElementName(contact.getZodiac()).toLowerCase();
        final String age = Integer.toString(contact.getAgeInYears());
        final String daysOld = Integer.toString(contact.getAgeInDays());

        String birthdayWeekName = "";

        if (contact.isCelebrationThisYear() || contact.isCelebrationRecent()) {
            birthdayWeekName = dateLocaleHelper.getDayOfWeek(contact.getCurrentYearEvent().getDayOfWeek(), context).toLowerCase();
        } else {
            birthdayWeekName = dateLocaleHelper.getDayOfWeek(contact.getNextYearEvent().getDayOfWeek(), context).toLowerCase();
        }

        return name.contains(filterString) ||
                age.startsWith(filterString) ||
                daysOld.startsWith(filterString) ||
                monthName.contains(filterString) ||
                birthdayWeekName.contains(filterString) ||
                zodiacName.contains(filterString) ||
                zodiacElement.contains(filterString);
    }

}
