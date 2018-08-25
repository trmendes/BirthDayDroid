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

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

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

        TextView appVersion = v.findViewById(R.id.tvVersion);
        Button btChangelog = v.findViewById(R.id.about_us_bt_changelog);
        WebView webViewCredits = v.findViewById(R.id.webViewCredits);

        btChangelog.setOnClickListener(this);

        webViewCredits.loadUrl("file:///android_asset/credits.html");

        appVersion.setText(container.getContext().getResources()
                .getString(R.string.build, BuildConfig.VERSION_CODE));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        boolean useDarkTheme = prefs.getBoolean("dark_theme", false);
        if (useDarkTheme) {
            webViewCredits.setBackgroundColor(0);
        }

        return v;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.about_us_bt_changelog:
                try {
                    InputStream changelogIS = view.getResources().getAssets().open("changelog.txt");
                    String s = readTextFile(changelogIS);

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage(s);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
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
