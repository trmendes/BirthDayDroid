package com.tmendes.birthdaydroid.zodiac;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(Parameterized.class)
public class ZodiacCalculatorTest {

    private final Calendar date;

    @Zodiac
    private final int zodiac;

    public ZodiacCalculatorTest(int year, int month, int day, @Zodiac int zodiac) {
        Calendar date = Calendar.getInstance();
        date.set(year, month - 1, day, 0, 0, 0);
        date.set(Calendar.MILLISECOND, 0);

        this.date = date;
        this.zodiac = zodiac;
    }

    @Parameterized.Parameters(name = "Date: {0,number,0000}-{1,number,00}-{2,number,00}")
    public static Collection daysOfTheYear2020() {
        List<Object[]> parameters = new ArrayList<>();

        parameters.add(new Object[]{2020, 1, 1, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 1, 2, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 1, 3, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 1, 4, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 1, 5, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 1, 6, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 1, 7, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 1, 8, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 1, 9, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 1, 10, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 1, 11, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 1, 12, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 1, 13, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 1, 14, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 1, 15, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 1, 16, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 1, 17, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 1, 18, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 1, 19, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 1, 20, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 1, 21, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 1, 22, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 1, 23, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 1, 24, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 1, 25, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 1, 26, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 1, 27, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 1, 28, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 1, 29, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 1, 30, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 1, 31, Zodiac.AQUARIUS});

        parameters.add(new Object[]{2020, 2, 1, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 2, 2, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 2, 3, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 2, 4, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 2, 5, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 2, 6, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 2, 7, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 2, 8, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 2, 9, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 2, 10, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 2, 11, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 2, 12, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 2, 13, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 2, 14, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 2, 15, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 2, 16, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 2, 17, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 2, 18, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 2, 19, Zodiac.AQUARIUS});
        parameters.add(new Object[]{2020, 2, 20, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 2, 21, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 2, 22, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 2, 23, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 2, 24, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 2, 25, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 2, 26, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 2, 27, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 2, 28, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 2, 29, Zodiac.PISCES});

        parameters.add(new Object[]{2020, 3, 1, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 3, 2, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 3, 3, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 3, 4, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 3, 5, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 3, 6, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 3, 7, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 3, 8, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 3, 9, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 3, 10, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 3, 11, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 3, 12, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 3, 13, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 3, 14, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 3, 15, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 3, 16, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 3, 17, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 3, 18, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 3, 19, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 3, 20, Zodiac.PISCES});
        parameters.add(new Object[]{2020, 3, 21, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 3, 22, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 3, 23, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 3, 24, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 3, 25, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 3, 26, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 3, 27, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 3, 28, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 3, 29, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 3, 30, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 3, 31, Zodiac.ARIES});

        parameters.add(new Object[]{2020, 4, 1, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 4, 2, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 4, 3, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 4, 4, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 4, 5, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 4, 6, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 4, 7, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 4, 8, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 4, 9, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 4, 10, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 4, 11, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 4, 12, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 4, 13, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 4, 14, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 4, 15, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 4, 16, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 4, 17, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 4, 18, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 4, 19, Zodiac.ARIES});
        parameters.add(new Object[]{2020, 4, 20, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 4, 21, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 4, 22, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 4, 23, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 4, 24, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 4, 25, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 4, 26, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 4, 27, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 4, 28, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 4, 29, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 4, 30, Zodiac.TAURUS});

        parameters.add(new Object[]{2020, 5, 1, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 5, 2, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 5, 3, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 5, 4, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 5, 5, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 5, 6, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 5, 7, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 5, 8, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 5, 9, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 5, 10, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 5, 11, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 5, 12, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 5, 13, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 5, 14, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 5, 15, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 5, 16, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 5, 17, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 5, 18, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 5, 19, Zodiac.TAURUS});
        parameters.add(new Object[]{2020, 5, 20, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 5, 21, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 5, 22, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 5, 23, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 5, 24, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 5, 25, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 5, 26, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 5, 27, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 5, 28, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 5, 29, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 5, 30, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 5, 31, Zodiac.GEMINI});

        parameters.add(new Object[]{2020, 6, 1, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 6, 2, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 6, 3, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 6, 4, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 6, 5, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 6, 6, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 6, 7, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 6, 8, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 6, 9, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 6, 10, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 6, 11, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 6, 12, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 6, 13, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 6, 14, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 6, 15, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 6, 16, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 6, 17, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 6, 18, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 6, 19, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 6, 20, Zodiac.GEMINI});
        parameters.add(new Object[]{2020, 6, 21, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 6, 22, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 6, 23, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 6, 24, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 6, 25, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 6, 26, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 6, 27, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 6, 28, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 6, 29, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 6, 30, Zodiac.CANCER});

        parameters.add(new Object[]{2020, 7, 1, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 2, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 3, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 4, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 5, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 6, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 7, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 8, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 9, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 10, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 11, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 12, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 13, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 14, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 15, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 16, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 17, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 18, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 19, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 20, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 21, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 22, Zodiac.CANCER});
        parameters.add(new Object[]{2020, 7, 23, Zodiac.LEO});
        parameters.add(new Object[]{2020, 7, 24, Zodiac.LEO});
        parameters.add(new Object[]{2020, 7, 25, Zodiac.LEO});
        parameters.add(new Object[]{2020, 7, 26, Zodiac.LEO});
        parameters.add(new Object[]{2020, 7, 27, Zodiac.LEO});
        parameters.add(new Object[]{2020, 7, 28, Zodiac.LEO});
        parameters.add(new Object[]{2020, 7, 29, Zodiac.LEO});
        parameters.add(new Object[]{2020, 7, 30, Zodiac.LEO});
        parameters.add(new Object[]{2020, 7, 31, Zodiac.LEO});

        parameters.add(new Object[]{2020, 8, 1, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 2, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 3, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 4, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 5, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 6, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 7, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 8, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 9, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 10, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 11, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 12, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 13, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 14, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 15, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 16, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 17, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 18, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 19, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 20, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 21, Zodiac.LEO});
        parameters.add(new Object[]{2020, 8, 22, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 8, 23, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 8, 24, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 8, 25, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 8, 26, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 8, 27, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 8, 28, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 8, 29, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 8, 30, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 8, 31, Zodiac.VIRGO});

        parameters.add(new Object[]{2020, 9, 1, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 2, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 3, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 4, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 5, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 6, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 7, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 8, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 9, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 10, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 11, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 12, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 13, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 14, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 15, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 16, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 17, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 18, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 19, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 20, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 21, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 22, Zodiac.VIRGO});
        parameters.add(new Object[]{2020, 9, 23, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 9, 24, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 9, 25, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 9, 26, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 9, 27, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 9, 28, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 9, 29, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 9, 30, Zodiac.LIBRA});

        parameters.add(new Object[]{2020, 10, 1, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 2, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 3, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 4, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 5, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 6, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 7, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 8, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 9, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 10, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 11, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 12, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 13, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 14, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 15, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 16, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 17, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 18, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 19, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 20, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 21, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 22, Zodiac.LIBRA});
        parameters.add(new Object[]{2020, 10, 23, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 10, 24, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 10, 25, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 10, 26, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 10, 27, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 10, 28, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 10, 29, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 10, 30, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 10, 31, Zodiac.SCORPIO});

        parameters.add(new Object[]{2020, 11, 1, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 2, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 3, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 4, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 5, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 6, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 7, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 8, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 9, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 10, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 11, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 12, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 13, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 14, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 15, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 16, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 17, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 18, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 19, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 20, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 21, Zodiac.SCORPIO});
        parameters.add(new Object[]{2020, 11, 22, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 11, 23, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 11, 24, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 11, 25, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 11, 26, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 11, 27, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 11, 28, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 11, 29, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 11, 30, Zodiac.SAGITTARIUS});

        parameters.add(new Object[]{2020, 12, 1, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 2, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 3, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 4, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 5, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 6, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 7, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 8, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 9, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 10, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 11, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 12, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 13, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 14, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 15, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 16, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 17, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 18, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 19, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 20, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 21, Zodiac.SAGITTARIUS});
        parameters.add(new Object[]{2020, 12, 22, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 12, 23, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 12, 24, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 12, 25, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 12, 26, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 12, 27, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 12, 28, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 12, 29, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 12, 30, Zodiac.CAPRICORN});
        parameters.add(new Object[]{2020, 12, 31, Zodiac.CAPRICORN});

        return parameters;
    }

    @Test
    public void testDay() {
        @Zodiac int zodiac = new ZodiacCalculator().calculateZodiac(this.date);
        assertThat(zodiac, is(this.zodiac));
    }
}