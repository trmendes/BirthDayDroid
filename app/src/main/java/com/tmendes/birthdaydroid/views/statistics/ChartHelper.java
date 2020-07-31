package com.tmendes.birthdaydroid.views.statistics;

import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.utils.ColorTemplate;

public class ChartHelper {
    public void setUpBarChartBaseSettings(BarChart barChart, boolean useDarkTheme) {
        barChart.setFitBars(true);
        barChart.setHighlightPerTapEnabled(true);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(false);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        barChart.setBackgroundColor(Color.TRANSPARENT);
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setDrawBorders(false);

        if (useDarkTheme) {
            barChart.getAxisLeft().setTextColor(Color.WHITE);
            barChart.getAxisRight().setTextColor(Color.WHITE);
            barChart.getXAxis().setTextColor(Color.WHITE);
        }
    }

    public int[] getColorTemplate() {
        return ColorTemplate.MATERIAL_COLORS;
    }
}
