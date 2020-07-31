package com.tmendes.birthdaydroid.views.statistics.zodiac;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.tmendes.birthdaydroid.zodiac.Zodiac;
import com.tmendes.birthdaydroid.zodiac.ZodiacResourceHelper;

import java.time.DateTimeException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class ZodiacLabelFormatter extends ValueFormatter {

    private final ZodiacResourceHelper zodiacResourceHelper;

    public ZodiacLabelFormatter(ZodiacResourceHelper zodiacResourceHelper) {
        this.zodiacResourceHelper = zodiacResourceHelper;
    }

    @Override
    public String getFormattedValue(float value) {
        @Zodiac int zodiac = Float.valueOf(value).intValue();
        if (0 <= zodiac && zodiac <= 11) {
            return zodiacResourceHelper.getZodiacSymbol(zodiac) + " " + zodiacResourceHelper.getZodiacName(zodiac);
        } else {
            return "";
        }
    }
}
