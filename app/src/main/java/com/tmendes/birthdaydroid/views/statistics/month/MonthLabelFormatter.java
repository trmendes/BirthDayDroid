package com.tmendes.birthdaydroid.views.statistics.month;

import android.content.res.Resources;
import android.os.Build;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class MonthLabelFormatter extends ValueFormatter {

    private final Locale locale;

    public MonthLabelFormatter(Resources resources) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = resources.getConfiguration().getLocales().get(0);
        } else {
            locale = resources.getConfiguration().locale;
        }
    }

    @Override
    public String getFormattedValue(float value) {
        int monthValue = Float.valueOf(value).intValue();
        try {
            return Month.of(monthValue).getDisplayName(TextStyle.FULL, locale);
        } catch (DateTimeException e) {
            return "";
        }
    }
}
