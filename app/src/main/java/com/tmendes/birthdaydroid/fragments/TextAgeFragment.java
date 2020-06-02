package com.tmendes.birthdaydroid.fragments;

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
import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.contact.ContactCache;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TextAgeFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_text_statistics, container, false);
        setHasOptionsMenu(true);

        TableLayout tableLayout = v.findViewById(R.id.tableLayout);

        TextView title = v.findViewById(R.id.tvStatisticsTitle);
        title.setText(Objects.requireNonNull(getContext()).getResources()
                .getString(R.string.menu_statistics_age));

        TableRow header = newRow(getContext().getResources().getString(R.string.array_order_age),
                getContext().getResources().getString(R.string.amount));
        tableLayout.addView(header);

        final ContactCache contactCache = ContactCache.getInstance();
        final Map<Integer, Integer> ageStat = contactCache.getContacts().stream()
                .filter(c -> !c.isIgnore())
                .collect(Collectors.toMap(Contact::getAge, c -> 1, Integer::sum));

        for (Map.Entry<Integer, Integer> pair : ageStat.entrySet()) {
            final int age = pair.getKey();
            final int amount = pair.getValue();

            TableRow row = newRow(String.valueOf(age), String.valueOf(amount));

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