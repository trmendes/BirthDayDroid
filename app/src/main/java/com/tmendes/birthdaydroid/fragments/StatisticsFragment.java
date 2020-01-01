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
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.activities.BarChartActivity;
import com.tmendes.birthdaydroid.activities.PieChartActivity;
import com.tmendes.birthdaydroid.providers.BirthdayDataProvider;

import java.text.DateFormatSymbols;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private boolean showStatisticsAsText;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_statistics,
                container, false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(container.getContext());
        boolean hideZoadiac = prefs.getBoolean("hide_zodiac", false);
        showStatisticsAsText = prefs.getBoolean("settings_statistics_as_text", false);

        Button buttonAges = v.findViewById(R.id.buttonAges);
        Button buttonZodiac = v.findViewById(R.id.buttonSign);
        Button buttonMonth = v.findViewById(R.id.buttonMonth);
        Button buttonWeek = v.findViewById(R.id.buttonWeek);

        if (hideZoadiac) {
            buttonZodiac.setVisibility(View.INVISIBLE);
        }

        buttonAges.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (showStatisticsAsText) {
                    int size = BirthdayDataProvider.getInstance().getAllContacts().size();
                    Map<Integer, Integer> ageStat = BirthdayDataProvider.getInstance().
                            getStatistics().getAgeStats();

                    StringBuilder dialogData = new StringBuilder(getContext().getResources()
                            .getQuantityString(
                                    R.plurals.statistics_contacts_counter,
                                    size,
                                    size));

                    for (Object o : ageStat.entrySet()) {
                        Map.Entry pair = (Map.Entry) o;
                        int age = (int) pair.getKey();
                        int number = (int) pair.getValue();
                        dialogData.append(getContext().getResources()
                                .getQuantityString(R.plurals.statistics_int_int, number, number, age));
                    }

                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle(getContext().getResources()
                            .getString(R.string.statistics_age_title));
                    alertDialog.setMessage(dialogData.toString());
                    alertDialog.show();
                } else {
                    Intent barChart = new Intent(getActivity(), BarChartActivity.class);
                    barChart.putExtra("statistic_type", 0);
                    startActivity(barChart);
                }
            }
        });

        buttonZodiac.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (showStatisticsAsText) {
                    int size = BirthdayDataProvider.getInstance().getAllContacts().size();
                    Map<String, Integer> signStat = BirthdayDataProvider.getInstance()
                            .getStatistics().getSignStats();
                    StringBuilder dialogData = new StringBuilder(getContext().getResources()
                            .getQuantityString(
                                    R.plurals.statistics_contacts_counter,
                                    size,
                                    size));

                    for (Object o : signStat.entrySet()) {
                        Map.Entry pair = (Map.Entry) o;
                        String sign = (String) pair.getKey();
                        int number = (int) pair.getValue();
                        dialogData.append(getContext().getResources()
                                .getQuantityString(
                                        R.plurals.statistics_int_string, number, number, sign));
                    }

                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle(getContext().getResources()
                            .getString(R.string.statistics_sign_title));
                    alertDialog.setMessage(dialogData.toString());
                    alertDialog.show();
                } else {
                    Intent pieChart = new Intent(getActivity(), PieChartActivity.class);
                    pieChart.putExtra("statistic_type", 1);
                    startActivity(pieChart);
                }
            }
        });

        buttonMonth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (showStatisticsAsText) {
                    int size = BirthdayDataProvider.getInstance().getAllContacts().size();
                    Map<Integer, Integer> monthStat = BirthdayDataProvider.getInstance()
                            .getStatistics().getMonthStats();
                    DateFormatSymbols dfs = new DateFormatSymbols();
                    StringBuilder dialogData = new StringBuilder(getContext().getResources()
                            .getQuantityString(
                                    R.plurals.statistics_contacts_counter,
                                    size,
                                    size));

                    for (Object o : monthStat.entrySet()) {
                        Map.Entry pair = (Map.Entry) o;
                        int month = (int) pair.getKey();
                        int number = (int) pair.getValue();
                        dialogData.append(getContext().getResources()
                                .getQuantityString(R.plurals.statistics_int_string,
                                        number, number, dfs.getMonths()[month]));
                    }

                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle(getContext().getResources()
                            .getString(R.string.statistics_month_title));
                    alertDialog.setMessage(dialogData.toString());
                    alertDialog.show();
                } else {
                    Intent pieChart = new Intent(getActivity(), PieChartActivity.class);
                    pieChart.putExtra("statistic_type", 2);
                    startActivity(pieChart);
                }
            }
        });

        buttonWeek.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (showStatisticsAsText) {
                    int size = BirthdayDataProvider.getInstance().getAllContacts().size();
                    Map<Integer, Integer> weekStat = BirthdayDataProvider.getInstance()
                            .getStatistics().getWeekStats();
                    DateFormatSymbols dfs = new DateFormatSymbols();
                    StringBuilder dialogData = new StringBuilder(getContext().getResources()
                            .getQuantityString(
                                    R.plurals.statistics_contacts_counter,
                                    size,
                                    size));

                    for (Object o : weekStat.entrySet()) {
                        Map.Entry pair = (Map.Entry) o;
                        int weekDay = (int) pair.getKey();
                        int number = (int) pair.getValue();
                        dialogData.append(getContext().getResources()
                                .getQuantityString(R.plurals.statistics_int_string,
                                        number, number, dfs.getWeekdays()[weekDay]));
                    }

                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle(getContext().getResources()
                            .getString(R.string.statistics_week_title));
                    alertDialog.setMessage(dialogData.toString());
                    alertDialog.show();
                } else {
                    Intent pieChart = new Intent(getActivity(), PieChartActivity.class);
                    pieChart.putExtra("statistic_type", 3);
                    startActivity(pieChart);
                }
            }
        });

        return v;

    }
}

