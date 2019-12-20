package com.tmendes.birthdaydroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Map;

public class BarChartActivity extends AppCompatActivity {

    private BarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_barchart);

        this.chart = findViewById(R.id.barchat);

        this.chart.setDrawBarShadow(false);
        this.chart.setDrawValueAboveBar(false);
        this.chart.setPinchZoom(false);
        this.chart.setDrawGridBackground(true);
        this.chart.getLegend().setEnabled(false);
        this.chart.getDescription().setText(getResources().getString(R.string.statistics_age_title));

        XAxis xAxis = this.chart.getXAxis();
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = this.chart.getAxisLeft();
        YAxis rightAxis = this.chart.getAxisRight();

        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisMinimum(0f);
        rightAxis.setAxisMinimum(0f);

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        StatisticsProvider statisticsProvider = StatisticsProvider.getInstance();

        Map<Integer, Integer> ageStat = statisticsProvider.getAgeStats();

        for (Object o : ageStat.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            int age = (int) pair.getKey();
            int number = (int) pair.getValue();
            barEntries.add(new BarEntry(age, number));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Age");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setDrawValues(false);
        barDataSet.setHighlightEnabled(false);
        barDataSet.setBarBorderWidth(0f);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);

        this.chart.setData(barData);
    }
}
