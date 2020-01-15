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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.providers.BirthdayDataProvider;
import com.tmendes.birthdaydroid.providers.StatisticsProvider;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Map;

public class PieChartMonthFragment extends Fragment implements OnChartValueSelectedListener {

    @SuppressWarnings("FieldCanBeLocal")
    private PieChart chart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_piechart, container, false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean useDarkTheme = prefs.getBoolean("dark_theme", false);

        this.chart = v.findViewById(R.id.pieChart);
        this.chart.getDescription().setEnabled(false);

        this.chart.setOnChartValueSelectedListener(this);

        this.chart.setHighlightPerTapEnabled(true);

        this.chart.setUsePercentValues(true);
        this.chart.getDescription().setEnabled(false);
        this.chart.setExtraOffsets(5, 10, 5, 5);
        this.chart.setDragDecelerationFrictionCoef(0.95f);
        this.chart.getLegend().setEnabled(false);

        this.chart.setDrawHoleEnabled(true);
        this.chart.setHoleRadius(50f);
        this.chart.setTransparentCircleRadius(10);

        this.chart.setDrawCenterText(true);

        if (useDarkTheme) {
            this.chart.setBackgroundColor(Color.BLACK);
            this.chart.setHoleColor(Color.BLACK);
        }

        this.chart.animateXY(1400, 1400);

        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        StatisticsProvider statisticsProvider = BirthdayDataProvider.getInstance().getStatistics();

        String label = "Month";

        Map<Integer, Integer> monthMap;
        monthMap = statisticsProvider.getMonthStats();
        for (Object o : monthMap.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            int month = (int) pair.getKey();
            int quantity = (int) pair.getValue();
            PieEntry entry = new PieEntry(quantity, month);
            String monthString = new DateFormatSymbols().getMonths()[month];
            entry.setLabel(monthString);
            pieEntries.add(entry);
        }

/*
        this.chart.getDescription().setText(getResources().getString(R.string.statistics_week_title));
*/

        PieDataSet pieDataSet = new PieDataSet(pieEntries, label);
        pieDataSet.setSliceSpace(1f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.YELLOW);

        this.chart.setData(pieData);

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