package com.tmendes.birthdaydroid.views.statistics.month;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.views.statistics.AbstractStatisticFragment;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TextMonthFragment extends AbstractStatisticFragment {

    private TableLayout tableLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_text_statistics, container, false);
        setHasOptionsMenu(true);

        tableLayout = v.findViewById(R.id.tableLayout);

        final TextView title = v.findViewById(R.id.tvStatisticsTitle);
        title.setText(requireContext().getResources().getString(R.string.menu_statistics_month));

        return v;
    }

    @Override
    protected void updateContacts(List<Contact> contacts) {
        final Map<Month, Integer> monthMap = contacts.stream()
                .filter(c -> !c.isIgnore())
                .collect(Collectors.toMap(c -> c.getBornOn().getMonth(), c -> 1, Integer::sum));

        TreeMap<Month, Integer> sorted = new TreeMap<>();
        sorted.putAll(monthMap);

        final TableRow header = newRow("", requireContext().getResources().getString(R.string.amount));
        tableLayout.removeAllViews();
        tableLayout.addView(header);
        for (Map.Entry<Month, Integer> pair : sorted.entrySet()) {
            final Month month = pair.getKey();
            final int amount = pair.getValue();

            final Locale locale;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = getResources().getConfiguration().getLocales().get(0);
            } else {
                locale = getResources().getConfiguration().locale;
            }
            final String monthName = month.getDisplayName(TextStyle.FULL, locale);
            final TableRow row = newRow(monthName, String.valueOf(amount));

            tableLayout.addView(row);
        }
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

    @Override
    protected int getStatisticViewType() {
        return TEXT_VIEW;
    }

    @Override
    protected int getCorrespondingTextOrDiagramNavId() {
        return R.id.nav_statistics_month_diagram;
    }
}