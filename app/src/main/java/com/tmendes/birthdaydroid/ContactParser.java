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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ContactParser {

    private final Context ctx;

    private final int eventType;

    private final String name;
    private String zodiac;
    private String zodiacElement;
    private final String key;
    private final String photoURI;
    private final String date;

    private int day;
    private int month;
    private int age;
    private int daysAge;

    private final String failMsg;

    private boolean containsYearInfo = false;
    private boolean failOnParseDateString = true;
    private boolean letsCelebrate = false;

    private Calendar bornOn;
    private Calendar nextBirthday;

    private static final long DAY = 60 * 60 * 1000 * 24;

    private final List<String> knownPatterns = Arrays.asList(
            "yyyy-M-dd",
            "yyyy-M-dd hh:mm:ss.SSS",
            "dd-M-y",
            "dd-M-y hh:mm:ss.SSS",
            "--M-dd",
            "--M-dd hh:mm:ss.SSS",
            "dd-M-- hh:mm:ss.SSS",
            "dd-M--"
    );

    public ContactParser(Context ctx, String key, String name, String date, String photoURI, int eventType) {
        this.ctx = ctx;
        this.key = key;
        this.name = name;
        this.date = date;
        this.photoURI = photoURI;
        this.eventType = eventType;

        failMsg = "";

        parseContactBirthdayField(date);

        if (!failOnParseDateString) {
            setContactBirthDayInfo();
            setContactZodiac();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void parseContactBirthdayField(String dateString) {
        for (String pattern : knownPatterns) {
            try {
                bornOn = new GregorianCalendar();
                Date bornOnDate = new SimpleDateFormat(pattern).parse(dateString);
                if (bornOnDate != null) {
                    bornOn.setTime(bornOnDate);
                    failOnParseDateString = false;
                    containsYearInfo = pattern.contains("y");
                } else {
                    containsYearInfo = false;
                    failOnParseDateString = true;
                }
                break;
            } catch (ParseException ignored) {
                containsYearInfo = false;
                failOnParseDateString = true;
            }
        }

        if (failOnParseDateString) {
            String dialogData = " " + name +
                    "\n" +
                    ctx.getResources().getString(R.string.log_cant_parse, dateString) +
                    "\n";
            failMsg.concat(dialogData);
        }
    }

    private void setContactBirthDayInfo() {
        Calendar today = Calendar.getInstance();
        final int todayYear = today.get(Calendar.YEAR);
        final boolean isLeapYear = new GregorianCalendar().isLeapYear(todayYear);

        Calendar thisYearBirthday = (Calendar) bornOn.clone();
        thisYearBirthday.set(Calendar.YEAR, todayYear);

        /* Set The Next Birthday Info */
        this.nextBirthday = (Calendar) bornOn.clone();

        boolean lateBirthday = thisYearBirthday.getTimeInMillis() < today.getTimeInMillis();
        if (lateBirthday) {
            this.nextBirthday.set(Calendar.YEAR, todayYear + 1);
        } else {
            this.nextBirthday.set(Calendar.YEAR, todayYear);

        }
        this.day = bornOn.get(Calendar.DAY_OF_MONTH);
        this.month = bornOn.get(Calendar.MONTH);

        /* Age */
        if (containsYearInfo) {
            long daysOld = (today.getTimeInMillis() - bornOn.getTimeInMillis()) / DAY;
            boolean lessThanAYear;

            if (daysOld >= 0) {
                if (isLeapYear) {
                    lessThanAYear = daysOld < 366;
                } else {
                    lessThanAYear = daysOld < 365;
                }

                if (lessThanAYear) {
                    this.age = 0;
                    this.daysAge = (int) daysOld;
                } else {
                    age = todayYear - bornOn.get(Calendar.YEAR);
                    if (!lateBirthday) {
                        --age;
                    }
                }
            } else {
                /* Born in the Future */
                this.age = 0;
                this.daysAge = 0;
            }
        } else {
            bornOn.set(Calendar.YEAR, todayYear);
            age = -1;
        }

        /* Party Today \o/ */
        if (!isLeapYear &&
                (bornOn.get(Calendar.DAY_OF_MONTH) == 29
                        && bornOn.get(Calendar.MONTH) == Calendar.FEBRUARY) &&
                (today.get(Calendar.DAY_OF_MONTH) == 1
                        && today.get(Calendar.MONTH) == Calendar.MARCH)) {
            letsCelebrate = true;
        } else {
            if (today.get(Calendar.DAY_OF_MONTH) == bornOn.get(Calendar.DAY_OF_MONTH)
                    && today.get(Calendar.MONTH) == bornOn.get(Calendar.MONTH)) {
                letsCelebrate = true;
            }
        }
    }

    private void setContactZodiac() {
        if (ctx == null) {
            return;
        }

        switch (month) {
            case 0: // Jan
                if ((day >= 21) && (day <= 31)) {
                    zodiac = ctx.getResources().getString(R.string.sign_aquarius);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_air);
                } else {
                    zodiac = ctx.getResources().getString(R.string.sign_capricorn);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_earth);
                }
                break;
            case 1: // Feb
                if ((day >= 20) && (day <= 29)) {
                    zodiac = ctx.getResources().getString(R.string.sign_pisces);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_water);
                } else {
                    zodiac = ctx.getResources().getString(R.string.sign_aquarius);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_air);
                }
                break;
            case 2: // Mar
                if ((day >= 21) && (day <= 31)) {
                    zodiac = ctx.getResources().getString(R.string.sign_aries);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_fire);
                } else {
                    zodiac = ctx.getResources().getString(R.string.sign_pisces);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_water);
                }
                break;
            case 3: // Apr
                if ((day >= 20) && (day <= 30)) {
                    zodiac = ctx.getResources().getString(R.string.sign_taurus);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_earth);
                } else {
                    zodiac = ctx.getResources().getString(R.string.sign_aries);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_fire);
                }
                break;
            case 4: //May
                if ((day >= 20) && (day <= 31)) {
                    zodiac = ctx.getResources().getString(R.string.sign_gemini);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_air);
                } else {
                    zodiac = ctx.getResources().getString(R.string.sign_taurus);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_earth);
                }
                break;
            case 5: // Jun
                if ((day >= 21) && (day <= 30)) {
                    zodiac = ctx.getResources().getString(R.string.sign_cancer);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_water);
                } else {
                    zodiac = ctx.getResources().getString(R.string.sign_gemini);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_air);
                }
                break;
            case 6: // Jul
                if ((day >= 23) && (day <= 31)) {
                    zodiac = ctx.getResources().getString(R.string.sign_leo);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_fire);
                } else {
                    zodiac = ctx.getResources().getString(R.string.sign_cancer);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_water);
                }
                break;
            case 7: // Aug
                if ((day >= 22) && (day <= 31)) {
                    zodiac = ctx.getResources().getString(R.string.sign_virgo);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_earth);
                } else {
                    zodiac = ctx.getResources().getString(R.string.sign_leo);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_fire);
                }
                break;
            case 8: // Sep
                if ((day >= 23) && (day <= 30)) {
                    zodiac = ctx.getResources().getString(R.string.sign_libra);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_air);
                } else {
                    zodiac = ctx.getResources().getString(R.string.sign_virgo);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_earth);
                }
                break;
            case 9: // Oct
                if ((day >= 23) && (day <= 31)) {
                    zodiac = ctx.getResources().getString(R.string.sign_scorpio);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_water);
                } else {
                    zodiac = ctx.getResources().getString(R.string.sign_libra);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_air);
                }
                break;
            case 10: // Nov
                if ((day >= 22) && (day <= 30)) {
                    zodiac = ctx.getResources().getString(R.string.sign_sagittarius);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_fire);
                } else {
                    zodiac = ctx.getResources().getString(R.string.sign_scorpio);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_water);
                }
                break;
            case 11:
                if ((day >= 22) && (day <= 31)) {
                    zodiac = ctx.getResources().getString(R.string.sign_capricorn);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_earth);
                } else {
                    zodiac = ctx.getResources().getString(R.string.sign_sagittarius);
                    zodiacElement = ctx.getResources().getString(R.string.sign_element_fire);
                }
                break;
        }
    }

    public String getMonthName() {
        DateFormatSymbols dfs = new DateFormatSymbols();
        return dfs.getMonths()[month];
    }

    public int getBirthDayWeek() {
        return bornOn.get(Calendar.DAY_OF_WEEK);
    }

    public String getNextBirthDayWeekName() {
        DateFormatSymbols dfs = new DateFormatSymbols();
        return dfs.getWeekdays()[this.nextBirthday.get(Calendar.DAY_OF_WEEK)];
    }

    public String getPrevBirthDayWeekName() {
        Calendar prevBirthDay = (Calendar) this.nextBirthday.clone();
        prevBirthDay.set(Calendar.YEAR, prevBirthDay.get(Calendar.YEAR) - 1);
        DateFormatSymbols dfs = new DateFormatSymbols();
        return dfs.getWeekdays()[prevBirthDay.get(Calendar.DAY_OF_WEEK)];
    }

    public String getName() {
        return name;
    }

    public String getContactFirstName() {
        String[] firstName = name.split(" ");
        return firstName[0];
    }

    public String getZodiac() {
        return zodiac;
    }

    public String getZodiacElement() {
        return zodiacElement;
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

    public int getDaysAge() { return daysAge; }

    public int getYearsAge() { return age; }

    public Calendar getBirthday() {
        return bornOn;
    }

    public Long getDaysUntilNextBirthDay() {
        if (shallWeCelebrateToday()) {
            return 0L;
        }

        if (this.nextBirthday == null) {
            return Long.MAX_VALUE;
        }

        long timeDiffMs;
        long days;

        Calendar now = Calendar.getInstance();

        if (this.nextBirthday.getTimeInMillis() >= now.getTimeInMillis()) {
            timeDiffMs = this.nextBirthday.getTimeInMillis() - now.getTimeInMillis();
        } else {
            timeDiffMs = now.getTimeInMillis() - this.nextBirthday.getTimeInMillis();
        }

        /* Days */
        days = (int) (timeDiffMs / DAY) + 1;

        return days;
    }

    public boolean shallWeCelebrateToday() {
        return letsCelebrate;
    }

    public boolean failOnParseDateString() {
        return failOnParseDateString;
    }

    public String getFailMsg() {
        return failMsg;
    }

    public int getEventType() { return eventType; }
}
