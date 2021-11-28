package com.tmendes.birthdaydroid.contact;

import com.tmendes.birthdaydroid.zodiac.Zodiac;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Contact {
    private static final int CELEBRATION_DAYS_THRESHOLD = 7;

    private long dbId;
    private String key;

    private String name;
    private String photoUri;
    private String eventTypeLabel;

    @Zodiac
    private int zodiac;

    private boolean favorite;
    private boolean ignore;

    private LocalDate eventOriginalDate;
    private LocalDate currentYearEvent;
    private LocalDate nextYearEvent;

    private boolean isCelebrtionThisYear;
    private boolean isCelebrationToday;
    private boolean isEventInTheFuture;
    private boolean isEventMissingYear;

    private int ageInYears;
    private int ageInDays;

    private int daysUntilNextEvent;
    private int daysSinceLastEvent;

    public long getDbId() {
        return dbId;
    }

    public void setDbId(long dbId) {
        this.dbId = dbId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getEventTypeLabel() {
        return eventTypeLabel;
    }

    public void setEventTypeLabel(String eventTypeLabel) {
        this.eventTypeLabel = eventTypeLabel;
    }

    public int getZodiac() {
        return zodiac;
    }

    public void setZodiac(@Zodiac int zodiac) {
        this.zodiac = zodiac;
    }

    public int getAgeInYears() {
        return ageInYears;
    }

    public int getAgeInDays() {
        return ageInDays;
    }

    public int getDaysUntilNextEvent() {
        return daysUntilNextEvent;
    }

    public int getDaysSinceLastEvent() {
        return daysSinceLastEvent;
    }

    public LocalDate getEventOriginalDate() {
        return eventOriginalDate;
    }

    public void setEventData(LocalDate eventOriginalDate, LocalDate now, boolean isYearMissing) {
        this.eventOriginalDate = eventOriginalDate;
        this.isEventMissingYear = isYearMissing;

        this.currentYearEvent = eventOriginalDate.withYear(now.getYear());
        this.nextYearEvent = currentYearEvent.plusYears(1);

        if (this.isEventMissingYear) {
            this.ageInDays = 0;
            this.ageInYears = 0;
        } else {
            this.ageInYears = Math.max(0, (int) ChronoUnit.YEARS.between(eventOriginalDate, now));
            this.ageInDays = Math.max(0, (int) ChronoUnit.DAYS.between(eventOriginalDate, now));
        }

        this.isCelebrtionThisYear = ((int) ChronoUnit.DAYS.between(now, this.currentYearEvent) >= 0);
        this.isCelebrationToday = ((int) ChronoUnit.DAYS.between(now, this.currentYearEvent) == 0);
        this.isEventInTheFuture = eventOriginalDate.isAfter(now) && this.isEventMissingYear;

        if (this.isCelebrtionThisYear) {
            this.daysUntilNextEvent = (int) ChronoUnit.DAYS.between(now, this.currentYearEvent);
            this.daysSinceLastEvent = (int) ChronoUnit.DAYS.between(this.currentYearEvent.minusYears(1), now);
        } else {
            this.daysUntilNextEvent = (int) ChronoUnit.DAYS.between(now, this.nextYearEvent);
            this.daysSinceLastEvent = (int) ChronoUnit.DAYS.between(this.nextYearEvent.minusYears(1), now);
        }
    }

    public LocalDate getCurrentYearEvent() { return currentYearEvent; }
    public LocalDate getNextYearEvent() {
        return nextYearEvent;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
        if(this.favorite) {
            this.ignore = false;
        }
    }

    public void toggleFavorite() {
        setFavorite(!isFavorite());
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
        if(this.ignore) {
            this.favorite = false;
        }
    }

    public void toggleIgnore() {
        setIgnore(!isIgnore());
    }

    public boolean isFromFuture() {
        return isEventInTheFuture;
    }

    public boolean isCelebrtionThisYear() { return isCelebrtionThisYear; }

    public boolean isCelebrationToday() {
        return isCelebrationToday;
    }

    public boolean isEventMissingYear() {
        return isEventMissingYear;
    }

    public boolean isCelebrationRecent() {
        return this.getDaysSinceLastEvent() <= CELEBRATION_DAYS_THRESHOLD && !this.isFromFuture();
    }

    public void updateEventData() {
        LocalDate now = LocalDate.now();
        this.setEventData(this.eventOriginalDate, now, this.isEventMissingYear);
    }
}
