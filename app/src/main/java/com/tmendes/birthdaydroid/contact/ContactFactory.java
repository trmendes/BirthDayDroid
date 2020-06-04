package com.tmendes.birthdaydroid.contact;

import com.tmendes.birthdaydroid.contact.android.AndroidContact;
import com.tmendes.birthdaydroid.contact.db.DBContact;
import com.tmendes.birthdaydroid.date.DateConverter;
import com.tmendes.birthdaydroid.zodiac.ZodiacCalculator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ContactFactory {
    private final ZodiacCalculator zodiacCalculator;
    private final DateConverter dateConverter;
    private final EventTypeLabelService eventTypeLabelService;

    public ContactFactory(ZodiacCalculator zodiacCalculator, DateConverter dateConverter, EventTypeLabelService eventTypeLabelService) {
        this.zodiacCalculator = zodiacCalculator;
        this.dateConverter = dateConverter;
        this.eventTypeLabelService = eventTypeLabelService;
    }

    public Contact createContact(AndroidContact androidContact, DBContact dbContact) throws ContactBuilderException {
        if (androidContact == null) {
            throw new ContactBuilderException("Can not create Contact without AndroidContact");
        }

        if (androidContact.getLookupKey() == null) {
            throw new ContactBuilderException("Can not build contact with null lookupKey");
        }

        if (androidContact.getDisplayName() == null) {
            throw new ContactBuilderException("Can not build contact with null displayName");
        }

        if (androidContact.getStartDate() == null) {
            throw new ContactBuilderException("Can not build contact with null startDate");
        }


        final WritableContact contact = new WritableContact();

        contact.setDbId(dbContact == null ? -1 : dbContact.getId());
        contact.setFavorite(dbContact != null && dbContact.isFavorite());
        contact.setIgnore(dbContact != null && dbContact.isIgnore());

        contact.setKey(androidContact.getLookupKey());
        contact.setName(androidContact.getDisplayName());
        contact.setPhotoUri(androidContact.getPhotoThumbnailUri());
        contact.setEventTypeLabel(eventTypeLabelService.getEventTypeLabel(androidContact.getEventType(), androidContact.getEventLabel()));

        final DateConverter.DateConverterResult convertingResult = dateConverter.convert(androidContact.getStartDate());
        if (!convertingResult.isSuccess()) {
            throw new ContactBuilderException(String.format("Can not build contact with unparsable date: %s", androidContact.getStartDate()));
        }

        final LocalDate bornOn = convertingResult.getDate();
        final Boolean missingYearInfo = convertingResult.getMissingYearInfo();
        final LocalDate now = LocalDate.now();

        contact.setBornOn(bornOn);
        contact.setMissingYearInfo(missingYearInfo);

        contact.setAge((int) ChronoUnit.YEARS.between(bornOn, now));

        LocalDate nextBirthday = bornOn.withYear(now.getYear());
        if (nextBirthday.isBefore(now)) {
            nextBirthday = nextBirthday.plusYears(1);
        }
        contact.setNextBirthday(nextBirthday);

        contact.setDaysUntilNextBirthday((int) ChronoUnit.DAYS.between(now, nextBirthday));
        contact.setDaysSinceLastBirthday((int) ChronoUnit.DAYS.between(nextBirthday.minusYears(1), now));
        contact.setDaysOld((int) ChronoUnit.DAYS.between(bornOn, now));
        contact.setBornInFuture(bornOn.isAfter(now) && !missingYearInfo);
        if(contact.isBornInFuture()) {
            contact.setAge(0);
            contact.setDaysOld(0);
            contact.setDaysSinceLastBirthday(0);
        }

        contact.setZodiac(zodiacCalculator.calculateZodiac(bornOn));

        return contact;
    }
}
