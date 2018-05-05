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
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tmendes.birthdaydroid.BirthDay;
import com.tmendes.birthdaydroid.R;

import java.text.DateFormatSymbols;
import java.util.Iterator;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private Context ctx;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_statistics,
                container, false);

        ctx = container.getContext();
        BirthDay data = BirthDay.getBirthDayList(this.ctx);
        DateFormatSymbols dfs = new DateFormatSymbols();

        int contactsCounter = data.getList().size();

        Map<Integer, Integer> ageStat = data.getAgeStats();
        Map<String, Integer> signStat = data.getSignStats();
        Map<Integer, Integer> monthStat = data.getMonthStats();
        Map<Integer, Integer> weekStat = data.getWeekStats();

        Button buttonAges = (Button) v.findViewById(R.id.buttonAges);
        Button buttonSign = (Button) v.findViewById(R.id.buttonSign);
        Button buttonMonth = (Button) v.findViewById(R.id.buttonMonth);
        Button buttonWeek = (Button) v.findViewById(R.id.buttonWeek);

        Iterator it;

        it = ageStat.entrySet().iterator();

        String dialogData = ctx.getResources().getString(R.string.statistics_contacts_counter, contactsCounter);

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            int age = (int) pair.getKey();
            int number = (int) pair.getValue();
            dialogData += ctx.getResources().getString(R.string.statistics_int_int, number, age);
        }

        final String finalDialogDataAges = dialogData;
        buttonAges.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
                alertDialog.setTitle(ctx.getResources().getString(R.string.statistics_age_title));
                alertDialog.setMessage(finalDialogDataAges);
                alertDialog.show();
            }

        });

        it = signStat.entrySet().iterator();
        dialogData = ctx.getResources().getString(R.string.statistics_contacts_counter, contactsCounter);
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String sign = (String) pair.getKey();
            int number = (int) pair.getValue();
            dialogData += ctx.getResources().getString(R.string.statistics_int_string, number, sign);
        }

        final String finalDialogDataSign = dialogData;
        buttonSign.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
                alertDialog.setTitle(ctx.getResources().getString(R.string.statistics_sign_title));
                alertDialog.setMessage(finalDialogDataSign);
                alertDialog.show();
            }

        });

        it = monthStat.entrySet().iterator();
        dialogData = ctx.getResources().getString(R.string.statistics_contacts_counter, contactsCounter);
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            int month = (int) pair.getKey();
            int number = (int) pair.getValue();
            dialogData += ctx.getResources().getString(R.string.statistics_int_string, number, dfs.getMonths()[month]);
        }

        final String finalDialogDataMonth = dialogData;
        buttonMonth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
                alertDialog.setTitle(ctx.getResources().getString(R.string.statistics_month_title));
                alertDialog.setMessage(finalDialogDataMonth);
                alertDialog.show();
            }

        });

        it = weekStat.entrySet().iterator();
        dialogData = ctx.getResources().getString(R.string.statistics_contacts_counter, contactsCounter);
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            int week = (int) pair.getKey();
            int number = (int) pair.getValue();
            dialogData += ctx.getResources().getString(R.string.statistics_int_string, number, dfs.getWeekdays()[week]);
        }

        final String finalDialogDataWeek = dialogData;
        buttonWeek.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
                alertDialog.setTitle(ctx.getResources().getString(R.string.statistics_week_title));
                alertDialog.setMessage(finalDialogDataWeek);
                alertDialog.show();
            }

        });

        return v;

    }
}
