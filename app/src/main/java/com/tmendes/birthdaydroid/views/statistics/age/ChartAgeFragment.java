package com.tmendes.birthdaydroid.views.statistics.age;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.github.mikephil.charting.charts.HorizontalBarChart;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ChartAgeFragment extends AbstractStatisticFragment implements OnChartValueSelectedListener {

    private HorizontalBarChart chart;
    private ChartHelper chartHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_barchart, container, false);
        setHasOptionsMenu(true);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        final boolean useDarkTheme = prefs.getBoolean("dark_theme", false);

        chart = v.findViewById(R.id.barChart);
        final TextView title = v.findViewById(R.id.tvBarChartTitle);
        title.setText(getResources().getString(R.string.menu_statistics_age));

        chartHelper = new ChartHelper();
        chartHelper.setUpBarChartBaseSettings(chart, useDarkTheme);
        chart.setOnChartValueSelectedListener(this);

        XAxis xAxis = chart.getXAxis(); // LEFT and RIGHT
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);

        YAxis rightAxis = chart.getAxisRight(); //TOP
        rightAxis.setDrawLabels(true);
        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        YAxis leftAxis = chart.getAxisLeft(); //BOTTOM
        leftAxis.setEnabled(false);

        return v;
    }

    @Override
    protected void updateContacts(List<Contact> contacts) {
        final ArrayList<BarEntry> barEntries = new ArrayList<>();
        float max_age = 0;

        final Map<Integer, Integer> ageStat = contacts.stream()
                .filter(c -> !c.isIgnore())
                .filter(c -> !c.isMissingYearInfo()) // Remove unknown year from statistic
                .collect(Collectors.toMap(Contact::getAgeInYears, c -> 1, Integer::sum, TreeMap::new));

        for (Map.Entry<Integer, Integer> pair : ageStat.entrySet()) {
            int age = pair.getKey();
            int number = pair.getValue();
            barEntries.add(new BarEntry(age, number));
            if ((age > max_age)) {
                max_age = age;
            }
        }

        final BarDataSet barDataSet = new BarDataSet(barEntries, requireContext().getResources().getString(R.string.array_order_age));
        barDataSet.setColors(chartHelper.getColorTemplate());
        barDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        barDataSet.setDrawValues(false);

        final BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);

        chart.getXAxis().setAxisMaximum(max_age + 0.5f);
        chart.getXAxis().setAxisMinimum(-0.5f);
        chart.setData(barData);
    }

    @Override
    protected int getStatisticViewType() {
        return CHART_VIEW;
    }

    @Override
    protected int getCorrespondingTextOrDiagramNavId() {
        return R.id.nav_statistics_age_text;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        String msg = (int) e.getX() + ": " + (int) e.getY();
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }
}