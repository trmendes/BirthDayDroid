package com.tmendes.birthdaydroid.views.statistics;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.navigation.NavHostController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;

import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.views.AbstractContactsFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class AbstractStatisticFragment extends AbstractContactsFragment {
    public static final int TEXT_VIEW = 0;
    public static final int DIAGRAM_VIEW = 1;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (getStatisticViewType() == TEXT_VIEW) {
            inflater.inflate(R.menu.menu_statistic_text, menu);
        } else if (getStatisticViewType() == DIAGRAM_VIEW) {
            inflater.inflate(R.menu.menu_statistic_diagram, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.switch_to_diagram || item.getItemId() == R.id.switch_to_text) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
            prefs.edit()
                    .putBoolean("settings_statistics_as_text", !prefs.getBoolean("settings_statistics_as_text", false))
                    .apply();
            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.nav_birthday_list, false).build();
            Navigation.findNavController(this.requireView()).navigate(getCorrespondingTextOrDiagramNavId(), null, navOptions);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @StatisticViewType
    protected abstract int getStatisticViewType();

    protected abstract int getCorrespondingTextOrDiagramNavId();

    @IntDef({TEXT_VIEW, DIAGRAM_VIEW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface StatisticViewType {
    }
}
