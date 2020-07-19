package com.tmendes.birthdaydroid.views.contactlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

public class FilterTermViewModel extends ViewModel {
    private final MutableLiveData<CharSequence> filterTerm = new MutableLiveData<>();

    public LiveData<CharSequence> getFilterTerm() {
        return filterTerm;
    }

    public void updateFilterTerm(CharSequence searchTerm) {
        if (!Objects.equals(searchTerm, this.filterTerm.getValue())) {
            this.filterTerm.postValue(searchTerm);
        }
    }
}
