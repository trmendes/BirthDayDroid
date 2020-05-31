package com.tmendes.birthdaydroid.contact;

import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
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

    /**
     * @deprecated Will be refactored in future
     */
    @Deprecated
    String getZodiacSymbol();
    /**
     * @deprecated Will be refactored in future
     */
    @Deprecated
    String getZodiacName();
    /**
     * @deprecated Will be refactored in future
     */
    @Deprecated
    String getZodiacElementName();
    /**
     * @deprecated Will be refactored in future
     */
    @Deprecated
    String getZodiacElementSymbol();

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

    /**
     * @deprecated Will be refactored in future
     */
    @Deprecated
    default String getBornOnMonthName() {
        return this.getBornOn().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
    }

    /**
     * @deprecated Will be refactored in future
     */
    @Deprecated
    default String getNextBirthDayWeekName() {
        return this.getNextBirthday().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
    }
}
