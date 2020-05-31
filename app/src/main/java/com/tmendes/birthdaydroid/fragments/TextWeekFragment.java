package com.tmendes.birthdaydroid.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.providers.BirthdayDataProvider;

import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class TextWeekFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_text_statistics, container, false);
        setHasOptionsMenu(true);

        TableLayout tableLayout = v.findViewById(R.id.tableLayout);

        BirthdayDataProvider bddDataProviver = BirthdayDataProvider.getInstance();

        TextView title = v.findViewById(R.id.tvStatisticsTitle);
        title.setText(Objects.requireNonNull(getContext()).getResources()
                .getString(R.string.menu_statistics_week));

        TableRow header = newRow("", getContext().getResources().getString(R.string.amount));
        tableLayout.addView(header);

        Map<DayOfWeek, Integer> ageStat = bddDataProviver.getStatistics().getWeekStats();

        for (Map.Entry<DayOfWeek, Integer> pair : ageStat.entrySet()) {
            DayOfWeek dayOfWeek = pair.getKey();
            int amount = pair.getValue();

            final Locale locale;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = getResources().getConfiguration().getLocales().get(0);
            } else {
                locale = getResources().getConfiguration().locale;
            }
            final String weekName = dayOfWeek.getDisplayName(TextStyle.FULL, locale);

            TableRow row = newRow(weekName, String.valueOf(amount));

            tableLayout.addView(row);
        }

        return v;
    }

    private TableRow newRow(String left, String right) {
        TableRow row = new TableRow(getContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        lp.setMargins(10, 10, 10, 10);
        row.setLayoutParams(lp);

        TextView leftTv = new TextView(getContext());
        TextView rightTv = new TextView(getContext());

        leftTv.setText(left);
        rightTv.setText(right);

        row.addView(leftTv);
        row.addView(rightTv);

        return row;
    }

}