package com.tmendes.birthdaydroid.views.statistics.zodiac;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.views.statistics.AbstractStatisticFragment;

public abstract class AbstractZodiacFragment extends AbstractStatisticFragment {
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_statistic_zodiac_info, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.zodiac_info) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.zodiac_info_title)
                    .setMessage(R.string.zodiac_info_message)
                    .show();
           return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
