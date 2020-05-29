package com.tmendes.birthdaydroid.zodiac;

import android.content.res.Resources;

import com.tmendes.birthdaydroid.R;

public class ZodiacResourceHelper {
    private final String[] zodiacNames;
    private final String[] zodiacSymbols;
    private final String[] zodiacElementNames;
    private final String[] zodiacElementSymbol;

    public ZodiacResourceHelper(Resources resources) {
        zodiacNames = resources.getStringArray(R.array.zodiac_names);
        zodiacSymbols = resources.getStringArray(R.array.zodiac_symbols);
        zodiacElementNames = resources.getStringArray(R.array.zodiac_element_names);
        zodiacElementSymbol = resources.getStringArray(R.array.zodiac_element_symbols);
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
