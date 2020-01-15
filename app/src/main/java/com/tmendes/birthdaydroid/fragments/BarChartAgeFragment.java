package com.tmendes.birthdaydroid.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class BarChartAgeFragment extends Fragment implements OnChartValueSelectedListener {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_barchart, container, false);

        float max_age = 0;
        float min_age = Integer.MAX_VALUE;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean useDarkTheme = prefs.getBoolean("dark_theme", false);

        BarChart chart = v.findViewById(R.id.barChart);
        chart.setHighlightPerTapEnabled(true);

        chart.setOnChartValueSelectedListener(this);

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(false);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(true);
        chart.getLegend().setEnabled(false);
        /*chart.getDescription().setText(getResources().getString(R.string.statistics_age_title));*/
        chart.setDrawBorders(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();

        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisMinimum(0f);

        if (useDarkTheme) {
            chart.setBackgroundColor(Color.BLACK);
            chart.setDrawGridBackground(false);
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
            short MAX_AGE = 120;
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

        chart.setData(barData);

        return v;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        String msg = Integer.toString(((int) e.getY()));
        Toast.makeText(getContext(), msg , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }
}