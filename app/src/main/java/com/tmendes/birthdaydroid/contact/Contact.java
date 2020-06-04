package com.tmendes.birthdaydroid.contact;

import com.tmendes.birthdaydroid.zodiac.Zodiac;

import java.time.LocalDate;

public interface Contact {
    long getDbId();
    void setDbId(long id);

    String getKey();
    String getName();

    String getPhotoUri();
    String getEventTypeLabel();

    @Zodiac
    int getZodiac();

    int getAge();
    int getDaysOld();

    int getDaysUntilNextBirthday();
    int getDaysSinceLastBirthday();

    LocalDate getBornOn();
    LocalDate getNextBirthday();

    boolean isFavorite();
    void setFavorite(boolean favorite);
    default void toggleFavorite() {
        setFavorite(!isFavorite());
    }

    boolean isIgnore();
    void setIgnore(boolean ignore);
    default void toggleIgnore() {
        setIgnore(!isIgnore());
    }

    boolean isBornInFuture();
    boolean isMissingYearInfo();

    default boolean hasBirthDayToday() {
        return this.getDaysUntilNextBirthday() == 0 && !isBornInFuture();
    }
}
