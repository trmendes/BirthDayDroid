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
import java.util.GregorianCalendar;

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
        calendar = new GregorianCalendar();
        calendar.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
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
                //Stale persisted data may be the wrong type
                persistedValue = System.currentTimeMillis();
            }
            calendar.setTimeInMillis(persistedValue);
        } else if (defaultValue != null) {
            calendar.setTimeInMillis(Long.parseLong((String) defaultValue));
        } else {
            //!restoreValue, defaultValue == null
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