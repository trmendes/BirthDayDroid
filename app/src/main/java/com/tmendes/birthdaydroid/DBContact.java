package com.tmendes.birthdaydroid;

public class DBContact {
    private int id;
    private String cid;
    private boolean favorite;
    private boolean ignore;

    public DBContact(int id, String cid, boolean favorite, boolean ignore) {
        this.id = id;
        this.cid = cid;
        this.favorite = favorite;
        this.ignore = ignore;
    }

    public int getId() {
        return this.id;
    }

    public String getCid() {
        return cid;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public boolean isIgnore() {
        return ignore;
    }
}
