package com.tmendes.birthdaydroid.permission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.tmendes.birthdaydroid.R;

public class PermissionHelper {
    private final Context ctx;

    public PermissionHelper(Context ctx) {
        this.ctx = ctx;
    }

    public boolean checkReadContactsPermission() {
        boolean allowed = ctx.checkCallingOrSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;

        if (!allowed) {
            Toast.makeText(ctx, ctx.getResources().getString(R.string.alert_contats_dialog_title),
                    Toast.LENGTH_SHORT).show();
        }

        return allowed;
    }
}
