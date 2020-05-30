package com.tmendes.birthdaydroid.contact;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateConverter {
    private static final String[] DATA_FORMATS_WITH_YEAR = new String[] {
            "yyyy-M[M]-d[d][ hh:mm[:ss[.SSS]]]",
            "d[d]-M[M]-yyyy[ hh:mm[:ss[.SSS]]]",
    };

    private static final String[] DATA_FORMATS_WITHOUT_YEAR = new String[] {
            "--M[M]-d[d][ hh:mm[:ss[.SSS]]]",
            "d[d]-M[M]--[ hh:mm[:ss[.SSS]]]",
    };

    public DateConverterResult convert(String dateString) {
        for (String pattern : DATA_FORMATS_WITH_YEAR) {
            try {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
                LocalDate localDate = LocalDate.parse(dateString, dateTimeFormatter);

                return DateConverterResult.createSuccess(false, localDate);
            } catch (DateTimeParseException ignored) {
            }
        }

        for (String pattern : DATA_FORMATS_WITHOUT_YEAR) {
            try {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
                MonthDay monthDay = MonthDay.parse(dateString, dateTimeFormatter);
                LocalDate localDate = monthDay.atYear(Year.now().getValue());

                return DateConverterResult.createSuccess(true, localDate);
            } catch (DateTimeParseException ignored) {
            }
        }

        return DateConverterResult.createNotSuccess();
    }


    public static class DateConverterResult {
        private final boolean success;
        private final Boolean missingYearInfo;
        private final LocalDate date;

        private static DateConverterResult createNotSuccess() {
            return new DateConverterResult(false, null, null);
        }

        private static DateConverterResult createSuccess(boolean missingYearInfo, LocalDate date) {
            return new DateConverterResult(true, missingYearInfo, date);
        }

        private DateConverterResult(boolean success, Boolean missingYearInfo, LocalDate date) {
            this.success = success;
            this.missingYearInfo = missingYearInfo;
            this.date = date;
        }

        public boolean isSuccess() {
            return success;
        }

        public Boolean getMissingYearInfo() {
            return missingYearInfo;
        }

        public LocalDate getDate() {
            return date;
        }
    }
}
