package com.tmendes.birthdaydroid.views.statistics.month;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.views.statistics.AbstractStatisticFragment;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class PieChartMonthFragment extends AbstractStatisticFragment implements OnChartValueSelectedListener {

    private PieChart chart;
    private String label;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_piechart, container, false);
        setHasOptionsMenu(true);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean useDarkTheme = prefs.getBoolean("dark_theme", false);

        this.chart = v.findViewById(R.id.pieChart);

        TextView title = v.findViewById(R.id.tvPieChartTitle);
        label = requireContext().getResources().getString(R.string.menu_statistics_month);
        title.setText(label);

        this.chart.getDescription().setEnabled(false);

        this.chart.setOnChartValueSelectedListener(this);

        this.chart.setHighlightPerTapEnabled(true);

        chart.setBackgroundColor(Color.TRANSPARENT);
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
            this.chart.setHoleColor(Color.BLACK);
        }

        return v;
    }

    @Override
    protected void updateContacts(List<Contact> contacts) {
        final Map<Month, Integer> monthMap = contacts.stream()
                .filter(c -> !c.isIgnore())
                .collect(Collectors.toMap(c -> c.getBornOn().getMonth(), c -> 1, Integer::sum, TreeMap::new));

        final ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<Month, Integer> pair : monthMap.entrySet()) {
            final Month month = pair.getKey();
            final int quantity = pair.getValue();
            final PieEntry entry = new PieEntry(quantity, month);
            final Locale locale;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = getResources().getConfiguration().getLocales().get(0);
            } else {
                locale = getResources().getConfiguration().locale;
            }
            String monthString = month.getDisplayName(TextStyle.FULL, locale);
            entry.setLabel(monthString);
            pieEntries.add(entry);
        }

        final PieDataSet pieDataSet = new PieDataSet(pieEntries, label);
        pieDataSet.setSliceSpace(1f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        final PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.YELLOW);

        this.chart.setData(pieData);
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
        return R.id.nav_statistics_month_text;
    }
}