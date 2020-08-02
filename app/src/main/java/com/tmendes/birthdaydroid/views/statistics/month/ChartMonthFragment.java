package com.tmendes.birthdaydroid.views.statistics.month;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.views.statistics.AbstractStatisticFragment;
import com.tmendes.birthdaydroid.views.statistics.ChartHelper;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ChartMonthFragment extends AbstractStatisticFragment implements OnChartValueSelectedListener {

    private BarChart chart;
    private ChartHelper chartHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_barchart_vertical, container, false);
        setHasOptionsMenu(true);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        final boolean useDarkTheme = prefs.getBoolean("dark_theme", false);

        this.chart = v.findViewById(R.id.barChart);

        final TextView title = v.findViewById(R.id.tvBarChartTitle);
        final String label = requireContext().getResources().getString(R.string.menu_statistics_month);
        title.setText(label);

        chartHelper = new ChartHelper();
        chartHelper.setUpBarChartBaseSettings(chart, useDarkTheme);
        chart.setOnChartValueSelectedListener(this);

        final XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(12);
        xAxis.setLabelRotationAngle(80f);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new MonthLabelFormatter(getResources()));

        // Right Y-Axis
        final YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        // Left Y-Axis
        final YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setGranularityEnabled(true);
        leftAxis.setGranularity(1f);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisMinimum(0f);

        return v;
    }

    @Override
    protected void updateContacts(List<Contact> contacts) {
        final Map<Month, Integer> monthMap = contacts.stream()
                .filter(c -> !c.isIgnore())
                .collect(Collectors.toMap(c -> c.getBornOn().getMonth(), c -> 1, Integer::sum, TreeMap::new));

        final ArrayList<BarEntry> barEntries = new ArrayList<>();

        for (Month month : Month.values()) {
            Integer count = monthMap.get(month);
            if(count == null) {
                count = 0;
            }
            BarEntry entry = new BarEntry(month.getValue(), count);
            barEntries.add(entry);
        }

        final BarDataSet barDataSet = new BarDataSet(barEntries, null);
        barDataSet.setColors(chartHelper.getColorTemplate());
        barDataSet.setDrawValues(false);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);

        this.chart.setData(barData);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        String msg = Integer.toString(((int) e.getY()));
        Toast.makeText(getContext(), msg , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    protected int getStatisticViewType() {
        return CHART_VIEW;
    }

    @Override
    protected int getCorrespondingTextOrDiagramNavId() {
        return R.id.nav_statistics_month_text;
    }
}