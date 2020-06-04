package com.tmendes.birthdaydroid.contact;

import com.tmendes.birthdaydroid.date.DateConverter;
import com.tmendes.birthdaydroid.zodiac.ZodiacCalculator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ContactBuilder {
    private final ZodiacCalculator zodiacCalculator;
    private final DateConverter dateConverter;

    private Long dbId;
    private String key;
    private String name;
    private String photoUri;
    private String eventTypeLabel;
    private String birthday;
    private Boolean ignore;
    private Boolean favorite;

    public ContactBuilder(ZodiacCalculator zodiacCalculator,
                          DateConverter dateConverter) {
        this.zodiacCalculator = zodiacCalculator;
        this.dateConverter = dateConverter;
    }

    public ContactBuilder setDbId(Long dbId) {
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

    public ContactBuilder setBirthdayString(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public ContactBuilder setIgnore(Boolean ignore) {
        this.ignore = ignore;
        return this;
    }

    public ContactBuilder setFavorite(Boolean favorite) {
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
        final DateConverter.DateConverterResult convertingResult = dateConverter.convert(birthday);
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
            contact.setDaysSinceLastBirthday(0);
        }

        contact.setZodiac(zodiacCalculator.calculateZodiac(bornOn));

        return contact;
    }
}
