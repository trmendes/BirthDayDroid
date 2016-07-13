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
import java.util.Date;
import java.util.GregorianCalendar;

public class ContactData {

    private String name, sign, signElement, key, photoURI, date;
    private int day, month, year, age, bornOnWeekDay, netxBirthDayOnWeekDay;
    private long daysUntilNextBirthDay;
    private boolean isThereAPartyToday = false, hasYear = false;
    private Calendar calBirthDay;

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
                Date birthday = df.parse(date);
                this.calBirthDay = new GregorianCalendar();
                this.calBirthDay.setTime(birthday);

                this.day = this.calBirthDay.get(Calendar.DAY_OF_MONTH);
                this.month = this.calBirthDay.get(Calendar.MONTH);

                if (format.contains("y")) {
                    this.hasYear = true;
                    this.year = this.calBirthDay.get(Calendar.YEAR);
                } else {
                    this.hasYear = false;
                    this.year = 0;
                }

                break;
            } catch (ParseException ignored) {
            }
        }
        setBirthInfo();
        setNextBirthDayData();
    }

    private void setBirthInfo() {
        this.isThereAPartyToday = false;
        Calendar today = Calendar.getInstance();
        this.age = today.get(Calendar.YEAR) - calBirthDay.get(Calendar.YEAR);
        this.bornOnWeekDay = calBirthDay.get(Calendar.DAY_OF_WEEK);
        if (today.get(Calendar.MONTH) < calBirthDay.get(Calendar.MONTH)) {
            this.age--;
        } else if (today.get(Calendar.MONTH) == calBirthDay.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) < calBirthDay.get(Calendar.DAY_OF_MONTH)) {
            this.age--;
        } else if (today.get(Calendar.MONTH) == calBirthDay.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) == calBirthDay.get(Calendar.DAY_OF_MONTH)) {
            this.isThereAPartyToday = true;
        }
        setSign();
    }

    private void setNextBirthDayData() {
        Calendar today = Calendar.getInstance();
        Calendar tmpBirthDay = calBirthDay;
        tmpBirthDay.set(Calendar.YEAR, today.get(Calendar.YEAR));
        this.netxBirthDayOnWeekDay = tmpBirthDay.get(Calendar.DAY_OF_WEEK);

        long diff = tmpBirthDay.getTime().getTime() - today.getTime().getTime();
        int numberOfDays = today.getActualMaximum(Calendar.DAY_OF_YEAR);
        boolean leapYear = numberOfDays > 365;
        if ((leapYear) && (today.get(Calendar.MONTH) > Calendar.FEBRUARY)) {
            numberOfDays--;
        }

        if (today.getTime().getTime() > tmpBirthDay.getTime().getTime()) {
            int nextYear = today.get(Calendar.YEAR) + 1;
            today.set(Calendar.YEAR, nextYear);
            this.netxBirthDayOnWeekDay = tmpBirthDay.get(Calendar.DAY_OF_WEEK);
            long daysLeft = diff / (1000 * 60 * 60 * 24) - 1;
            this.daysUntilNextBirthDay = numberOfDays + daysLeft;
        } else {
            this.daysUntilNextBirthDay = diff / (1000 * 60 * 60 * 24);
        }

        if (this.daysUntilNextBirthDay == numberOfDays) {
            this.daysUntilNextBirthDay = 0;
        }
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
        return this.bornOnWeekDay;
    }

    public String getNextBirtDayWeekName() {
        DateFormatSymbols dfs = new DateFormatSymbols();
        return dfs.getWeekdays()[this.netxBirthDayOnWeekDay];

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

    private Integer getYear() {
        return year;
    }

    public Integer getAge() {
        return age;
    }

    public Long getDaysUntilNextBirthDay() {
        if (isThereAPartyToday()) {
            return 0L;
        }

        return daysUntilNextBirthDay + 1;
    }

    public boolean isThereAPartyToday() {
        return isThereAPartyToday;
    }

    public boolean hasYear() {
        return hasYear;
    }

    public String toString() {
        return "Name: " + getName() + " - Age: " + getAge() + " - [d:" + getDay() + ":m:" + getMonth() + ":y:" + getYear() + "] - " + " sign: " + getSign() + " - Element: " + getSignElement() + " - hasYear(): " + hasYear() + " isThereAPartyToday(): " + isThereAPartyToday();
    }

}
