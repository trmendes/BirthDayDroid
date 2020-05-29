package com.tmendes.birthdaydroid.zodiac;

import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import static com.tmendes.birthdaydroid.comparators.BirthDayComparatorFactory.SORT_ORDER_DESC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

public class ZodiacCalculatorExceptionTest {

    @Test
    public void testNullDate() {
        final ZodiacCalculator zodiacCalculator = new ZodiacCalculator();

        Assert.assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() {
                zodiacCalculator.calculateZodiac(null);
            }
        });
    }
}
