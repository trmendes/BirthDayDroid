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
import com.tmendes.birthdaydroid.views.AbstractContactsFragment;
import com.tmendes.birthdaydroid.zodiac.Zodiac;
import com.tmendes.birthdaydroid.zodiac.ZodiacResourceHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PieChartZodiacFragment extends AbstractContactsFragment implements OnChartValueSelectedListener {
    private PieChart chart;
    private String label;
    private ZodiacResourceHelper zodiacResourceHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zodiacResourceHelper = new ZodiacResourceHelper(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_piechart, container, false);
        setHasOptionsMenu(true);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean useDarkTheme = prefs.getBoolean("dark_theme", false);

        this.chart = v.findViewById(R.id.pieChart);

        TextView title = v.findViewById(R.id.tvPieChartTitle);
        label = requireContext().getResources().getString(R.string.menu_statistics_zodiac);
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
        final Map<Integer, Integer> zodiacMap = contacts.stream()
                .filter(c -> !c.isIgnore())
                .collect(Collectors.toMap(Contact::getZodiac, c -> 1, Integer::sum));

        final ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<Integer, Integer> pair : zodiacMap.entrySet()) {
            @Zodiac int zodiac = pair.getKey();
            int number = pair.getValue();
            PieEntry entry = new PieEntry(number, zodiac);
            entry.setLabel(zodiacResourceHelper.getZodiacName(zodiac));
            pieEntries.add(entry);
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, label);
        pieDataSet.setSliceSpace(1f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData pieData = new PieData(pieDataSet);
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
}