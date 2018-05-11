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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tmendes.birthdaydroid.BirthDay;
import com.tmendes.birthdaydroid.MainActivity;
import com.tmendes.birthdaydroid.R;

import java.text.DateFormatSymbols;
import java.util.Map;
import java.util.Objects;

public class StatisticsFragment extends Fragment {

    private Context ctx;
    private BirthDay birthDay;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_statistics,
                container, false);

        ctx = container.getContext();
        birthDay = ((MainActivity) Objects.requireNonNull(getActivity())).getBirthday();

        Button buttonAges = v.findViewById(R.id.buttonAges);
        Button buttonSign = v.findViewById(R.id.buttonSign);
        Button buttonMonth = v.findViewById(R.id.buttonMonth);
        Button buttonWeek = v.findViewById(R.id.buttonWeek);

        buttonAges.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Map<Integer, Integer> ageStat = birthDay.getAgeStats();
                StringBuilder dialogData = new StringBuilder(ctx.getResources()
                        .getString(R.string.statistics_contacts_counter,
                                birthDay.getBirthDayList().size()));

                for (Object o : ageStat.entrySet()) {
                    Map.Entry pair = (Map.Entry) o;
                    int age = (int) pair.getKey();
                    int number = (int) pair.getValue();
                    dialogData.append(ctx.getResources()
                            .getString(R.string.statistics_int_int, number, age));
                }

                AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
                alertDialog.setTitle(ctx.getResources().getString(R.string.statistics_age_title));
                alertDialog.setMessage(dialogData.toString());
                alertDialog.show();
            }

        });

        buttonSign.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Map<String, Integer> signStat = birthDay.getSignStats();
                StringBuilder dialogData = new StringBuilder(ctx.getResources()
                        .getString(R.string.statistics_contacts_counter,
                                birthDay.getBirthDayList().size()));

                for (Object o : signStat.entrySet()) {
                    Map.Entry pair = (Map.Entry) o;
                    String sign = (String) pair.getKey();
                    int number = (int) pair.getValue();
                    dialogData.append(ctx.getResources()
                            .getString(R.string.statistics_int_string, number, sign));
                }

                AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
                alertDialog.setTitle(ctx.getResources().getString(R.string.statistics_sign_title));
                alertDialog.setMessage(dialogData.toString());
                alertDialog.show();
            }

        });

        buttonMonth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Map<Integer, Integer> monthStat = birthDay.getMonthStats();
                DateFormatSymbols dfs = new DateFormatSymbols();
                StringBuilder dialogData = new StringBuilder(ctx.getResources()
                        .getString(R.string.statistics_contacts_counter,
                                birthDay.getBirthDayList().size()));

                for (Object o : monthStat.entrySet()) {
                    Map.Entry pair = (Map.Entry) o;
                    int month = (int) pair.getKey();
                    int number = (int) pair.getValue();
                    dialogData.append(ctx.getResources()
                            .getString(R.string.statistics_int_string,
                                    number, dfs.getMonths()[month]));
                }

                AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
                alertDialog.setTitle(ctx.getResources().getString(R.string.statistics_month_title));
                alertDialog.setMessage(dialogData.toString());
                alertDialog.show();
            }

        });

        buttonWeek.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Map<Integer, Integer> weekStat = birthDay.getWeekStats();
                DateFormatSymbols dfs = new DateFormatSymbols();
                StringBuilder dialogData = new StringBuilder(ctx.getResources()
                        .getString(R.string.statistics_contacts_counter,
                                birthDay.getBirthDayList().size()));

                for (Object o : weekStat.entrySet()) {
                    Map.Entry pair = (Map.Entry) o;
                    int week = (int) pair.getKey();
                    int number = (int) pair.getValue();
                    dialogData.append(ctx.getResources()
                            .getString(R.string.statistics_int_string,
                                    number, dfs.getWeekdays()[week]));
                }

                AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
                alertDialog.setTitle(ctx.getResources().getString(R.string.statistics_week_title));
                alertDialog.setMessage(dialogData.toString());
                alertDialog.show();
            }

        });

        return v;

    }
}
