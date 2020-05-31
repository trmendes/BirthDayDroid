package com.tmendes.birthdaydroid.date;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Year;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class EventDateConverterTest {
    private EventDateConverter converter;

    @Before
    public void setUp() {
        converter = new EventDateConverter();
    }

    @Test
    public void testYearMonthDayWithoutTime() {
        EventDateConverter.DateConverterResult result1 = converter.convert("2020-01-02");
        assertThat(result1.isSuccess(), is(true));
        assertThat(result1.getMissingYearInfo(), is(false));
        assertThat(result1.getDate(), is(LocalDate.of(2020, 1, 2)));

        EventDateConverter.DateConverterResult result2 = converter.convert("2020-1-02");
        assertThat(result2.isSuccess(), is(true));
        assertThat(result2.getMissingYearInfo(), is(false));
        assertThat(result2.getDate(), is(LocalDate.of(2020, 1, 2)));

        EventDateConverter.DateConverterResult result3 = converter.convert("2020-01-2");
        assertThat(result3.isSuccess(), is(true));
        assertThat(result3.getMissingYearInfo(), is(false));
        assertThat(result3.getDate(), is(LocalDate.of(2020, 1, 2)));

        EventDateConverter.DateConverterResult result4 = converter.convert("2020-1-2");
        assertThat(result4.isSuccess(), is(true));
        assertThat(result4.getMissingYearInfo(), is(false));
        assertThat(result4.getDate(), is(LocalDate.of(2020, 1, 2)));
    }

    @Test
    public void testYearMonthDayWithTime() {
        EventDateConverter.DateConverterResult result1 = converter.convert("2020-01-02 03:04:05.678");
        assertThat(result1.isSuccess(), is(true));
        assertThat(result1.getMissingYearInfo(), is(false));
        assertThat(result1.getDate(), is(LocalDate.of(2020, 1, 2)));
    }

    @Test
    public void testDayMonthYearWithoutTime() {
        EventDateConverter.DateConverterResult result1 = converter.convert("02-01-2020");
        assertThat(result1.isSuccess(), is(true));
        assertThat(result1.getMissingYearInfo(), is(false));
        assertThat(result1.getDate(), is(LocalDate.of(2020, 1, 2)));

        EventDateConverter.DateConverterResult result2 = converter.convert("2-01-2020");
        assertThat(result2.isSuccess(), is(true));
        assertThat(result2.getMissingYearInfo(), is(false));
        assertThat(result2.getDate(), is(LocalDate.of(2020, 1, 2)));

        EventDateConverter.DateConverterResult result3 = converter.convert("02-1-2020");
        assertThat(result3.isSuccess(), is(true));
        assertThat(result3.getMissingYearInfo(), is(false));
        assertThat(result3.getDate(), is(LocalDate.of(2020, 1, 2)));

        EventDateConverter.DateConverterResult result4 = converter.convert("2-1-2020");
        assertThat(result4.isSuccess(), is(true));
        assertThat(result4.getMissingYearInfo(), is(false));
        assertThat(result4.getDate(), is(LocalDate.of(2020, 1, 2)));
    }

    @Test
    public void testDayMonthYearWithTime() {
        EventDateConverter.DateConverterResult result1 = converter.convert("02-01-2020 03:04:05.678");
        assertThat(result1.isSuccess(), is(true));
        assertThat(result1.getMissingYearInfo(), is(false));
        assertThat(result1.getDate(), is(LocalDate.of(2020, 1, 2)));
    }

    @Test
    public void testMonthDayWithoutTime() {
        EventDateConverter.DateConverterResult result1 = converter.convert("--01-02");
        assertThat(result1.isSuccess(), is(true));
        assertThat(result1.getMissingYearInfo(), is(true));
        assertThat(result1.getDate(), is(LocalDate.of(Year.now().getValue(), 1, 2)));

        EventDateConverter.DateConverterResult result2 = converter.convert("--1-02");
        assertThat(result2.isSuccess(), is(true));
        assertThat(result2.getMissingYearInfo(), is(true));
        assertThat(result2.getDate(), is(LocalDate.of(Year.now().getValue(), 1, 2)));

        EventDateConverter.DateConverterResult result3 = converter.convert("--01-2");
        assertThat(result3.isSuccess(), is(true));
        assertThat(result3.getMissingYearInfo(), is(true));
        assertThat(result3.getDate(), is(LocalDate.of(Year.now().getValue(), 1, 2)));

        EventDateConverter.DateConverterResult result4 = converter.convert("--1-2");
        assertThat(result4.isSuccess(), is(true));
        assertThat(result4.getMissingYearInfo(), is(true));
        assertThat(result4.getDate(), is(LocalDate.of(Year.now().getValue(), 1, 2)));
    }

    @Test
    public void testMonthDayWithTime() {
        EventDateConverter.DateConverterResult result1 = converter.convert("--01-02 03:04:05.678");
        assertThat(result1.isSuccess(), is(true));
        assertThat(result1.getMissingYearInfo(), is(true));
        assertThat(result1.getDate(), is(LocalDate.of(Year.now().getValue(), 1, 2)));
    }

    @Test
    public void testDayMonthWithoutTime() {
        EventDateConverter.DateConverterResult result1 = converter.convert("02-01--");
        assertThat(result1.isSuccess(), is(true));
        assertThat(result1.getMissingYearInfo(), is(true));
        assertThat(result1.getDate(), is(LocalDate.of(Year.now().getValue(), 1, 2)));

        EventDateConverter.DateConverterResult result2 = converter.convert("2-01--");
        assertThat(result2.isSuccess(), is(true));
        assertThat(result2.getMissingYearInfo(), is(true));
        assertThat(result2.getDate(), is(LocalDate.of(Year.now().getValue(), 1, 2)));

        EventDateConverter.DateConverterResult result3 = converter.convert("02-1--");
        assertThat(result3.isSuccess(), is(true));
        assertThat(result3.getMissingYearInfo(), is(true));
        assertThat(result3.getDate(), is(LocalDate.of(Year.now().getValue(), 1, 2)));

        EventDateConverter.DateConverterResult result4 = converter.convert("2-1--");
        assertThat(result4.isSuccess(), is(true));
        assertThat(result4.getMissingYearInfo(), is(true));
        assertThat(result4.getDate(), is(LocalDate.of(Year.now().getValue(), 1, 2)));
    }

    @Test
    public void testDayMonthWithTime() {
        EventDateConverter.DateConverterResult result1 = converter.convert("02-01-- 03:04:05.678");
        assertThat(result1.isSuccess(), is(true));
        assertThat(result1.getMissingYearInfo(), is(true));
        assertThat(result1.getDate(), is(LocalDate.of(Year.now().getValue(), 1, 2)));
    }

    @Test
    public void testUnparsableDate() {
        EventDateConverter.DateConverterResult result1 = converter.convert("1234-1234-1234");
        assertThat(result1.isSuccess(), is(false));
        assertThat(result1.getMissingYearInfo(), nullValue());
        assertThat(result1.getDate(), nullValue());
    }
}
