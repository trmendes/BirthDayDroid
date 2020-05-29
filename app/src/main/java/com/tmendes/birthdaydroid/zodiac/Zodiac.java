package com.tmendes.birthdaydroid.zodiac;

import androidx.annotation.IntDef;

@IntDef({Zodiac.CAPRICORN, Zodiac.AQUARIUS, Zodiac.PISCES, Zodiac.ARIES,
        Zodiac.TAURUS, Zodiac.GEMINI, Zodiac.CANCER, Zodiac.LEO,
        Zodiac.VIRGO, Zodiac.LIBRA, Zodiac.SCORPIO, Zodiac.SAGITTARIUS})
public @interface Zodiac {
    int CAPRICORN = 0; // Earth
    int AQUARIUS = 1; // Air
    int PISCES = 2; // Water
    int ARIES = 3; // Fire
    int TAURUS = 4; // Earth
    int GEMINI = 5; // Air
    int CANCER = 6; // Water
    int LEO = 7; // Fire
    int VIRGO = 8; // Earth
    int LIBRA = 9; // Air
    int SCORPIO = 10; // Water
    int SAGITTARIUS = 11; // Fire
}
