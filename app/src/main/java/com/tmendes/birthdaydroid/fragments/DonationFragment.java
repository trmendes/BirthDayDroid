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

import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tmendes.birthdaydroid.R;

import static android.content.Context.CLIPBOARD_SERVICE;

public class DonationFragment extends Fragment {
    private static final String BITCOIN_ADDR = "12mgStuib7nGkqzvKjMqc4LQwNFkL1Q7m7";

    public DonationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_donation, container, false);

        TextView bitcoin = v.findViewById(R.id.tvBitcoin);

        bitcoin.setText(BITCOIN_ADDR);

        bitcoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().
                        getSystemService(CLIPBOARD_SERVICE);
                final android.content.ClipData clipData = android.content.ClipData
                        .newPlainText(BITCOIN_ADDR, BITCOIN_ADDR);
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(getContext(), getContext().getResources().
                        getString(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}
