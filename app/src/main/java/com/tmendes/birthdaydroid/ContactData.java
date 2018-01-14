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

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ContactData {

    private String name, sign, signElement, key, photoURI, date;
    private int day, month, year, age;
    private int monthAge, daysAge;

    private boolean aPartyGoingOnToday = false;
    private Calendar birthday;
    private Calendar nextBirthday;

    private final Context ctx;

    public ContactData(Context ctx) {
        this.ctx = ctx;
    }

    public ContactData(Context ctx, String key, String name, String date, String photoURI) {
        this.ctx = ctx;
        this.key = key;
        this.name = name;
        this.date = date;
        this.photoURI = photoURI;
        parseDate(date);
    }

    public void parseDate(String date) {

        final String[] formats = new String[]{
                "--MM-dd",
                "yyyy-MM-dd",
                "yyyy-MM-dd hh:mm:ss.SSS",
                "dd-MM--",
                "dd-MM-yyyy",
                "dd-MM-yyyy hh:mm:ss.SSS",
        };

        for (String format : formats) {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat df = new SimpleDateFormat(format);
            df.setLenient(false);
            try {
                this.birthday = new GregorianCalendar();
                this.birthday.setTime(df.parse(date));

                this.day = this.birthday.get(Calendar.DAY_OF_MONTH);
                this.month = this.birthday.get(Calendar.MONTH);

                if (format.contains("y")) {
                    this.year = this.birthday.get(Calendar.YEAR);
                } else {
                    this.year = 0;
                }

                break;
            } catch (ParseException ignored) {
            }
        }

        setBirthInfo();
    }

    private void setBirthInfo() {
        this.aPartyGoingOnToday = false;

        Calendar now = Calendar.getInstance();

        long timeDifferenceMilliseconds = now.getTimeInMillis() - birthday.getTimeInMillis();

        int aDay = 60 * 60 * 1000 * 24;
        int year = now.get(Calendar.YEAR);
        boolean isLeapYear = ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0));

        /* Years */
        this.age = (int) (timeDifferenceMilliseconds / ((long)aDay * now.getMaximum(Calendar.DAY_OF_YEAR)));

        if (this.age == 0) {
            /* Days */
            this.daysAge = (int) (timeDifferenceMilliseconds / aDay);
        }

        this.nextBirthday = birthday;

        this.nextBirthday.set(Calendar.YEAR, now.get(year));

        if (nextBirthday.getTimeInMillis() < now.getTimeInMillis()) {
            nextBirthday.add(year, 1);
        }

        /* Birthday today */
        if (now.get(Calendar.DAY_OF_MONTH) == birthday.get(Calendar.DAY_OF_MONTH) &&
                now.get(Calendar.MONTH) == birthday.get(Calendar.MONTH)) {
            this.aPartyGoingOnToday = true;
            ++this.age;
        } else if (!isLeapYear &&
                (now.get(Calendar.DAY_OF_MONTH) == 1 &&
                                birthday.get(Calendar.DAY_OF_MONTH) == 29) &&
                (now.get(Calendar.MONTH) == Calendar.MARCH &&
                birthday.get(Calendar.MONTH) == Calendar.FEBRUARY)) {
            this.aPartyGoingOnToday = true;
            ++this.age;
        }

        setSign();
    }


    private void setSign() {

        if (ctx == null) {
            return;
        }

        switch (this.month) {
            case 0: // Jan
                if ((this.day >= 21) && (this.day <= 31)) {
                    this.sign = ctx.getResources().getString(R.string.sign_aquarius);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_air);
                } else {
                    this.sign = ctx.getResources().getString(R.string.sign_capricorn);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_earth);
                }
                break;
            case 1: // Feb
                if ((this.day >= 20) && (this.day <= 29)) {
                    this.sign = ctx.getResources().getString(R.string.sign_pisces);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_water);
                } else {
                    this.sign = ctx.getResources().getString(R.string.sign_aquarius);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_air);
                }
                break;
            case 2: // Mar
                if ((this.day >= 21) && (this.day <= 31)) {
                    this.sign = ctx.getResources().getString(R.string.sign_aries);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_fire);
                } else {
                    this.sign = ctx.getResources().getString(R.string.sign_pisces);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_water);
                }
                break;
            case 3: // Apr
                if ((this.day >= 20) && (this.day <= 30)) {
                    this.sign = ctx.getResources().getString(R.string.sign_taurus);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_earth);
                } else {
                    this.sign = ctx.getResources().getString(R.string.sign_aries);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_fire);
                }
                break;
            case 4: //May
                if ((this.day >= 20) && (this.day <= 31)) {
                    this.sign = ctx.getResources().getString(R.string.sign_gemini);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_air);
                } else {
                    this.sign = ctx.getResources().getString(R.string.sign_taurus);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_earth);
                }
                break;
            case 5: // Jun
                if ((this.day >= 21) && (this.day <= 30)) {
                    this.sign = ctx.getResources().getString(R.string.sign_cancer);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_water);
                } else {
                    this.sign = ctx.getResources().getString(R.string.sign_gemini);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_air);
                }
                break;
            case 6: // Jul
                if ((this.day >= 23) && (this.day <= 31)) {
                    this.sign = ctx.getResources().getString(R.string.sign_leo);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_fire);
                } else {
                    this.sign = ctx.getResources().getString(R.string.sign_cancer);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_water);
                }
                break;
            case 7: // Aug
                if ((this.day >= 22) && (this.day <= 31)) {
                    this.sign = ctx.getResources().getString(R.string.sign_virgo);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_earth);
                } else {
                    this.sign = ctx.getResources().getString(R.string.sign_leo);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_fire);
                }
                break;
            case 8: // Sep
                if ((this.day >= 23) && (this.day <= 30)) {
                    this.sign = ctx.getResources().getString(R.string.sign_libra);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_air);
                } else {
                    this.sign = ctx.getResources().getString(R.string.sign_virgo);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_earth);
                }
                break;
            case 9: // Oct
                if ((this.day >= 23) && (this.day <= 31)) {
                    this.sign = ctx.getResources().getString(R.string.sign_scorpio);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_water);
                } else {
                    this.sign = ctx.getResources().getString(R.string.sign_libra);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_air);
                }
                break;
            case 10: // Nov
                if ((this.day >= 22) && (this.day <= 30)) {
                    this.sign = ctx.getResources().getString(R.string.sign_sagittarius);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_fire);
                } else {
                    this.sign = ctx.getResources().getString(R.string.sign_scorpio);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_water);
                }
                break;
            case 11:
                if ((this.day >= 22) && (this.day <= 31)) {
                    this.sign = ctx.getResources().getString(R.string.sign_capricorn);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_earth);
                } else {
                    this.sign = ctx.getResources().getString(R.string.sign_sagittarius);
                    this.signElement = ctx.getResources().getString(R.string.sign_element_fire);
                }
                break;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKeyID(String keyID) {
        this.key = keyID;
    }

    public void setPhotoURI(String photoURI) {
        this.photoURI = photoURI;
    }

    public String getMonthName() {
        DateFormatSymbols dfs = new DateFormatSymbols();
        return dfs.getMonths()[this.month];
    }

    public int getBirthDayWeek() {
        return this.birthday.get(Calendar.DAY_OF_WEEK);
    }

    public String getNextBirtDayWeekName() {
        DateFormatSymbols dfs = new DateFormatSymbols();
        return dfs.getWeekdays()[nextBirthday.get(Calendar.DAY_OF_WEEK)];

    }

    public String getName() {
        return name;
    }

    public String getContactFirstName() {
        String[] firstName = name.split(" ");
        return firstName[0];
    }

    public String getSign() {
        return sign;
    }

    public String getSignElement() {
        return signElement;
    }

    public String getKey() {
        return key;
    }

    public String getPhotoURI() {
        return photoURI;
    }

    public String getDate() { return this.date; }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    private int getYear() {
        return year;
    }

    public int getMonthAge() { return monthAge; }

    public int getDaysAge() { return daysAge; }

    public Calendar getBirthday() {
        return this.birthday;
    }

    public int getAge() {
        return age;
    }

    public Long getDaysUntilNextBirthDay() {
        if (isaPartyGoingOnToday()) {
            return 0L;
        }

        long timeDifferenceMilliseconds = 0;
        long days = 0;

        Calendar now = Calendar.getInstance();

        if (nextBirthday.getTimeInMillis() >= now.getTimeInMillis()) {
            timeDifferenceMilliseconds = nextBirthday.getTimeInMillis() - now.getTimeInMillis();
        } else {
            timeDifferenceMilliseconds = now.getTimeInMillis() - nextBirthday.getTimeInMillis();
        }

        /* Days */
        days = (int) (timeDifferenceMilliseconds / (60 * 60 * 1000 * 24)) + 1;

        return days;
    }

    public boolean isaPartyGoingOnToday() {
        return aPartyGoingOnToday;
    }

    public boolean hasYear() {
        return this.year != 0;
    }

    public String toString() {
        return "Name: " + getName() + " - Age: " + getAge() + " - [d:" + getDay() + ":m:" + getMonth() + ":y:" + getYear() + "] - " + " sign: " + getSign() + " - Element: " + getSignElement() + " - hasYear(): " + hasYear() + " aPartyGoingOnToday(): " + isaPartyGoingOnToday();
    }

}
