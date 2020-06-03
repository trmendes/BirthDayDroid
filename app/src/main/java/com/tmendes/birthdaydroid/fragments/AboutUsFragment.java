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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.tmendes.birthdaydroid.BuildConfig;
import com.tmendes.birthdaydroid.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AboutUsFragment extends Fragment  implements View.OnClickListener {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_about_us,
                container, false);
        setHasOptionsMenu(true);

        TextView appVersion = v.findViewById(R.id.tvVersion);
        Button btChangelog = v.findViewById(R.id.about_us_bt_changelog);

        btChangelog.setOnClickListener(this);

        appVersion.setText(container.getContext().getResources()
                .getString(R.string.build, BuildConfig.VERSION_CODE));

        return v;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.about_us_bt_changelog) {
            try {
                InputStream changelogIS = view.getResources().getAssets().open("changelog.txt");
                String s = readTextFile(changelogIS);

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage(s);
                builder.setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());

                AlertDialog alert = builder.create();
                alert.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toString();
    }
}
