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

package com.tmendes.birthdaydroid.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import com.tmendes.birthdaydroid.MainActivity;
import com.tmendes.birthdaydroid.R;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public class TimeDialogPreference extends DialogPreference {
    private LocalTime localTime;
    private TimePicker picker = null;

    public TimeDialogPreference(Context ctxt) {
        this(ctxt, null);
    }

    public TimeDialogPreference(Context ctxt, AttributeSet attrs) {
        this(ctxt, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public TimeDialogPreference(Context ctxt, AttributeSet attrs, int defStyle) {
        super(ctxt, attrs, defStyle);

        setPositiveButtonText(ctxt.getResources().getString(R.string.settings_time_set));
        setNegativeButtonText(ctxt.getResources().getString(R.string.settings_time_cancel));

        localTime = LocalTime.of(MainActivity.DEFAULT_ALARM_TIME, 0);
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        picker.setIs24HourView(true);
        return (picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setHour(localTime.getHour());
        picker.setMinute(localTime.getMinute());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            localTime = localTime
                    .withHour(picker.getHour())
                    .withMinute(picker.getMinute());

            setSummary(getSummary());

            int millis = localTime.get(ChronoField.MILLI_OF_DAY);

            if (callChangeListener(millis)) {
                persistLong(millis);
                notifyChanged();
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        if (restoreValue) {
            long persistedValue;
            try {
                persistedValue = getPersistedLong(System.currentTimeMillis());
            } catch (Exception e) {
                persistedValue = System.currentTimeMillis();
            }

            localTime = LocalTime.of(0,0).plus(persistedValue, ChronoUnit.MILLIS);
        } else if (defaultValue != null) {
            localTime = LocalTime.of(0,0).plus(Long.parseLong((String) defaultValue), ChronoUnit.MILLIS);
        } else {
            localTime = LocalTime.of(0,0).plus(System.currentTimeMillis(), ChronoUnit.MILLIS);
        }

        setSummary(getSummary());
    }

    @Override
    public CharSequence getSummary() {
        if (localTime == null) {
            return null;
        }

        return localTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
    }
}