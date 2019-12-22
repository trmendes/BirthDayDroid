/*
 * Copyright (C) 2015-2016 The Food Restriction Project Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tmendes.birthdaydroid.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tmendes.birthdaydroid.activities.BarChartActivity;
import com.tmendes.birthdaydroid.activities.PieChartActivity;
import com.tmendes.birthdaydroid.R;

public class StatisticsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_statistics,
                container, false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(container.getContext());
        boolean hideZoadiac = prefs.getBoolean("hide_zodiac", false);

        Button buttonAges = v.findViewById(R.id.buttonAges);
        Button buttonZodiac = v.findViewById(R.id.buttonSign);
        Button buttonMonth = v.findViewById(R.id.buttonMonth);
        Button buttonWeek = v.findViewById(R.id.buttonWeek);

        if (hideZoadiac) {
            buttonZodiac.setVisibility(View.INVISIBLE);
        }

        buttonAges.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent barChart = new Intent(getActivity(), BarChartActivity.class);
                barChart.putExtra("statistic_type", 0);
                startActivity(barChart);
            }

        });

        buttonZodiac.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent pieChart = new Intent(getActivity(), PieChartActivity.class);
                pieChart.putExtra("statistic_type", 1);
                startActivity(pieChart);
            }
        });

        buttonMonth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent pieChart = new Intent(getActivity(), PieChartActivity.class);
                pieChart.putExtra("statistic_type", 2);
                startActivity(pieChart);
            }

        });

        buttonWeek.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent pieChart = new Intent(getActivity(), PieChartActivity.class);
                pieChart.putExtra("statistic_type", 3);
                startActivity(pieChart);
            }

        });

        return v;

    }
}
