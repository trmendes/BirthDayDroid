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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tmendes.birthdaydroid.R;

import java.util.Objects;

import static android.content.Context.CLIPBOARD_SERVICE;

public class DonationFragment extends Fragment {
    private static final String BITCOIN_ADDR = "12mgStuib7nGkqzvKjMqc4LQwNFkL1Q7m7";


    /**
     * Google
     */
    private static final String GOOGLE_PUBKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg8bTVFK5zIg4FGYkHKKQ/j/iGZQlXU0qkAv2BA6epOX1ihbMz78iD4SmViJlECHN8bKMHxouRNd9pkmQKxwEBHg5/xDC/PHmSCXFx/gcY/xa4etA1CSfXjcsS9i94n+j0gGYUg69rNkp+p/09nO9sgfRTAQppTxtgKaXwpfKe1A8oqmDUfOnPzsEAG6ogQL6Svo6ynYLVKIvRPPhXkq+fp6sJ5YVT5Hr356yCXlM++G56Pk8Z+tPzNjjvGSSs/MsYtgFaqhPCsnKhb55xHkc8GJ9haq8k3PSqwMSeJHnGiDq5lzdmsjdmGkWdQq2jIhKlhMZMm5VQWn0T59+xjjIIwIDAQAB";
    private static final String[] GOOGLE_CATALOG = new String[]{"ntpsync.donation.1",
            "ntpsync.donation.2", "ntpsync.donation.3", "ntpsync.donation.5", "ntpsync.donation.8",
            "ntpsync.donation.13"};

    /**
     * PayPal
     */
    private static final String PAYPAL_USER = "android@schuermann.eu";
    private static final String PAYPAL_CURRENCY_CODE = "USD";

    /**
     * Flattr
     */
    private static final String FLATTR_PROJECT_URL = "https://github.com/dschuermann/android-donations-lib/";
    // FLATTR_URL without http:// !
    private static final String FLATTR_URL = "flattr.com/thing/712895/dschuermannandroid-donations-lib-on-GitHub";

    /**
     * Bitcoin
     */
    private static final String BITCOIN_ADDRESS = "12Y6zbBYoRxf8kBrjau3WedjtzvcACvPMk";


    public DonationFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_donation, container, false);

        TextView bitcoin = v.findViewById(R.id.tvBitcoin);

        bitcoin.setText(BITCOIN_ADDR);

        bitcoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) Objects.requireNonNull(getContext()).
                        getSystemService(CLIPBOARD_SERVICE);
                final android.content.ClipData clipData = android.content.ClipData
                        .newPlainText(BITCOIN_ADDR, BITCOIN_ADDR);
                Objects.requireNonNull(clipboard).setPrimaryClip(clipData);
                Toast.makeText(getContext(), getContext().getResources().
                        getString(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}
