package com.tmendes.birthdaydroid.helpers;

import android.content.Context;
import android.widget.Toast;

import com.tmendes.birthdaydroid.R;

public class PermissionHelper {

    public static final int CONTACT_PERMISSION = 0;
    private boolean contactReadStatus;
    private final Context ctx;

    public PermissionHelper(Context ctx) {
        contactReadStatus = false;
        this.ctx = ctx;
    }

    public void updatePermissionPreferences(int permission, boolean status) {
        switch (permission) {
            case CONTACT_PERMISSION: {
                contactReadStatus = status;
            }
            break;
        }
    }

    public boolean checkPermissionPreferences(int permission) {
        boolean allowed = false;
        switch (permission) {
            case CONTACT_PERMISSION: {
                allowed = contactReadStatus;
            }
        }

        if (!allowed) {
            Toast.makeText(ctx, ctx.getResources().getString(R.string.alert_contats_dialog_title),
                    Toast.LENGTH_SHORT).show();
        }

        return allowed;
    }
}
