package com.tmendes.birthdaydroid.contact;

import java.util.ArrayList;
import java.util.List;

public class ContactCache {
    private static ContactCache INSTANCE;
    private List<Contact> contacts = new ArrayList<>();

    private ContactCache() {
    }

    public static synchronized ContactCache getInstance() {
        if(INSTANCE == null){
            INSTANCE = new ContactCache();
        }
        return INSTANCE;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
}
