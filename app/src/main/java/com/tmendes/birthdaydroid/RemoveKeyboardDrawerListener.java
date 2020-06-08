package com.tmendes.birthdaydroid;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.Objects;

public class RemoveKeyboardDrawerListener extends DrawerLayout.SimpleDrawerListener {
    private final Context context;

    public RemoveKeyboardDrawerListener(Context context) {
        this.context = context;
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        InputMethodManager inputManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputManager).hideSoftInputFromWindow(drawerView.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
