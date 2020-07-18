package com.tmendes.birthdaydroid.views.statistics.zodiac;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.views.statistics.AbstractStatisticFragment;
import com.tmendes.birthdaydroid.zodiac.Zodiac;
import com.tmendes.birthdaydroid.zodiac.ZodiacResourceHelper;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TextZodiacFragment extends AbstractStatisticFragment {

    private TableLayout tableLayout;
    private ZodiacResourceHelper zodiacResourceHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zodiacResourceHelper = new ZodiacResourceHelper(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_text_statistics, container, false);
        setHasOptionsMenu(true);

        tableLayout = v.findViewById(R.id.tableLayout);

        final TextView title = v.findViewById(R.id.tvStatisticsTitle);
        title.setText(requireContext().getResources().getString(R.string.menu_statistics_zodiac));

        return v;
    }

    @Override
    protected void updateContacts(List<Contact> contacts) {
        final Map<Integer, Integer> zodiacMap = contacts.stream()
                .filter(c -> !c.isIgnore())
                .collect(Collectors.toMap(Contact::getZodiac, c -> 1, Integer::sum));

        TreeMap<Integer, Integer> sorted = new TreeMap<>();
        sorted.putAll(zodiacMap);

        final TableRow header = newRow("", requireContext().getResources().getString(R.string.amount));
        tableLayout.removeAllViews();
        tableLayout.addView(header);
        for (Map.Entry<Integer, Integer> pair: sorted.entrySet()) {
            @Zodiac final int zodiac = pair.getKey();
            final int amount = pair.getValue();

            final TableRow row = newRow(zodiacResourceHelper.getZodiacName(zodiac), String.valueOf(amount));

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
        return R.id.nav_statistics_zodiac_diagram;
    }
}