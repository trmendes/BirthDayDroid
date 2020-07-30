package com.tmendes.birthdaydroid.views.statistics.zodiac;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.views.statistics.AbstractStatisticFragment;
import com.tmendes.birthdaydroid.zodiac.Zodiac;
import com.tmendes.birthdaydroid.zodiac.ZodiacResourceHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class BarChartZodiacFragment extends AbstractZodiacFragment implements OnChartValueSelectedListener {
    private BarChart chart;
    private String label;
    private ZodiacResourceHelper zodiacResourceHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zodiacResourceHelper = new ZodiacResourceHelper(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_barchart_vertical, container, false);
        setHasOptionsMenu(true);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        this.chart = v.findViewById(R.id.barChart);

        TextView title = v.findViewById(R.id.tvBarChartTitle);
        label = requireContext().getResources().getString(R.string.menu_statistics_zodiac);
        title.setText(label);

        chart.setFitBars(true);
        chart.setHighlightPerTapEnabled(true);
        chart.setOnChartValueSelectedListener(this);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(false);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawBorders(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(12);
        xAxis.setLabelRotationAngle(80f);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                @Zodiac int zodiac = Float.valueOf(value).intValue();
                return zodiacResourceHelper.getZodiacSymbol(zodiac) + " " + zodiacResourceHelper.getZodiacName(zodiac);
            }
        });

        // Right Y-Axis
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        // Left Y-Axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setGranularityEnabled(true);
        leftAxis.setGranularity(1f);

        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisMinimum(0f);

        boolean useDarkTheme = prefs.getBoolean("dark_theme", false);
        if (useDarkTheme) {
            leftAxis.setTextColor(Color.WHITE);
            rightAxis.setTextColor(Color.WHITE);
            xAxis.setTextColor(Color.WHITE);
        }

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

        final BarDataSet barDataSet = new BarDataSet(barEntries, label);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
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
        return DIAGRAM_VIEW;
    }

    @Override
    protected int getCorrespondingTextOrDiagramNavId() {
        return R.id.nav_statistics_zodiac_text;
    }
}