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

public class Contact {

    private String name;
    private String sign;
    private String signElement;
    private String key;
    private String photoURI;
    private String date;

    private int day;
    private int month;
    private int year;
    private int age;
    private int daysAge;

    private boolean missingYear = false;
    private boolean missingData = true;
    private boolean letsCelebrate = false;

    private Calendar bornOn;
    private Calendar nextBirthday;

    private final Context ctx;

    private static final long DAY = 60 * 60 * 1000 * 24;

    private final static String[] formats = new String[]{
            "--MM-dd",
            "dd-MM--",
            "yyyy-MM-dd",
            "yyyy-MM-dd hh:mm:ss.SSS",
            "dd-MM-yyyy",
            "dd-MM-yyyy hh:mm:ss.SSS",
    };

    public Contact(Context ctx) {
        this.ctx = ctx;
    }

    public Contact(Context ctx, String key, String name, String date, String photoURI) {
        this.ctx = ctx;
        this.key = key;
        this.name = name;
        this.date = date;
        this.photoURI = photoURI;

        parseContactBirthdayField(date);

        if (!missingData) {
            setContactBirthDayInfo();
            setContactSign();
        }
    }

    private void parseContactBirthdayField(String date) {
        for (String format : formats) {
            try {
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat df = new SimpleDateFormat(format);
                df.setLenient(false);
                bornOn = new GregorianCalendar();
                bornOn.setTime(df.parse(date));

                day = bornOn.get(Calendar.DAY_OF_MONTH);
                month = bornOn.get(Calendar.MONTH);

                if (format.contains("y")) {
                    year = bornOn.get(Calendar.YEAR);
                    missingYear = false;
                } else {
                    year = 0;
                    missingYear = true;
                }

                missingData = false;
                break;
            } catch (ParseException ignored) {
                missingData = true;
            }
        }
    }

    private void setContactBirthDayInfo() {
        Calendar nowCal = Calendar.getInstance();
        final int nowYear = nowCal.get(Calendar.YEAR);

        final boolean isLeapYear = ((nowYear % 4 == 0)
                && (nowYear % 100 != 0) || (nowYear % 400 == 0));

        long diffBirthDayTodayMs;

        /* Years */
        if (!missingYear) {
            diffBirthDayTodayMs = nowCal.getTimeInMillis() - bornOn.getTimeInMillis();
            age = (int) (diffBirthDayTodayMs / (DAY * nowCal.getMaximum(Calendar.DAY_OF_YEAR)));
            if (age == 0) {
                /* For those who are just born */
                daysAge = (int) (diffBirthDayTodayMs / DAY);
                /* People still can't be born on the future /o\ */
                if (daysAge < 0) {
                    daysAge = 0;
                }
            }
        }

        nextBirthday = (Calendar) bornOn.clone();
        nextBirthday.set(Calendar.YEAR, nowYear);

        /* In case the birthday is over we add 1 year to the age */
        if (nextBirthday.getTimeInMillis() < nowCal.getTimeInMillis()) {
            nextBirthday.add(Calendar.YEAR, 1);
        }

        /* Mark those we have to celebrate today \o/ */
        if (isLeapYear) {
            if ((nowCal.get(Calendar.DAY_OF_MONTH) == 1)
                    && (bornOn.get(Calendar.DAY_OF_MONTH) == 29)
                    && (nowCal.get(Calendar.MONTH) == Calendar.MARCH)) {
                letsCelebrate = true;
                ++age;
            } else if (nowCal.get(Calendar.DAY_OF_MONTH) == bornOn.get(Calendar.DAY_OF_MONTH)
                    && nowCal.get(Calendar.MONTH) == bornOn.get(Calendar.MONTH)) {
                letsCelebrate = true;
                ++age;
            }
        } else {
            /* In case the birthday is today */
            if (nowCal.get(Calendar.DAY_OF_MONTH) == bornOn.get(Calendar.DAY_OF_MONTH)
                    && nowCal.get(Calendar.MONTH) == bornOn.get(Calendar.MONTH)) {
                letsCelebrate = true;
                ++age;
            }
        }
    }

    private void setContactSign() {
        if (ctx == null) {
            return;
        }

        switch (month) {
            case 0: // Jan
                if ((day >= 21) && (day <= 31)) {
                    sign = ctx.getResources().getString(R.string.sign_aquarius);
                    signElement = ctx.getResources().getString(R.string.sign_element_air);
                } else {
                    sign = ctx.getResources().getString(R.string.sign_capricorn);
                    signElement = ctx.getResources().getString(R.string.sign_element_earth);
                }
                break;
            case 1: // Feb
                if ((day >= 20) && (day <= 29)) {
                    sign = ctx.getResources().getString(R.string.sign_pisces);
                    signElement = ctx.getResources().getString(R.string.sign_element_water);
                } else {
                    sign = ctx.getResources().getString(R.string.sign_aquarius);
                    signElement = ctx.getResources().getString(R.string.sign_element_air);
                }
                break;
            case 2: // Mar
                if ((day >= 21) && (day <= 31)) {
                    sign = ctx.getResources().getString(R.string.sign_aries);
                    signElement = ctx.getResources().getString(R.string.sign_element_fire);
                } else {
                    sign = ctx.getResources().getString(R.string.sign_pisces);
                    signElement = ctx.getResources().getString(R.string.sign_element_water);
                }
                break;
            case 3: // Apr
                if ((day >= 20) && (day <= 30)) {
                    sign = ctx.getResources().getString(R.string.sign_taurus);
                    signElement = ctx.getResources().getString(R.string.sign_element_earth);
                } else {
                    sign = ctx.getResources().getString(R.string.sign_aries);
                    signElement = ctx.getResources().getString(R.string.sign_element_fire);
                }
                break;
            case 4: //May
                if ((day >= 20) && (day <= 31)) {
                    sign = ctx.getResources().getString(R.string.sign_gemini);
                    signElement = ctx.getResources().getString(R.string.sign_element_air);
                } else {
                    sign = ctx.getResources().getString(R.string.sign_taurus);
                    signElement = ctx.getResources().getString(R.string.sign_element_earth);
                }
                break;
            case 5: // Jun
                if ((day >= 21) && (day <= 30)) {
                    sign = ctx.getResources().getString(R.string.sign_cancer);
                    signElement = ctx.getResources().getString(R.string.sign_element_water);
                } else {
                    sign = ctx.getResources().getString(R.string.sign_gemini);
                    signElement = ctx.getResources().getString(R.string.sign_element_air);
                }
                break;
            case 6: // Jul
                if ((day >= 23) && (day <= 31)) {
                    sign = ctx.getResources().getString(R.string.sign_leo);
                    signElement = ctx.getResources().getString(R.string.sign_element_fire);
                } else {
                    sign = ctx.getResources().getString(R.string.sign_cancer);
                    signElement = ctx.getResources().getString(R.string.sign_element_water);
                }
                break;
            case 7: // Aug
                if ((day >= 22) && (day <= 31)) {
                    sign = ctx.getResources().getString(R.string.sign_virgo);
                    signElement = ctx.getResources().getString(R.string.sign_element_earth);
                } else {
                    sign = ctx.getResources().getString(R.string.sign_leo);
                    signElement = ctx.getResources().getString(R.string.sign_element_fire);
                }
                break;
            case 8: // Sep
                if ((day >= 23) && (day <= 30)) {
                    sign = ctx.getResources().getString(R.string.sign_libra);
                    signElement = ctx.getResources().getString(R.string.sign_element_air);
                } else {
                    sign = ctx.getResources().getString(R.string.sign_virgo);
                    signElement = ctx.getResources().getString(R.string.sign_element_earth);
                }
                break;
            case 9: // Oct
                if ((day >= 23) && (day <= 31)) {
                    sign = ctx.getResources().getString(R.string.sign_scorpio);
                    signElement = ctx.getResources().getString(R.string.sign_element_water);
                } else {
                    sign = ctx.getResources().getString(R.string.sign_libra);
                    signElement = ctx.getResources().getString(R.string.sign_element_air);
                }
                break;
            case 10: // Nov
                if ((day >= 22) && (day <= 30)) {
                    sign = ctx.getResources().getString(R.string.sign_sagittarius);
                    signElement = ctx.getResources().getString(R.string.sign_element_fire);
                } else {
                    sign = ctx.getResources().getString(R.string.sign_scorpio);
                    signElement = ctx.getResources().getString(R.string.sign_element_water);
                }
                break;
            case 11:
                if ((day >= 22) && (day <= 31)) {
                    sign = ctx.getResources().getString(R.string.sign_capricorn);
                    signElement = ctx.getResources().getString(R.string.sign_element_earth);
                } else {
                    sign = ctx.getResources().getString(R.string.sign_sagittarius);
                    signElement = ctx.getResources().getString(R.string.sign_element_fire);
                }
                break;
        }
    }

    public void setName(String name) {
        name = name;
    }

    public void setKeyID(String keyID) {
        key = keyID;
    }

    public void setPhotoURI(String photoURI) {
        photoURI = photoURI;
    }

    public String getMonthName() {
        DateFormatSymbols dfs = new DateFormatSymbols();
        return dfs.getMonths()[month];
    }

    public int getBirthDayWeek() {
        return bornOn.get(Calendar.DAY_OF_WEEK);
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

    public String getDate() { return date; }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    private int getYear() { return year; }

    public int getDaysAge() { return daysAge; }

    public Calendar getBirthday() {
        return bornOn;
    }

    public int getAge() {
        return age;
    }

    public Long getDaysUntilNextBirthDay() {
        if (shallWeCelebrateToday()) {
            return 0L;
        }

        long timeDiffMs = 0;
        long days = 0;

        Calendar now = Calendar.getInstance();

        if (nextBirthday.getTimeInMillis() >= now.getTimeInMillis()) {
            timeDiffMs = nextBirthday.getTimeInMillis() - now.getTimeInMillis();
        } else {
            timeDiffMs = now.getTimeInMillis() - nextBirthday.getTimeInMillis();
        }

        /* Days */
        days = (int) (timeDiffMs / DAY) + 1;

        return days;
    }

    public boolean shallWeCelebrateToday() {
        return letsCelebrate;
    }

    public boolean isMissingData() {
        return missingData;
    }

    public boolean isMissingYear() {
        return missingYear;
    }

    public String toString() {
        return "Name: " + getName() + " - Age: " + getAge() + " - [d:" + getDay() + ":m:" +
                getMonth() + ":y:" + getYear() + "] - " + " sign: " + getSign() + " - Element: " +
                getSignElement() + " - isMissingData(): " + isMissingData() +
                " aPartyGoingOnToday(): " + shallWeCelebrateToday();
    }

}
