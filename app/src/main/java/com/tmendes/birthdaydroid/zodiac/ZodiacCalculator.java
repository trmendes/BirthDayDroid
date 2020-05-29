package com.tmendes.birthdaydroid.zodiac;

import java.util.Calendar;

public class ZodiacCalculator {
    @Zodiac
    public int calculateZodiac(Calendar birthDate) {
        if (birthDate != null) {
            final int birthMonth = birthDate.get(Calendar.MONTH);
            final int birthDay = birthDate.get(Calendar.DAY_OF_MONTH);

            switch (birthMonth) {
                case Calendar.JANUARY:
                    if ((birthDay >= 21)) {
                        return Zodiac.AQUARIUS;
                    } else {
                        return Zodiac.CAPRICORN;
                    }
                case Calendar.FEBRUARY:
                    if ((birthDay >= 20)) {
                        return Zodiac.PISCES;
                    } else {
                        return Zodiac.AQUARIUS;
                    }
                case Calendar.MARCH:
                    if ((birthDay >= 21)) {
                        return Zodiac.ARIES;
                    } else {
                        return Zodiac.PISCES;
                    }
                case Calendar.APRIL:
                    if ((birthDay >= 20)) {
                        return Zodiac.TAURUS;
                    } else {
                        return Zodiac.ARIES;
                    }
                case Calendar.MAY:
                    if ((birthDay >= 20)) {
                        return Zodiac.GEMINI;
                    } else {
                        return Zodiac.TAURUS;
                    }
                case Calendar.JUNE:
                    if ((birthDay >= 21)) {
                        return Zodiac.CANCER;
                    } else {
                        return Zodiac.GEMINI;
                    }
                case Calendar.JULY:
                    if ((birthDay >= 23)) {
                        return Zodiac.LEO;
                    } else {
                        return Zodiac.CANCER;
                    }
                case Calendar.AUGUST:
                    if ((birthDay >= 22)) {
                        return Zodiac.VIRGO;
                    } else {
                        return Zodiac.LEO;
                    }
                case Calendar.SEPTEMBER:
                    if ((birthDay >= 23)) {
                        return Zodiac.LIBRA;
                    } else {
                        return Zodiac.VIRGO;
                    }
                case Calendar.OCTOBER:
                    if ((birthDay >= 23)) {
                        return Zodiac.SCORPIO;
                    } else {
                        return Zodiac.LIBRA;
                    }
                case Calendar.NOVEMBER:
                    if ((birthDay >= 22)) {
                        return Zodiac.SAGITTARIUS;
                    } else {
                        return Zodiac.SCORPIO;
                    }
                case Calendar.DECEMBER:
                    if (birthDay >= 22) {
                        return Zodiac.CAPRICORN;
                    } else {
                        return Zodiac.SAGITTARIUS;
                    }
            }
        }

        throw new IllegalArgumentException();
    }
}
