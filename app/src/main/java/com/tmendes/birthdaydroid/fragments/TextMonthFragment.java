package com.tmendes.birthdaydroid.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.providers.BirthdayDataProvider;

import java.text.DateFormatSymbols;
import java.util.Map;

public class TextMonthFragment extends Fragment {

    private BirthdayDataProvider bddDataProviver;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_text_statistics, container, false);
        TableLayout tableLayout = v.findViewById(R.id.tableLayout);

        bddDataProviver = BirthdayDataProvider.getInstance();

        TextView title = v.findViewById(R.id.tvStatisticsTitle);
        title.setText(getContext().getResources().getString(R.string.menu_statistics_month));

        Map<Integer, Integer> ageStat = bddDataProviver.getStatistics().getMonthStats();

        TableRow header = newRow("", getContext().getResources().getString(R.string.amount));
        tableLayout.addView(header);

        for (Object o : ageStat.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            int month = (int) pair.getKey();
            int amount = (int) pair.getValue();

            DateFormatSymbols dfs = new DateFormatSymbols();
            String monthName = dfs.getMonths()[month];

            TableRow row = newRow(monthName, String.valueOf(amount));

            tableLayout.addView(row);
        }

        return v;
    }

    public TableRow newRow(String left, String right) {
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