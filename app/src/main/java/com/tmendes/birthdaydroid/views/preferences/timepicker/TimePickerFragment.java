package com.tmendes.birthdaydroid.views.preferences.timepicker;

import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.tmendes.birthdaydroid.R;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public class TimePickerFragment extends PreferenceDialogFragmentCompat {
    private static final String SAVE_STATE_VALUE = "TimePickerPreference.value";
    private TimePicker timePicker;
    private LocalTime value;

    @NonNull
    public static TimePickerFragment newInstance(@NonNull String key) {
        final TimePickerFragment fragment = new TimePickerFragment();
        final Bundle args = new Bundle(1);
        args.putString(ARG_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            value = getTimePickerPreference().getValue();
        } else {
            long millis = savedInstanceState.getLong(SAVE_STATE_VALUE);
            value = LocalTime.of(0,0).plus(millis, ChronoUnit.MILLIS);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(SAVE_STATE_VALUE, value.get(ChronoField.MILLI_OF_DAY));
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);

        timePicker = view.findViewById(R.id.number_picker);
        timePicker.setIs24HourView(true);

        if (timePicker == null) {
            throw new IllegalStateException("Dialog view must contain an NumberPicker with id" +
                    " @id/nppc_number_picker");
        }

        timePicker.setHour(value.getHour());
        timePicker.setMinute(value.getMinute());
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {

            // clearFocus() triggers validateInputTextView() internally
            timePicker.clearFocus();

            final LocalTime value = LocalTime.of(
                    timePicker.getHour(),
                    timePicker.getMinute()
            );

            if (getTimePickerPreference().callChangeListener(value)) {
                getTimePickerPreference().setValue(value);
            }
        }
    }

    private TimePickerPreference getTimePickerPreference() {
        return (TimePickerPreference) getPreference();
    }


}
