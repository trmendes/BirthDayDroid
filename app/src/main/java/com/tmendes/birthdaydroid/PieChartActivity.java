package com.tmendes.birthdaydroid;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
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

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Map;

public class PieChartActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private PieChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_piechart);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean useDarkTheme = prefs.getBoolean("dark_theme", false);

        this.chart = findViewById(R.id.piechart);

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

        String label = "";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int statistic = extras.getInt("statistic_type");
            switch (statistic) {
                case 0:
                    Map<Integer, Integer> ageMap;
                    ageMap = statisticsProvider.getAgeStats();
                    for (Object o : ageMap.entrySet()) {
                        Map.Entry pair = (Map.Entry) o;
                        int age = (int) pair.getKey();
                        int number = (int) pair.getValue();
                        PieEntry entry = new PieEntry(number, age);
                        entry.setLabel(String.valueOf(age));
                        pieEntries.add(entry);
                    }
                    label = "Age";
                    this.chart.getDescription().setText(getResources().getString(R.string.statistics_age_title));
                    break;
                case 1:
                    Map<String, Integer> zodiacMap;
                    zodiacMap = statisticsProvider.getSignStats();
                    for (Object o : zodiacMap.entrySet()) {
                        Map.Entry pair = (Map.Entry) o;
                        String zodiac = (String) pair.getKey();
                        int number = (int) pair.getValue();
                        PieEntry entry = new PieEntry(number, zodiac);
                        entry.setLabel(zodiac);
                        pieEntries.add(entry);
                    }
                    label = "Zodiac";
                    this.chart.getDescription().setText(getResources().getString(R.string.statistics_sign_title));
                    break;
                case 2:
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
                    label = "Month";
                    this.chart.getDescription().setText(getResources().getString(R.string.statistics_month_title));
                    break;
                case 3:
                    Map<Integer, Integer> weekMap;
                    weekMap = statisticsProvider.getWeekStats();
                    for (Object o : weekMap.entrySet()) {
                        Map.Entry pair = (Map.Entry) o;
                        int week = (int) pair.getKey();
                        int quantity = (int) pair.getValue();
                        PieEntry entry = new PieEntry(quantity, week);
                        String weekString = new DateFormatSymbols().getWeekdays()[week];
                        entry.setLabel(weekString);
                        pieEntries.add(entry);
                    }
                    label = "Week";
                    this.chart.getDescription().setText(getResources().getString(R.string.statistics_week_title));
                    break;
                default:
                    label = "Unknown";
            }
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
        Toast.makeText(this, msg , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {
    }
}
