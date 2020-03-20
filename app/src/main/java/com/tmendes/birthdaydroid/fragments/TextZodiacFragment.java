package com.tmendes.birthdaydroid.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.providers.BirthdayDataProvider;

import java.util.Map;
import java.util.Objects;

public class TextZodiacFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_text_statistics, container, false);
        setHasOptionsMenu(true);

        TableLayout tableLayout = v.findViewById(R.id.tableLayout);

        BirthdayDataProvider bddDataProviver = BirthdayDataProvider.getInstance();

        Map<String, Integer> ageStat = bddDataProviver.getStatistics().getSignStats();

        TextView title = v.findViewById(R.id.tvStatisticsTitle);
        title.setText(Objects.requireNonNull(getContext()).getResources()
                .getString(R.string.menu_statistics_zodiac));

        TableRow header = newRow("", getContext().getResources().getString(R.string.amount));
        tableLayout.addView(header);

        for (Object o : ageStat.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            String zodiac = (String) pair.getKey();
            int amount = (int) pair.getValue();

            TableRow row = newRow(zodiac, String.valueOf(amount));

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