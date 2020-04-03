package com.tmendes.birthdaydroid.helpers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AccountHelper {
    private Context ctx;
    private SharedPreferences prefs;

    public void init(Context ctx) {
        this.ctx = ctx;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public List<Account> getAccounts() {
        return Arrays.asList(AccountManager.get(ctx).getAccounts());
    }

    public List<Account> getIgnoredAccounts() {
        Set<String> ignoredAccountStringArray = prefs.getStringSet("ignored_accounts",
                Collections.<String>emptySet());

        List<Account> ignoredAccountList = new ArrayList<>();
        for (String ignoredAccountString : ignoredAccountStringArray) {
            ignoredAccountList.add(convertStringToAccount(ignoredAccountString));
        }

        return ignoredAccountList;
    }

    private static final String ACCOUNT_DELIMITER = "\u0000";

    public String convertAccountToString(Account account) {
        return account.name + ACCOUNT_DELIMITER + account.type;
    }

    public Account convertStringToAccount(String string) {
        int splitIndex = string.indexOf(ACCOUNT_DELIMITER);
        if (splitIndex == -1) {
            throw new IllegalArgumentException("An string with account information must contain one U+0000 sign as delimiter");
        } else {
            return new Account(
                    string.substring(0, splitIndex),
                    string.substring(splitIndex + ACCOUNT_DELIMITER.length())
            );
        }
    }
}
