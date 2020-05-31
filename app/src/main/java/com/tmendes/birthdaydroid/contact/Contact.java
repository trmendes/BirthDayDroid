package com.tmendes.birthdaydroid.contact;

import com.tmendes.birthdaydroid.zodiac.Zodiac;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public interface Contact {
    long getDbId();
    void setDbId(long id);

    String getKey();
    String getName();
    /**
     * @deprecated Will be refactored in future
     */
    @Deprecated
    default String getFirstName() {
        String[] firstName = getName().split(" ");
        return firstName[0];
    }

    String getPhotoUri();
    boolean isCustomEventTypeLabel();
    String getEventTypeLabel();

    @Zodiac
    int getZodiac();

    int getAge();
    int getDaysOld();

    int getDaysUntilNextBirthday();
    int getDaysSinceLastBirthday();

    LocalDate getBornOn();
    LocalDate getNextBirthday();

    default Month getBornOnMonth(){
        return this.getBornOn().getMonth();
    }

    default DayOfWeek getBornOnDayOfWeek(){
        return this.getBornOn().getDayOfWeek();
    }

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

    /**
     * @deprecated Will be refactored in future
     */
    @Deprecated
    default String getNextBirthDayInfo() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM/dd - E", Locale.getDefault());
        return dateFormatter.format(this.getNextBirthday());
    }
}
