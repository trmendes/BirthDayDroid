package com.tmendes.birthdaydroid.views.statistics.age;

import android.content.res.Resources;
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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TextAgeFragment extends AbstractStatisticFragment {

    private TableLayout tableLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_text_statistics, container, false);
        setHasOptionsMenu(true);

        tableLayout = v.findViewById(R.id.tableLayout);

        TextView title = v.findViewById(R.id.tvStatisticsTitle);
        title.setText(requireContext().getResources().getString(R.string.menu_statistics_age));

        return v;
    }

    @Override
    protected void updateContacts(List<Contact> contacts) {
        final Map<Integer, Integer> ageStat = contacts.stream()
                .filter(c -> !c.isIgnore())
                .filter(c -> !c.isMissingYearInfo()) // Remove unknown year from statistic
                .collect(Collectors.toMap(Contact::getAgeInYears, c -> 1, Integer::sum));

        TreeMap<Integer, Integer> sorted = new TreeMap<>();
        sorted.putAll(ageStat);

        final Resources resources = requireContext().getResources();
        final TableRow header = newRow(resources.getString(R.string.array_order_age),
                resources.getString(R.string.amount));

        tableLayout.removeAllViews();
        tableLayout.addView(header);
        for (Map.Entry<Integer, Integer> pair : sorted.entrySet()) {
            final int age = pair.getKey();
            final int amount = pair.getValue();
            TableRow row = newRow(String.valueOf(age), String.valueOf(amount));
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
        return R.id.nav_statistics_age_diagram;
    }
}