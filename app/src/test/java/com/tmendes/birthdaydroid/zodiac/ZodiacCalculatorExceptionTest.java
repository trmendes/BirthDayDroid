package com.tmendes.birthdaydroid.zodiac;

import org.junit.Assert;
import org.junit.Test;

public class ZodiacCalculatorExceptionTest {

    @Test
    public void testNullDate() {
        final ZodiacCalculator zodiacCalculator = new ZodiacCalculator();

        Assert.assertThrows(IllegalArgumentException.class,
                () -> zodiacCalculator.calculateZodiac(null));
    }
}
