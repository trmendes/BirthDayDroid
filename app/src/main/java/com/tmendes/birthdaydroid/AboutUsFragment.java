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

package com.tmendes.birthdaydroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutUsFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_about_us,
                container, false);

        TextView appVersion = (TextView) v.findViewById(R.id.tvVersion);
        TextView tvHelpDevel = (TextView) v.findViewById(R.id.tvHelpDevel);
        TextView tvHelpIssue = (TextView) v.findViewById(R.id.tvHelpIssue);
        TextView tvHelpTranslator = (TextView) v.findViewById(R.id.tvHelpTranslator);
        TextView tvIcons01 = (TextView) v.findViewById(R.id.tvIcons01);
        TextView tvTranslatorNames = (TextView) v.findViewById(R.id.tvTranslatorNames);

        tvHelpDevel.setMovementMethod(LinkMovementMethod.getInstance());
        tvHelpIssue.setMovementMethod(LinkMovementMethod.getInstance());
        tvHelpTranslator.setMovementMethod(LinkMovementMethod.getInstance());
        tvIcons01.setMovementMethod(LinkMovementMethod.getInstance());
        tvTranslatorNames.setMovementMethod(LinkMovementMethod.getInstance());

        appVersion.setText(container.getContext().getResources().getString(R.string.build, BuildConfig.VERSION_CODE));

        return v;

    }

}
