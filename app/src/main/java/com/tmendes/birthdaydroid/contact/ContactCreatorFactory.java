package com.tmendes.birthdaydroid.contact;

import android.content.Context;

import com.tmendes.birthdaydroid.date.DateConverter;
import com.tmendes.birthdaydroid.zodiac.ZodiacCalculator;

public class ContactCreatorFactory {
    public ContactCreator createContactCreator(Context context) {
        final ZodiacCalculator zodiacCalculator = new ZodiacCalculator();
        final DateConverter dateConverter = new DateConverter();
        final EventTypeLabelService eventTypeLabelService = new EventTypeLabelService(context);
        return new ContactCreator(zodiacCalculator, dateConverter, eventTypeLabelService);
    }
}
