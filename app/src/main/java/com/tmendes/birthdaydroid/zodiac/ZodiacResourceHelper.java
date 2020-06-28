package com.tmendes.birthdaydroid.zodiac;

import android.content.Context;
import android.content.res.Resources;

import com.tmendes.birthdaydroid.R;

public class ZodiacResourceHelper {
    private final String[] zodiacNames;
    private final String[] zodiacSymbols;
    private final String[] zodiacElementNames;
    private final String[] zodiacElementSymbol;

    public ZodiacResourceHelper(Context context) {
        final Resources resources = context.getResources();
        zodiacNames = new String[]{
                resources.getString(R.string.sign_capricorn),
                resources.getString(R.string.sign_aquarius),
                resources.getString(R.string.sign_pisces),
                resources.getString(R.string.sign_aries),
                resources.getString(R.string.sign_taurus),
                resources.getString(R.string.sign_gemini),
                resources.getString(R.string.sign_cancer),
                resources.getString(R.string.sign_leo),
                resources.getString(R.string.sign_virgo),
                resources.getString(R.string.sign_libra),
                resources.getString(R.string.sign_scorpio),
                resources.getString(R.string.sign_sagittarius)
        };

        zodiacSymbols = new String[]{
                resources.getString(R.string.sign_capricorn_symbol),
                resources.getString(R.string.sign_aquarius_symbol),
                resources.getString(R.string.sign_pisces_symbol),
                resources.getString(R.string.sign_aries_symbol),
                resources.getString(R.string.sign_taurus_symbol),
                resources.getString(R.string.sign_gemini_symbol),
                resources.getString(R.string.sign_cancer_symbol),
                resources.getString(R.string.sign_leo_symbol),
                resources.getString(R.string.sign_virgo_symbol),
                resources.getString(R.string.sign_libra_symbol),
                resources.getString(R.string.sign_scorpio_symbol),
                resources.getString(R.string.sign_sagittarius_symbol)
        };

        zodiacElementNames = new String[]{
                resources.getString(R.string.sign_element_earth),
                resources.getString(R.string.sign_element_air),
                resources.getString(R.string.sign_element_water),
                resources.getString(R.string.sign_element_fire)
        };

        zodiacElementSymbol = new String[]{
                resources.getString(R.string.sign_element_earth_symbol),
                resources.getString(R.string.sign_element_air_symbol),
                resources.getString(R.string.sign_element_water_symbol),
                resources.getString(R.string.sign_element_fire_symbol)
        };
    }

    public String getZodiacName(@Zodiac int zodiac) {
        return zodiacNames[zodiac];
    }

    public String getZodiacSymbol(@Zodiac int zodiac) {
        return zodiacSymbols[zodiac];
    }

    public String getZodiacElementName(@Zodiac int zodiac) {
        return zodiacElementNames[zodiac % 4];
    }

    public String getZodiacElementSymbol(@Zodiac int zodiac) {
        return zodiacElementSymbol[zodiac % 4];
    }
}
