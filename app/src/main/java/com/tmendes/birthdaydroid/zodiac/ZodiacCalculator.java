package com.tmendes.birthdaydroid.zodiac;

import java.time.LocalDate;
import java.time.Month;

public class ZodiacCalculator {
    @Zodiac
    public int calculateZodiac(LocalDate birthDate) {
        if (birthDate != null) {
            final Month birthMonth = birthDate.getMonth();
            final int birthDay = birthDate.getDayOfMonth();

            switch (birthMonth) {
                case JANUARY:
                    if ((birthDay >= 21)) {
                        return Zodiac.AQUARIUS;
                    } else {
                        return Zodiac.CAPRICORN;
                    }
                case FEBRUARY:
                    if ((birthDay >= 20)) {
                        return Zodiac.PISCES;
                    } else {
                        return Zodiac.AQUARIUS;
                    }
                case MARCH:
                    if ((birthDay >= 21)) {
                        return Zodiac.ARIES;
                    } else {
                        return Zodiac.PISCES;
                    }
                case APRIL:
                    if ((birthDay >= 20)) {
                        return Zodiac.TAURUS;
                    } else {
                        return Zodiac.ARIES;
                    }
                case MAY:
                    if ((birthDay >= 20)) {
                        return Zodiac.GEMINI;
                    } else {
                        return Zodiac.TAURUS;
                    }
                case JUNE:
                    if ((birthDay >= 21)) {
                        return Zodiac.CANCER;
                    } else {
                        return Zodiac.GEMINI;
                    }
                case JULY:
                    if ((birthDay >= 23)) {
                        return Zodiac.LEO;
                    } else {
                        return Zodiac.CANCER;
                    }
                case AUGUST:
                    if ((birthDay >= 22)) {
                        return Zodiac.VIRGO;
                    } else {
                        return Zodiac.LEO;
                    }
                case SEPTEMBER:
                    if ((birthDay >= 23)) {
                        return Zodiac.LIBRA;
                    } else {
                        return Zodiac.VIRGO;
                    }
                case OCTOBER:
                    if ((birthDay >= 23)) {
                        return Zodiac.SCORPIO;
                    } else {
                        return Zodiac.LIBRA;
                    }
                case NOVEMBER:
                    if ((birthDay >= 22)) {
                        return Zodiac.SAGITTARIUS;
                    } else {
                        return Zodiac.SCORPIO;
                    }
                case DECEMBER:
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
