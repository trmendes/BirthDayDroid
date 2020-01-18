package com.tmendes.birthdaydroid;

public class DBContact {
    private final int id;
    private final boolean favorite;
    private final boolean ignore;

    public DBContact(int id, boolean favorite, boolean ignore) {
        this.id = id;
        this.favorite = favorite;
        this.ignore = ignore;
    }

    public int getId() {
        return this.id;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public boolean isIgnore() {
        return ignore;
    }
}
