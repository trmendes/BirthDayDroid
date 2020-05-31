/*
 * Copyright (C) 2015-2016 The Food Restriction Project Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tmendes.birthdaydroid;

import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;


public class Contact {
    private long dbID;

    private final String key;
    private final String name;
    private final String photoURI;
    private final boolean customTypeLabel;
    private final String eventTypeLabel;

    private String zodiacSymbol;
    private String zodiacName;
    private String zodiacElementSymbol;
    private String zodiacElementName;

    private int age;
    private int daysOld;

    private Month bornOnMonth;
    private DayOfWeek bornOnDayOfWeek;

    private int daysUntilNextBirthday;
    private int daysSinceLastBirthday;

    private LocalDate bornOn;
    private LocalDate nextBirthday;

    private boolean favorite;
    private boolean ignore;

    private boolean bornInFuture;
    private boolean missingYearInfo;

    public Contact(String key, String name, String photoURI,
                   boolean customTypeLabel, String eventTypeLabel) {
        this.key = key;
        this.name = name;
        this.photoURI = photoURI;
        this.customTypeLabel = customTypeLabel;
        this.eventTypeLabel = eventTypeLabel;
        this.dbID = -1;
        this.missingYearInfo = false;
    }

    public String getZodiacName() {
        return zodiacName;
    }

    public void setZodiacSymbol(String zodiacSymbol) {
        this.zodiacSymbol = zodiacSymbol;
    }

    public void setZodiacName(String zodiacName) {
        this.zodiacName = zodiacName;
    }

    public String getZodiacElementName() {
        return zodiacElementName;
    }

    public void setZodiacElementSymbol(String zodiacElementSymbol) {
        this.zodiacElementSymbol = zodiacElementSymbol;
    }

    public void setZodiacElementName(String zodiacElementName) {
        this.zodiacElementName = zodiacElementName;
    }

    public int getAge() {
        return age;
    }

    public int getDaysOld() {
        return daysOld;
    }

    public LocalDate getBornOn() {
        return bornOn;
    }

    public void setBornOn(LocalDate bornOn) {
        if (bornOn != null) {
            this.bornOn = bornOn;

            this.bornOnMonth = bornOn.getMonth();
            this.bornOnDayOfWeek = bornOn.getDayOfWeek();

            LocalDate now = LocalDate.now();

            this.age = (int) ChronoUnit.YEARS.between(bornOn, now);

            this.nextBirthday = this.bornOn.withYear(now.getYear());
            if (nextBirthday.isBefore(now)) {
                this.nextBirthday = this.nextBirthday.plusYears(1);
            }

            this.daysUntilNextBirthday = (int) ChronoUnit.DAYS.between(now, nextBirthday);
            this.daysSinceLastBirthday = (int) ChronoUnit.DAYS.between(nextBirthday.minusYears(1), now);
            this.daysOld = (int) ChronoUnit.DAYS.between(bornOn, now);

            this.bornInFuture = this.bornOn.isAfter(now) && !this.missingYearInfo;

            if (this.bornInFuture) {
                this.age = 0;
                this.daysOld = 0;
            }
        }
    }

    public Month getBornOnMonth() {
        return bornOnMonth;
    }

    public String getNextBirthDayInfo() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM/dd - E", Locale.getDefault());
        return dateFormatter.format(this.nextBirthday);
    }

    public String getBornOnMonthName() {
        DateFormatSymbols dfs = new DateFormatSymbols();
        return dfs.getMonths()[bornOnMonth.getValue() - 1];
    }

    public DayOfWeek getBornOnDayOfWeek() {
        return bornOnDayOfWeek;
    }

    public String getNextBirthDayWeekName() {
        String weekName = "";
        if (nextBirthday != null) {
            DateFormatSymbols dfs = new DateFormatSymbols();
            weekName = dfs.getWeekdays()[this.nextBirthday.getDayOfWeek().getValue() - 1];
        }
        return weekName;
    }

    public String getName() {
        return name;
    }

    public String getContactFirstName() {
        String[] firstName = name.split(" ");
        return firstName[0];
    }

    public String getKey() {
        return key;
    }

    public String getPhotoURI() {
        return photoURI;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isCustomTypeLabel() {
        return customTypeLabel;
    }

    public String getEventTypeLabel() {
        return eventTypeLabel;
    }

    public int getDaysUntilNextBirthday() {
        return this.daysUntilNextBirthday;
    }

    public boolean shallWePartyToday() {
        return this.daysUntilNextBirthday == 0 && !this.bornInFuture;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite() {
        this.ignore = false;
        this.favorite = !this.favorite;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore() {
        this.favorite = false;
        this.ignore = !this.ignore;
    }

    public long getDbID() {
        return dbID;
    }

    public void setDbID(long dbID) {
        this.dbID = dbID;
    }

    public String getZodiacSymbol() {
        return this.zodiacSymbol;
    }

    public String getZodiacElementSymbol() {
        return this.zodiacElementSymbol;
    }

    public void setMissingYearInfo(boolean missingYearInfo) {
        this.missingYearInfo = missingYearInfo;
    }

    public boolean isMissingYearInfo() {
        return this.missingYearInfo;
    }

    public int getDaysSinceLastBirthday() {
        return daysSinceLastBirthday;
    }
}