package com.tmendes.birthdaydroid.permission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.tmendes.birthdaydroid.R;

public class PermissionGranter extends PermissionChecker {
    private static final int READ_CONTACT_REQUEST_CODE = 100;
    private final Activity activity;
    private final GrantedListener grantedListener;

    public PermissionGranter(Activity activity, GrantedListener grantedListener) {
        super(activity);
        this.activity = activity;
        this.grantedListener = grantedListener;
    }

    public void grandReadContactOrExit() {
        if (!checkReadContactsPermission()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CONTACTS)) {
                displayPermissionExplanation();
            } else {
                requestReadContactsPermission();
            }
        } else {
            runGrantedListener();
        }
    }

    private void runGrantedListener() {
        if (grantedListener != null) {
            grantedListener.onGranted();
        }
    }

    private void requestReadContactsPermission() {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.READ_CONTACTS},
                READ_CONTACT_REQUEST_CODE);
    }

    private void displayPermissionExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(activity.getResources().getString(R.string.alert_contacts_dialog_msg));
        builder.setTitle(activity.getString(R.string.alert_contats_dialog_title));

        builder.setPositiveButton(activity.getString(R.string.alert_permissions_allow),
                (dialog, which) -> this.requestReadContactsPermission());

        builder.setNegativeButton(activity.getString(R.string.alert_permissions_deny),
                (dialog, which) -> {
                    activity.finish();
                    System.exit(0);
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void onRequestPermissionsResultForReadContacts(int requestCode, @NonNull String[] permissions,
                                                          @NonNull int[] grantResults) {
        if (requestCode == READ_CONTACT_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (Manifest.permission.READ_CONTACTS.equals(permissions[i])
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    runGrantedListener();
                }
            }
            displayPermissionExplanation();
        }
    }

    public interface GrantedListener {
        void onGranted();
    }
}
