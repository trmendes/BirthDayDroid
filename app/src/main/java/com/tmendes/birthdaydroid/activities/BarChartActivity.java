package com.tmendes.birthdaydroid.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.providers.BirthdayDataProvider;
import com.tmendes.birthdaydroid.providers.StatisticsProvider;

import java.util.ArrayList;
import java.util.Map;

public class BarChartActivity extends AppCompatActivity  implements OnChartValueSelectedListener {

    private BarChart chart;
    private final short MAX_AGE = 120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_barchart);

        float max_age = 0;
        float min_age = Integer.MAX_VALUE;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean useDarkTheme = prefs.getBoolean("dark_theme", false);

        this.chart = findViewById(R.id.barchat);
        this.chart.setHighlightPerTapEnabled(true);

        this.chart.setOnChartValueSelectedListener(this);

        this.chart.setDrawBarShadow(false);
        this.chart.setDrawValueAboveBar(false);
        this.chart.setPinchZoom(false);
        this.chart.setDrawGridBackground(true);
        this.chart.getLegend().setEnabled(false);
        this.chart.getDescription().setText(getResources().getString(R.string.statistics_age_title));
        this.chart.setDrawBorders(false);

        XAxis xAxis = this.chart.getXAxis();
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = this.chart.getAxisLeft();
        YAxis rightAxis = this.chart.getAxisRight();

        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisMinimum(0f);

        if (useDarkTheme) {
            this.chart.setBackgroundColor(Color.BLACK);
            this.chart.setDrawGridBackground(false);
            leftAxis.setTextColor(Color.WHITE);
            rightAxis.setTextColor(Color.WHITE);
            xAxis.setTextColor(Color.WHITE);
        }

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        StatisticsProvider statisticsProvider = BirthdayDataProvider.getInstance().getStatistics();

        Map<Integer, Integer> ageStat = statisticsProvider.getAgeStats();

        for (Object o : ageStat.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            int age = (int) pair.getKey();
            int number = (int) pair.getValue();
            barEntries.add(new BarEntry(age, number));
            if ((age > max_age) && (age < MAX_AGE)) {
                max_age = age;
            }
            if (age < min_age) {
                min_age = age;
            }
        }

        xAxis.setAxisMaximum(max_age);
        xAxis.setAxisMinimum(min_age);

        BarDataSet barDataSet = new BarDataSet(barEntries, "Age");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setDrawValues(false);
        barDataSet.setHighlightEnabled(true);
        barDataSet.setBarBorderWidth(0f);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);

        this.chart.setData(barData);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        String msg = Integer.toString(((int) e.getY()));
        Toast.makeText(this, msg , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }
}
