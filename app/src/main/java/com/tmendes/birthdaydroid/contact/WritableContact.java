package com.tmendes.birthdaydroid.contact;

import com.tmendes.birthdaydroid.zodiac.Zodiac;

import java.time.LocalDate;

public class WritableContact implements Contact {
    private long dbId;
    private String key;
    private String name;
    private String photoUri;
    private boolean customEventTypeLabel;
    private String eventTypeLabel;
    @Zodiac
    private int zodiac;
    private int age;
    private int daysOld;
    private int daysUntilNextBirthday;
    private int daysSinceLastBirthday;
    private LocalDate bornOn;
    private LocalDate nextBirthday;
    private boolean favorite;
    private boolean ignore;
    private boolean bornInFuture;
    private boolean missingYearInfo;

    @Override
    public long getDbId() {
        return dbId;
    }

    public void setDbId(long dbId) {
        this.dbId = dbId;
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    @Override
    public boolean isCustomEventTypeLabel() {
        return customEventTypeLabel;
    }

    public void setCustomEventTypeLabel(boolean customEventTypeLabel) {
        this.customEventTypeLabel = customEventTypeLabel;
    }

    @Override
    public String getEventTypeLabel() {
        return eventTypeLabel;
    }

    public void setEventTypeLabel(String eventTypeLabel) {
        this.eventTypeLabel = eventTypeLabel;
    }

    @Override
    public int getZodiac() {
        return zodiac;
    }

    public void setZodiac(@Zodiac int zodiac) {
        this.zodiac = zodiac;
    }

    @Override
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public int getDaysOld() {
        return daysOld;
    }

    public void setDaysOld(int daysOld) {
        this.daysOld = daysOld;
    }

    @Override
    public int getDaysUntilNextBirthday() {
        return daysUntilNextBirthday;
    }

    public void setDaysUntilNextBirthday(int daysUntilNextBirthday) {
        this.daysUntilNextBirthday = daysUntilNextBirthday;
    }

    @Override
    public int getDaysSinceLastBirthday() {
        return daysSinceLastBirthday;
    }

    public void setDaysSinceLastBirthday(int daysSinceLastBirthday) {
        this.daysSinceLastBirthday = daysSinceLastBirthday;
    }

    @Override
    public LocalDate getBornOn() {
        return bornOn;
    }

    public void setBornOn(LocalDate bornOn) {
        this.bornOn = bornOn;
    }

    @Override
    public LocalDate getNextBirthday() {
        return nextBirthday;
    }

    public void setNextBirthday(LocalDate nextBirthday) {
        this.nextBirthday = nextBirthday;
    }

    @Override
    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
        if(this.favorite) {
            this.ignore = false;
        }
    }

    @Override
    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
        if(this.ignore) {
            this.favorite = false;
        }
    }

    @Override
    public boolean isBornInFuture() {
        return bornInFuture;
    }

    public void setBornInFuture(boolean bornInFuture) {
        this.bornInFuture = bornInFuture;
    }

    @Override
    public boolean isMissingYearInfo() {
        return missingYearInfo;
    }

    public void setMissingYearInfo(boolean missingYearInfo) {
        this.missingYearInfo = missingYearInfo;
    }
}
