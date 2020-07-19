package com.tmendes.birthdaydroid.permission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

public class PermissionChecker {
    private final Context context;

    public PermissionChecker(Context context) {
        this.context = context;
    }

    public boolean checkReadContactsPermission() {
        return context.checkCallingOrSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }
}
