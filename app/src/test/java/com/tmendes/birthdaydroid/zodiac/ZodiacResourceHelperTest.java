package com.tmendes.birthdaydroid.zodiac;

import android.content.Context;
import android.content.res.Resources;

import com.tmendes.birthdaydroid.R;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ZodiacResourceHelperTest {

    private static Context context;
    private ZodiacResourceHelper zodiacResourceHelper;

    @BeforeClass
    public static void setUpClass() {
        Resources resources = mock(Resources.class);

        when(resources.getString(R.string.sign_capricorn)).thenReturn("zn1");
        when(resources.getString(R.string.sign_aquarius)).thenReturn("zn2");
        when(resources.getString(R.string.sign_pisces)).thenReturn("zn3");
        when(resources.getString(R.string.sign_aries)).thenReturn("zn4");
        when(resources.getString(R.string.sign_taurus)).thenReturn("zn5");
        when(resources.getString(R.string.sign_gemini)).thenReturn("zn6");
        when(resources.getString(R.string.sign_cancer)).thenReturn("zn7");
        when(resources.getString(R.string.sign_leo)).thenReturn("zn8");
        when(resources.getString(R.string.sign_virgo)).thenReturn("zn9");
        when(resources.getString(R.string.sign_libra)).thenReturn("zn10");
        when(resources.getString(R.string.sign_scorpio)).thenReturn("zn11");
        when(resources.getString(R.string.sign_sagittarius)).thenReturn("zn12");

        when(resources.getString(R.string.sign_capricorn_symbol)).thenReturn("zs1");
        when(resources.getString(R.string.sign_aquarius_symbol)).thenReturn("zs2");
        when(resources.getString(R.string.sign_pisces_symbol)).thenReturn("zs3");
        when(resources.getString(R.string.sign_aries_symbol)).thenReturn("zs4");
        when(resources.getString(R.string.sign_taurus_symbol)).thenReturn("zs5");
        when(resources.getString(R.string.sign_gemini_symbol)).thenReturn("zs6");
        when(resources.getString(R.string.sign_cancer_symbol)).thenReturn("zs7");
        when(resources.getString(R.string.sign_leo_symbol)).thenReturn("zs8");
        when(resources.getString(R.string.sign_virgo_symbol)).thenReturn("zs9");
        when(resources.getString(R.string.sign_libra_symbol)).thenReturn("zs10");
        when(resources.getString(R.string.sign_scorpio_symbol)).thenReturn("zs11");
        when(resources.getString(R.string.sign_sagittarius_symbol)).thenReturn("zs12");

        when(resources.getString(R.string.sign_element_earth)).thenReturn("zen1");
        when(resources.getString(R.string.sign_element_air)).thenReturn("zen2");
        when(resources.getString(R.string.sign_element_water)).thenReturn("zen3");
        when(resources.getString(R.string.sign_element_fire)).thenReturn("zen4");

        when(resources.getString(R.string.sign_element_earth_symbol)).thenReturn("zes1");
        when(resources.getString(R.string.sign_element_air_symbol)).thenReturn("zes2");
        when(resources.getString(R.string.sign_element_water_symbol)).thenReturn("zes3");
        when(resources.getString(R.string.sign_element_fire_symbol)).thenReturn("zes4");

        context = mock(Context.class);
        when(context.getResources()).thenReturn(resources);
    }

    @Before
    public void setUp() {
        zodiacResourceHelper = new ZodiacResourceHelper(context);
    }

    @Test
    public void testZodiacNames() {
        String resource1 = zodiacResourceHelper.getZodiacName(Zodiac.CAPRICORN);
        String resource2 = zodiacResourceHelper.getZodiacName(Zodiac.AQUARIUS);
        String resource3 = zodiacResourceHelper.getZodiacName(Zodiac.PISCES);
        String resource4 = zodiacResourceHelper.getZodiacName(Zodiac.ARIES);
        String resource5 = zodiacResourceHelper.getZodiacName(Zodiac.TAURUS);
        String resource6 = zodiacResourceHelper.getZodiacName(Zodiac.GEMINI);
        String resource7 = zodiacResourceHelper.getZodiacName(Zodiac.CANCER);
        String resource8 = zodiacResourceHelper.getZodiacName(Zodiac.LEO);
        String resource9 = zodiacResourceHelper.getZodiacName(Zodiac.VIRGO);
        String resource10 = zodiacResourceHelper.getZodiacName(Zodiac.LIBRA);
        String resource11 = zodiacResourceHelper.getZodiacName(Zodiac.SCORPIO);
        String resource12 = zodiacResourceHelper.getZodiacName(Zodiac.SAGITTARIUS);

        assertThat(resource1, is("zn1"));
        assertThat(resource2, is("zn2"));
        assertThat(resource3, is("zn3"));
        assertThat(resource4, is("zn4"));
        assertThat(resource5, is("zn5"));
        assertThat(resource6, is("zn6"));
        assertThat(resource7, is("zn7"));
        assertThat(resource8, is("zn8"));
        assertThat(resource9, is("zn9"));
        assertThat(resource10, is("zn10"));
        assertThat(resource11, is("zn11"));
        assertThat(resource12, is("zn12"));
    }

    @Test
    public void testZodiacSymbol() {
        String resource1 = zodiacResourceHelper.getZodiacSymbol(Zodiac.CAPRICORN);
        String resource2 = zodiacResourceHelper.getZodiacSymbol(Zodiac.AQUARIUS);
        String resource3 = zodiacResourceHelper.getZodiacSymbol(Zodiac.PISCES);
        String resource4 = zodiacResourceHelper.getZodiacSymbol(Zodiac.ARIES);
        String resource5 = zodiacResourceHelper.getZodiacSymbol(Zodiac.TAURUS);
        String resource6 = zodiacResourceHelper.getZodiacSymbol(Zodiac.GEMINI);
        String resource7 = zodiacResourceHelper.getZodiacSymbol(Zodiac.CANCER);
        String resource8 = zodiacResourceHelper.getZodiacSymbol(Zodiac.LEO);
        String resource9 = zodiacResourceHelper.getZodiacSymbol(Zodiac.VIRGO);
        String resource10 = zodiacResourceHelper.getZodiacSymbol(Zodiac.LIBRA);
        String resource11 = zodiacResourceHelper.getZodiacSymbol(Zodiac.SCORPIO);
        String resource12 = zodiacResourceHelper.getZodiacSymbol(Zodiac.SAGITTARIUS);

        assertThat(resource1, is("zs1"));
        assertThat(resource2, is("zs2"));
        assertThat(resource3, is("zs3"));
        assertThat(resource4, is("zs4"));
        assertThat(resource5, is("zs5"));
        assertThat(resource6, is("zs6"));
        assertThat(resource7, is("zs7"));
        assertThat(resource8, is("zs8"));
        assertThat(resource9, is("zs9"));
        assertThat(resource10, is("zs10"));
        assertThat(resource11, is("zs11"));
        assertThat(resource12, is("zs12"));
    }

    @Test
    public void testZodiacElementName() {
        String resource1 = zodiacResourceHelper.getZodiacElementName(Zodiac.CAPRICORN);
        String resource2 = zodiacResourceHelper.getZodiacElementName(Zodiac.AQUARIUS);
        String resource3 = zodiacResourceHelper.getZodiacElementName(Zodiac.PISCES);
        String resource4 = zodiacResourceHelper.getZodiacElementName(Zodiac.ARIES);
        String resource5 = zodiacResourceHelper.getZodiacElementName(Zodiac.TAURUS);
        String resource6 = zodiacResourceHelper.getZodiacElementName(Zodiac.GEMINI);
        String resource7 = zodiacResourceHelper.getZodiacElementName(Zodiac.CANCER);
        String resource8 = zodiacResourceHelper.getZodiacElementName(Zodiac.LEO);
        String resource9 = zodiacResourceHelper.getZodiacElementName(Zodiac.VIRGO);
        String resource10 = zodiacResourceHelper.getZodiacElementName(Zodiac.LIBRA);
        String resource11 = zodiacResourceHelper.getZodiacElementName(Zodiac.SCORPIO);
        String resource12 = zodiacResourceHelper.getZodiacElementName(Zodiac.SAGITTARIUS);

        assertThat(resource1, is("zen1"));
        assertThat(resource2, is("zen2"));
        assertThat(resource3, is("zen3"));
        assertThat(resource4, is("zen4"));
        assertThat(resource5, is("zen1"));
        assertThat(resource6, is("zen2"));
        assertThat(resource7, is("zen3"));
        assertThat(resource8, is("zen4"));
        assertThat(resource9, is("zen1"));
        assertThat(resource10, is("zen2"));
        assertThat(resource11, is("zen3"));
        assertThat(resource12, is("zen4"));
    }

    @Test
    public void testZodiacElementSymbol() {
        String resource1 = zodiacResourceHelper.getZodiacElementSymbol(Zodiac.CAPRICORN);
        String resource2 = zodiacResourceHelper.getZodiacElementSymbol(Zodiac.AQUARIUS);
        String resource3 = zodiacResourceHelper.getZodiacElementSymbol(Zodiac.PISCES);
        String resource4 = zodiacResourceHelper.getZodiacElementSymbol(Zodiac.ARIES);
        String resource5 = zodiacResourceHelper.getZodiacElementSymbol(Zodiac.TAURUS);
        String resource6 = zodiacResourceHelper.getZodiacElementSymbol(Zodiac.GEMINI);
        String resource7 = zodiacResourceHelper.getZodiacElementSymbol(Zodiac.CANCER);
        String resource8 = zodiacResourceHelper.getZodiacElementSymbol(Zodiac.LEO);
        String resource9 = zodiacResourceHelper.getZodiacElementSymbol(Zodiac.VIRGO);
        String resource10 = zodiacResourceHelper.getZodiacElementSymbol(Zodiac.LIBRA);
        String resource11 = zodiacResourceHelper.getZodiacElementSymbol(Zodiac.SCORPIO);
        String resource12 = zodiacResourceHelper.getZodiacElementSymbol(Zodiac.SAGITTARIUS);

        assertThat(resource1, is("zes1"));
        assertThat(resource2, is("zes2"));
        assertThat(resource3, is("zes3"));
        assertThat(resource4, is("zes4"));
        assertThat(resource5, is("zes1"));
        assertThat(resource6, is("zes2"));
        assertThat(resource7, is("zes3"));
        assertThat(resource8, is("zes4"));
        assertThat(resource9, is("zes1"));
        assertThat(resource10, is("zes2"));
        assertThat(resource11, is("zes3"));
        assertThat(resource12, is("zes4"));
    }
}
