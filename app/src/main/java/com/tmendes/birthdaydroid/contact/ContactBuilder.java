package com.tmendes.birthdaydroid.contact;

import com.tmendes.birthdaydroid.date.EventDateConverter;
import com.tmendes.birthdaydroid.zodiac.ZodiacCalculator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ContactBuilder {
    private final ZodiacCalculator zodiacCalculator;
    private final EventDateConverter eventDateConverter;

    private Long dbId;
    private String key;
    private String name;
    private String photoUri;
    private String eventTypeLabel;
    private Boolean customEventTypeLabel;
    private String birthday;
    private Boolean ignore;
    private Boolean favorite;

    public ContactBuilder(ZodiacCalculator zodiacCalculator,
                          EventDateConverter eventDateConverter) {
        this.zodiacCalculator = zodiacCalculator;
        this.eventDateConverter = eventDateConverter;
    }

    public ContactBuilder setDbId(long dbId) {
        this.dbId = dbId;
        return this;
    }

    public ContactBuilder setKey(String key) {
        this.key = key;
        return this;
    }

    public ContactBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ContactBuilder setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
        return this;
    }

    public ContactBuilder setEventTypeLabel(String eventTypeLabel) {
        this.eventTypeLabel = eventTypeLabel;
        return this;
    }

    public ContactBuilder setCustomEventTypeLabel(boolean customEventTypeLabel) {
        this.customEventTypeLabel = customEventTypeLabel;
        return this;
    }


    public ContactBuilder setBirthdayString(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public ContactBuilder setIgnore(boolean ignore) {
        this.ignore = ignore;
        return this;
    }

    public ContactBuilder setFavorite(boolean favorite) {
        this.favorite = favorite;
        return this;
    }

    public Contact build() throws ContactBuilderException {
        WritableContact contact = new WritableContact();

        if (dbId == null) {
            throw new ContactBuilderException("Can not build contact without dbId");
        }
        contact.setDbId(dbId);

        if (key == null) {
            throw new ContactBuilderException("Can not build contact without key");
        }
        contact.setKey(key);

        if (name == null) {
            throw new ContactBuilderException("Can not build contact without name");
        }
        contact.setName(name);

        contact.setPhotoUri(photoUri);

        if (eventTypeLabel == null) {
            throw new ContactBuilderException("Can not build contact without eventTypeLabel");
        }
        contact.setEventTypeLabel(eventTypeLabel);

        if (customEventTypeLabel == null) {
            throw new ContactBuilderException("Can not build contact without customEventTypeLabel");
        }
        contact.setCustomEventTypeLabel(customEventTypeLabel);

        if (favorite == null) {
            throw new ContactBuilderException("Can not build contact without favorite");
        }
        contact.setFavorite(favorite);

        if (ignore == null) {
            throw new ContactBuilderException("Can not build contact without ignore");
        }
        contact.setFavorite(ignore);

        if(birthday == null) {
            throw new ContactBuilderException("Can not build contact without birthday");
        }
        final EventDateConverter.DateConverterResult convertingResult = eventDateConverter.convert(birthday);
        if (!convertingResult.isSuccess()) {
            throw new ContactBuilderException(String.format("Can not build contact with unparsable date: %s", birthday));
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
        }

        contact.setZodiac(zodiacCalculator.calculateZodiac(bornOn));

        return contact;
    }
}
