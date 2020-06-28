package com.tmendes.birthdaydroid.date;

import android.content.Context;
import android.os.Build;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class DateLocaleHelper {
    public String getMonthString(Month month, Context context) {
        return month.getDisplayName(TextStyle.FULL, getCurrentLocale(context));
    }

    public String getDayOfWeek(DayOfWeek dayOfWeek, Context context) {
        return dayOfWeek.getDisplayName(TextStyle.FULL, getCurrentLocale(context));
    }

    public Locale getCurrentLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }
}
