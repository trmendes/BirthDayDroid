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
import java.util.Calendar;

public class Contact {


    private String key;
    private String name;
    private String photoURI;
    private String date;
    private int eventType;

    private String zodiac;
    private String zodiacElement;

    private boolean yearSettled;
    private boolean isHeSheNotEvenOneYearOld;
    private int age;
    private int daysOld;

    private int bornOnDay;
    private int bornOnMonth;
    private int bornOnYear;
    private int bornOnDayWeek;

    private int daysUntilNextBirthday;

    private Calendar bornOn;
    private Calendar nextBirthday;

    private boolean shallWePartyToday;

    public Contact(String key, String name, String photoURI, String date,
                   int eventType) {
        this.key = key;
        this.name = name;
        this.photoURI = photoURI;
        this.date = date;
        this.eventType = eventType;
        this.yearSettled = false;
    }

    public String getZodiac() {
        return zodiac;
    }

    public void setZodiac(String zodiac) {
        this.zodiac = zodiac;
    }

    public String getZodiacElement() {
        return zodiacElement;
    }

    public void setZodiacElement(String zodiacElement) {
        this.zodiacElement = zodiacElement;
    }

    public boolean isYearSettled() {
        return yearSettled;
    }

    public void setYearSettled(boolean yearSettled) {
        if (!yearSettled) {
            this.age = 0;
            this.daysOld = 0;
        }
        this.yearSettled = yearSettled;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getDaysOld() {
        return daysOld;
    }

    public void setDaysOld(int daysOld) {
        this.daysOld = daysOld;
    }

    public Calendar getBornOn() {
        return bornOn;
    }

    public void setBornOn(Calendar bornOn) {
        if (bornOn != null) {
            this.bornOn = bornOn;
            this.bornOnDay = bornOn.get(Calendar.DAY_OF_MONTH);
            this.bornOnMonth = bornOn.get(Calendar.MONTH);
            this.bornOnYear = bornOn.get(Calendar.YEAR);
            this.bornOnDayWeek = bornOn.get(Calendar.DAY_OF_WEEK);
        }
    }

    public void setNextBirthday(Calendar nextBirthday) {
        if (nextBirthday != null) {
            this.nextBirthday = nextBirthday;
        }
    }

    public boolean isHeSheNotEvenOneYearOld() {
        return isHeSheNotEvenOneYearOld;
    }

    public void setHeSheNotEvenOneYearOld(boolean heSheNotEvenOneYearOld) {
        this.isHeSheNotEvenOneYearOld = heSheNotEvenOneYearOld;
    }

    public int getBornOnDay() {
        return bornOnDay;
    }

    public int getBornOnMonth() {
        return bornOnMonth;
    }

    public String getBornOnMonthName() {
        DateFormatSymbols dfs = new DateFormatSymbols();
        return dfs.getMonths()[bornOnMonth];
    }

    public int getBornOnYear() {
        return bornOnYear;
    }

    public int getBornOnDayWeek() {
        return bornOnDayWeek;
    }

    public String getPrevBirthDayWeekName() {
        String weekName = "";
        if (nextBirthday != null) {
            Calendar prevBirthDay = (Calendar) this.nextBirthday.clone();
            prevBirthDay.set(Calendar.YEAR, prevBirthDay.get(Calendar.YEAR) - 1);
            DateFormatSymbols dfs = new DateFormatSymbols();
            weekName = dfs.getWeekdays()[prevBirthDay.get(Calendar.DAY_OF_WEEK)];
        }
        return weekName;
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

    public int getEventType() {
        return eventType;
    }

    public String getDate() {
        return date;
    }

    public int getDaysUntilNextBirthday() {
        return daysUntilNextBirthday;
    }

    public void setDaysUntilNextBirthday(int daysUntilNextBirthday) {
        this.daysUntilNextBirthday = daysUntilNextBirthday;
    }

    public boolean shallWePartyToday() {
        return shallWePartyToday;
    }

    public void setshallWePartyToday(boolean shallWePartyToday) {
        this.shallWePartyToday = shallWePartyToday;
    }
}