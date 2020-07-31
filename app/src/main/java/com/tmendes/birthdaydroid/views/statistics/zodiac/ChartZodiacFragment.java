package com.tmendes.birthdaydroid.views.statistics.zodiac;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.views.statistics.ChartHelper;
import com.tmendes.birthdaydroid.zodiac.Zodiac;
import com.tmendes.birthdaydroid.zodiac.ZodiacResourceHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ChartZodiacFragment extends AbstractZodiacFragment implements OnChartValueSelectedListener {
    private BarChart chart;
    private ZodiacResourceHelper zodiacResourceHelper;
    private ChartHelper chartHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zodiacResourceHelper = new ZodiacResourceHelper(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_barchart_vertical, container, false);
        setHasOptionsMenu(true);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        final boolean useDarkTheme = prefs.getBoolean("dark_theme", false);

        this.chart = v.findViewById(R.id.barChart);

        final TextView title = v.findViewById(R.id.tvBarChartTitle);
        final String label = requireContext().getResources().getString(R.string.menu_statistics_zodiac);
        title.setText(label);

        chartHelper = new ChartHelper();
        chartHelper.setUpBarChartBaseSettings(chart, useDarkTheme);
        chart.setOnChartValueSelectedListener(this);

        final XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(12);
        xAxis.setLabelRotationAngle(80f);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ZodiacLabelFormatter(zodiacResourceHelper));

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
        final Map<Integer, Integer> zodiacMap = contacts.stream()
                .filter(c -> !c.isIgnore())
                .collect(Collectors.toMap(Contact::getZodiac, c -> 1, Integer::sum, TreeMap::new));

        final ArrayList<BarEntry> barEntries = new ArrayList<>();

        for (@Zodiac int zodiac = 0; zodiac < 12; zodiac++) {
            Integer count = zodiacMap.get(zodiac);
            if(count == null) {
                count = 0;
            }
            BarEntry entry = new BarEntry(zodiac, count);
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
        return R.id.nav_statistics_zodiac_text;
    }
}