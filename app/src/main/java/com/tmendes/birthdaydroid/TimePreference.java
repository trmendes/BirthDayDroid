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

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class TimePreference extends DialogPreference {
    private final Calendar calendar;
    private TimePicker picker = null;

    public TimePreference(Context ctxt) {
        this(ctxt, null);
    }

    public TimePreference(Context ctxt, AttributeSet attrs) {
        this(ctxt, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public TimePreference(Context ctxt, AttributeSet attrs, int defStyle) {
        super(ctxt, attrs, defStyle);

        setPositiveButtonText(ctxt.getResources().getString(R.string.settings_time_set));
        setNegativeButtonText(ctxt.getResources().getString(R.string.settings_time_cancel));

        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, MainActivity.DEFAULT_ALARM_TIME);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            picker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            picker.setMinute(calendar.get(Calendar.MINUTE));
        } else {
            //noinspection deprecation
            picker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            //noinspection deprecation
            picker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
                calendar.set(Calendar.MINUTE, picker.getMinute());
            } else {
                //noinspection deprecation
                calendar.set(Calendar.HOUR_OF_DAY, picker.getCurrentHour());
                //noinspection deprecation
                calendar.set(Calendar.MINUTE, picker.getCurrentMinute());
            }

            setSummary(getSummary());

            if (callChangeListener(calendar.getTimeInMillis())) {
                persistLong(calendar.getTimeInMillis());
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
            calendar.setTimeInMillis(persistedValue);

        } else if (defaultValue != null) {
            calendar.setTimeInMillis(Long.parseLong((String) defaultValue));
        } else {
            calendar.setTimeInMillis(System.currentTimeMillis());
        }

        setSummary(getSummary());
    }

    @Override
    public CharSequence getSummary() {
        if (calendar == null) {
            return null;
        }
        return DateFormat.getTimeFormat(getContext()).format(new Date(calendar.getTimeInMillis()));
    }
}