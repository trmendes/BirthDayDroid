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
import com.tmendes.birthdaydroid.zodiac.Zodiac;
import com.tmendes.birthdaydroid.zodiac.ZodiacResourceHelper;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TextZodiacFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_text_statistics, container, false);
        setHasOptionsMenu(true);

        final TableLayout tableLayout = v.findViewById(R.id.tableLayout);

        final TextView title = v.findViewById(R.id.tvStatisticsTitle);
        title.setText(Objects.requireNonNull(getContext()).getResources()
                .getString(R.string.menu_statistics_zodiac));

        final TableRow header = newRow("", getContext().getResources().getString(R.string.amount));
        tableLayout.addView(header);

        final ContactCache contactCache = ContactCache.getInstance();
        final Map<Integer, Integer> zodiacMap = contactCache.getContacts().stream()
                .filter(c -> !c.isIgnore())
                .collect(Collectors.toMap(Contact::getZodiac, c -> 1, Integer::sum));

        final ZodiacResourceHelper zodiacResourceHelper = new ZodiacResourceHelper(getContext().getResources());
        for (Map.Entry<Integer, Integer> pair: zodiacMap.entrySet()) {
            @Zodiac final int zodiac = pair.getKey();
            final int amount = pair.getValue();

            final TableRow row = newRow(zodiacResourceHelper.getZodiacName(zodiac), String.valueOf(amount));

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