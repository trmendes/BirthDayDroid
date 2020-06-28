package com.tmendes.birthdaydroid.contact;

import com.tmendes.birthdaydroid.zodiac.Zodiac;

import java.time.LocalDate;

public class Contact {
    private long dbId;
    private String key;

    private String name;
    private String photoUri;
    private String eventTypeLabel;

    @Zodiac
    private int zodiac;

    private boolean favorite;
    private boolean ignore;

    private LocalDate bornOn;
    private boolean missingYearInfo;

    private LocalDate nextBirthday;
    private int ageInYears;
    private int ageInDays;
    private int daysUntilNextBirthday;
    private int daysSinceLastBirthday;
    private boolean bornInFuture;
    private boolean birthdayToday;

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

    public void setAgeInYears(int ageInYears) {
        this.ageInYears = ageInYears;
    }

    public int getAgeInDays() {
        return ageInDays;
    }

    public void setAgeInDays(int ageInDays) {
        this.ageInDays = ageInDays;
    }

    public int getDaysUntilNextBirthday() {
        return daysUntilNextBirthday;
    }

    public void setDaysUntilNextBirthday(int daysUntilNextBirthday) {
        this.daysUntilNextBirthday = daysUntilNextBirthday;
    }

    public int getDaysSinceLastBirthday() {
        return daysSinceLastBirthday;
    }

    public void setDaysSinceLastBirthday(int daysSinceLastBirthday) {
        this.daysSinceLastBirthday = daysSinceLastBirthday;
    }

    public LocalDate getBornOn() {
        return bornOn;
    }

    public void setBornOn(LocalDate bornOn) {
        this.bornOn = bornOn;
    }

    public LocalDate getNextBirthday() {
        return nextBirthday;
    }

    public void setNextBirthday(LocalDate nextBirthday) {
        this.nextBirthday = nextBirthday;
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

    public boolean isBornInFuture() {
        return bornInFuture;
    }

    public void setBornInFuture(boolean bornInFuture) {
        this.bornInFuture = bornInFuture;
    }

    public boolean isMissingYearInfo() {
        return missingYearInfo;
    }

    public void setMissingYearInfo(boolean missingYearInfo) {
        this.missingYearInfo = missingYearInfo;
    }

    public void setBirthdayToday(boolean birthdayToday) {
        this.birthdayToday = birthdayToday;
    }

    public boolean isBirthdayToday() {
        return birthdayToday;
    }
}
