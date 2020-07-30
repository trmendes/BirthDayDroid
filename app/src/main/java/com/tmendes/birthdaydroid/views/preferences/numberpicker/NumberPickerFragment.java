package com.tmendes.birthdaydroid.views.preferences.numberpicker;

import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.tmendes.birthdaydroid.R;

public class NumberPickerFragment extends PreferenceDialogFragmentCompat {
    private static final String SAVE_STATE_VALUE = "NumberPickerPreference.value";

    private NumberPicker numberPicker;
    private int value;

    @NonNull
    public static NumberPickerFragment newInstance(@NonNull String key) {
        final NumberPickerFragment fragment = new NumberPickerFragment();
        final Bundle args = new Bundle(1);
        args.putString(ARG_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            value = getNumberPickerPreference().getValue();
        } else {
            value = savedInstanceState.getInt(SAVE_STATE_VALUE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_STATE_VALUE, value);
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);

        numberPicker = view.findViewById(R.id.number_picker);

        if (numberPicker == null) {
            throw new IllegalStateException("Dialog view must contain an NumberPicker with id" +
                    " @id/nppc_number_picker");
        }

        numberPicker.setMinValue(getNumberPickerPreference().getMinValue());
        numberPicker.setMaxValue(getNumberPickerPreference().getMaxValue());
        numberPicker.setValue(value);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            // clearFocus() triggers validateInputTextView() internally
            numberPicker.clearFocus();

            final int value = numberPicker.getValue();
            if (getNumberPickerPreference().callChangeListener(value)) {
                getNumberPickerPreference().setValue(value);
            }
        }
    }

    private NumberPickerPreference getNumberPickerPreference() {
        return (NumberPickerPreference) getPreference();
    }
}
