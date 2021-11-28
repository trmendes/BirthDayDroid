package com.tmendes.birthdaydroid.contact;

import com.tmendes.birthdaydroid.contact.android.AndroidContact;
import com.tmendes.birthdaydroid.contact.db.DBContact;
import com.tmendes.birthdaydroid.date.DateConverter;
import com.tmendes.birthdaydroid.zodiac.ZodiacCalculator;

import java.time.LocalDate;

public class ContactCreator {
    private final ZodiacCalculator zodiacCalculator;
    private final DateConverter dateConverter;
    private final EventTypeLabelService eventTypeLabelService;

    public ContactCreator(ZodiacCalculator zodiacCalculator, DateConverter dateConverter, EventTypeLabelService eventTypeLabelService) {
        this.zodiacCalculator = zodiacCalculator;
        this.dateConverter = dateConverter;
        this.eventTypeLabelService = eventTypeLabelService;
    }

    public Contact createContact(AndroidContact androidContact, DBContact dbContact) throws ContactFactoryException {
        if (androidContact == null) {
            throw new ContactFactoryException("Can not create Contact without AndroidContact");
        }

        if (androidContact.getLookupKey() == null) {
            throw new ContactFactoryException("Can not build contact with null lookupKey");
        }

        if (androidContact.getDisplayName() == null) {
            throw new ContactFactoryException("Can not build contact with null displayName");
        }

        if (androidContact.getStartDate() == null) {
            throw new ContactFactoryException("Can not build contact with null startDate");
        }

        final DateConverter.DateConverterResult convertingResult = dateConverter.convert(androidContact.getStartDate());
        if (!convertingResult.isSuccess()) {
            throw new ContactFactoryException(String.format("Can not build contact with unparsable date: %s", androidContact.getStartDate()));
        }

        final Contact contact = new Contact();

        contact.setDbId(dbContact == null ? -1 : dbContact.getId());
        contact.setFavorite(dbContact != null && dbContact.isFavorite());
        contact.setIgnore(dbContact != null && dbContact.isIgnore());

        contact.setKey(androidContact.getLookupKey());
        contact.setName(androidContact.getDisplayName());
        contact.setPhotoUri(androidContact.getPhotoThumbnailUri());
        contact.setEventTypeLabel(eventTypeLabelService.getEventTypeLabel(androidContact.getEventType(), androidContact.getEventLabel()));

        final LocalDate bornOn = convertingResult.getDate();

        LocalDate now = LocalDate.now();
        contact.setEventData(bornOn, now, convertingResult.getMissingYearInfo());
        contact.setZodiac(zodiacCalculator.calculateZodiac(bornOn));

        return contact;
    }
}
