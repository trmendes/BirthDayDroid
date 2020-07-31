package com.tmendes.birthdaydroid.views.statistics.dayofweek;

import android.content.res.Resources;
import android.os.Build;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.sql.SQLOutput;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

public class DayOfWeekLabelFormatter extends ValueFormatter {

    private final Locale locale;

    public DayOfWeekLabelFormatter(Resources resources) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = resources.getConfiguration().getLocales().get(0);
        } else {
            locale = resources.getConfiguration().locale;
        }
    }

    @Override
    public String getFormattedValue(float value) {
        int dayOfWeekValue = Float.valueOf(value).intValue();
        try {
            return DayOfWeek.of(dayOfWeekValue).getDisplayName(TextStyle.FULL, locale);
        } catch (DateTimeException e) {
            return "";
        }
    }
}
