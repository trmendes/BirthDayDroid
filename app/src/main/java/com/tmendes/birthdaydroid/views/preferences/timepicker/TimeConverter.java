package com.tmendes.birthdaydroid.views.preferences.timepicker;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public class TimeConverter {
    public LocalTime millisToLocalTime(long millis) {
        return LocalTime.of(0,0).plus(millis, ChronoUnit.MILLIS);
    }

    public long localTieToMillis(LocalTime localTime) {
        return localTime.get(ChronoField.MILLI_OF_DAY);
    }
}
