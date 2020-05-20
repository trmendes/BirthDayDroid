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

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class Contact {
    private long dbID;

    private final String key;
    private final String name;
    private final String photoURI;
    private final boolean customTypeLabel;
    private final String eventTypeLabel;

    private String zodiacSymbol;
    private String zodiac;
    private String zodiacElementSymbol;
    private String zodiacElement;

    private int age;
    private int daysOld;

    private int bornOnDay;
    private int bornOnMonth;
    private int bornOnDayWeek;

    private int daysUntilNextBirthday;

    private Calendar bornOn;
    private Calendar nextBirthday;

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

    public String getZodiac() {
        return zodiac;
    }

    public void setZodiacSymbol(String zodiacSymbol) {
        this.zodiacSymbol = zodiacSymbol;
    }

    public void setZodiac(String zodiac) {
        this.zodiac = zodiac;
    }

    public String getZodiacElement() {
        return zodiacElement;
    }

    public void setZodiacElementSymbol(String zodiacElementSymbol) {
        this.zodiacElementSymbol = zodiacElementSymbol;
    }

    public void setZodiacElement(String zodiacElement) {
        this.zodiacElement = zodiacElement;
    }

    public int getAge() {
        return age;
    }

    public int getDaysOld() {
        return daysOld;
    }

    public Calendar getBornOn() {
        return bornOn;
    }

    public void setBornOn(Calendar bornOn) {
        if (bornOn != null) {
            this.bornOn = bornOn;
            this.bornOn.set(Calendar.HOUR_OF_DAY, 0);
            this.bornOn.set(Calendar.MINUTE, 0);
            this.bornOn.set(Calendar.SECOND, 0);
            this.bornOn.set(Calendar.MILLISECOND, 0);

            this.bornOnDay = bornOn.get(Calendar.DAY_OF_MONTH);
            this.bornOnMonth = bornOn.get(Calendar.MONTH);
            this.bornOnDayWeek = bornOn.get(Calendar.DAY_OF_WEEK);

            Calendar now = Calendar.getInstance();
            now.set(Calendar.HOUR_OF_DAY, 0);
            now.set(Calendar.MINUTE, 0);
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);

            this.age = now.get(Calendar.YEAR) - this.bornOn.get(Calendar.YEAR);

            this.nextBirthday = (Calendar) this.bornOn.clone();
            this.nextBirthday.set(Calendar.YEAR, now.get(Calendar.YEAR));

            // diff for daylight saving time. Offset between nextBirthday and now can be different.
            long timeZoneDiff = nextBirthday.getTimeZone().getOffset(nextBirthday.getTimeInMillis())
                    - now.getTimeZone().getOffset(now.getTimeInMillis());

            long diffInMillis = nextBirthday.getTimeInMillis() - now.getTimeInMillis() + timeZoneDiff;
            this.daysUntilNextBirthday = (int) TimeUnit.DAYS.convert(diffInMillis,
                    TimeUnit.MILLISECONDS);

            if (this.daysUntilNextBirthday < 0){
                /* Late birthday */
                ++this.age;
                this.nextBirthday.set(Calendar.YEAR, now.get(Calendar.YEAR) + 1);
            }

            this.bornInFuture = this.bornOn.compareTo(now) >= 1 && !this.missingYearInfo;

            diffInMillis = bornOn.getTimeInMillis() - now.getTimeInMillis();
            this.daysOld = Math.abs((int) TimeUnit.DAYS.convert(diffInMillis,
                    TimeUnit.MILLISECONDS));

            if (this.bornInFuture) {
                this.age = 0;
                this.daysOld = 0;
            }
        }
    }

    public int getBornOnDay() {
        return bornOnDay;
    }

    public int getBornOnMonth() {
        return bornOnMonth;
    }

    public String getNextBirthDayInfo() {
        DateFormat dateFormat = new SimpleDateFormat("MMM/dd - E", Locale.getDefault());
        Date date = this.nextBirthday.getTime();
        return dateFormat.format(date);
    }

    public String getBornOnMonthName() {
        DateFormatSymbols dfs = new DateFormatSymbols();
        return dfs.getMonths()[bornOnMonth];
    }

    public int getBornOnDayWeek() {
        return bornOnDayWeek;
    }

    public String getNextBirthDayWeekName() {
        String weekName = "";
        if (nextBirthday != null) {
            DateFormatSymbols dfs = new DateFormatSymbols();
            weekName = dfs.getWeekdays()[this.nextBirthday.get(Calendar.DAY_OF_WEEK)];
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

    public void setMissinYearInfo() {
        this.missingYearInfo = true;
    }

    public boolean isMissingYearInfo() {
        return this.missingYearInfo;
    }
}