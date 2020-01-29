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
import java.util.GregorianCalendar;

public class Contact {


    private long dbID;

    private final String key;
    private final String name;
    private final String photoURI;
    private final String eventTypeLabel;

    private String zodiacSymbol;
    private String zodiac;
    private String zodiacElementSymbol;
    private String zodiacElement;

    private boolean isHeSheNotEvenOneYearOld;
    private boolean notYetBorn;
    private int age;
    private int daysOld;

    private int bornOnDay;
    private int bornOnMonth;
    private int bornOnDayWeek;

    private int daysUntilNextBirthday;

    private Calendar bornOn;
    private Calendar nextBirthday;

    private boolean shallWePartyToday;
    private boolean favorite;
    private boolean ignore;

    public Contact(String key, String name, String photoURI,
                   String eventTypeLabel) {
        this.key = key;
        this.name = name;
        this.photoURI = photoURI;
        this.eventTypeLabel = eventTypeLabel;
        this.dbID = -1;
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

    public String getNextBirthDayInfo() {
        DateFormat dateFormat = new SimpleDateFormat("MMM/dd - E");
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

    public String getPrevBirthDayInfo() {
        Calendar prevBirthDay = (Calendar) this.nextBirthday.clone();
        prevBirthDay.set(Calendar.YEAR, prevBirthDay.get(Calendar.YEAR) - 1);
        DateFormat dateFormat = new SimpleDateFormat("MMM/dd - E");
        Date date = prevBirthDay.getTime();
        return dateFormat.format(date);
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

    public String getEventTypeLabel() {
        return eventTypeLabel;
    }

    public int getDaysUntilNextBirthday() {
        return daysUntilNextBirthday;
    }

    public void setDaysUntilNextBirthday(int daysUntilNextBirthday) {
        Calendar now = Calendar.getInstance();
        int nowYear = now.get(Calendar.YEAR);
        boolean isNowLeapYear = new GregorianCalendar().isLeapYear(nowYear);
        if (isNowLeapYear) {
            if (daysUntilNextBirthday == 366) {
                daysUntilNextBirthday = 0;
            }
        } else {
            if (daysUntilNextBirthday == 365) {
                daysUntilNextBirthday = 0;
            }
        }
        this.daysUntilNextBirthday = daysUntilNextBirthday;
    }

    public boolean shallWePartyToday() {
        return !notYetBorn && shallWePartyToday;
    }

    public void setshallWePartyToday(boolean shallWePartyToday) {
        this.shallWePartyToday = shallWePartyToday;
    }

    public boolean isNotYetBorn() {
        return notYetBorn;
    }

    public void setNotYetBorn(boolean notYetBorn) {
        this.notYetBorn = notYetBorn;
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
}