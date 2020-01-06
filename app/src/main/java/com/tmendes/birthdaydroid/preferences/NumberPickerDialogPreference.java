package com.tmendes.birthdaydroid.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

/* Thanks to
 * https://stackoverflow.com/questions/20758986/android-preferenceactivity-dialog-with-number-picker
 */

/**
 * A {@link android.preference.Preference} that displays a number picker as a dialog.
 */
public class NumberPickerDialogPreference extends DialogPreference {

    // allowed range
    private static final int MAX_VALUE = 364;
    private static final int MIN_VALUE = 1;
    // enable or disable the 'circular behavior'
    private static final boolean WRAP_SELECTOR_WHEEL = true;

    private NumberPicker picker;
    private int value = 1;

    public NumberPickerDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberPickerDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View onCreateDialogView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        picker = new NumberPicker(getContext());
        picker.setLayoutParams(layoutParams);

        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(picker);

        return dialogView;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        picker.setMinValue(MIN_VALUE);
        picker.setMaxValue(MAX_VALUE);
        picker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        picker.setValue(getValue());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            picker.clearFocus();
            int newValue = picker.getValue();
            if (callChangeListener(newValue)) {
                setValue(newValue);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, MIN_VALUE);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(MIN_VALUE) : (Integer) defaultValue);
    }

    private void setValue(int value) {
        this.value = value;
        persistInt(this.value);
    }

    private int getValue() {
        return this.value;
    }
}