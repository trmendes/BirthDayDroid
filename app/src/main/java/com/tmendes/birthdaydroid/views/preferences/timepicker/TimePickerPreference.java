package com.tmendes.birthdaydroid.views.preferences.timepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.preference.DialogPreference;

import com.tmendes.birthdaydroid.R;

import java.time.LocalTime;

public class TimePickerPreference extends DialogPreference {
    private static final LocalTime DEFAULT_VALUE = LocalTime.of(9,0);

    private final TimeConverter timeConverter = new TimeConverter();

    private LocalTime localTime = DEFAULT_VALUE;

    public TimePickerPreference(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setDialogLayoutResource(R.layout.preference_dialog_timepicker);
    }

    public void setValue(LocalTime localTime) {
        final boolean wasBlocking = shouldDisableDependents();

        this.localTime = localTime;
        persistLong(timeConverter.localTieToMillis(localTime));

        final boolean isBlocking = shouldDisableDependents();
        if (isBlocking != wasBlocking) {
            notifyDependencyChange(isBlocking);
        }
    }

    public LocalTime getValue() {
        return localTime;
    }

    @NonNull
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, (int) timeConverter.localTieToMillis(DEFAULT_VALUE));
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        long millis = getPersistedLong((defaultValue != null) ? (Integer) defaultValue : timeConverter.localTieToMillis(DEFAULT_VALUE));
        LocalTime value = timeConverter.millisToLocalTime(millis);
        setValue(value);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }

        final SavedState state = new SavedState(superState);
        state.value = getValue();
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }

        final SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setValue(myState.value);
    }

    private static class SavedState extends BaseSavedState {
        private final TimeConverter timeConverter = new TimeConverter();
        private LocalTime value;

        public SavedState(Parcel source) {
            super(source);

            long millis = source.readLong();
            value = timeConverter.millisToLocalTime(millis);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeLong(timeConverter.localTieToMillis(value));
        }

        public static final Creator<SavedState> CREATOR = new Creator<TimePickerPreference.SavedState>() {
            @Override
            public TimePickerPreference.SavedState createFromParcel(Parcel in) {
                return new TimePickerPreference.SavedState(in);
            }

            @Override
            public TimePickerPreference.SavedState[] newArray(int size) {
                return new TimePickerPreference.SavedState[size];
            }
        };
    }
}
