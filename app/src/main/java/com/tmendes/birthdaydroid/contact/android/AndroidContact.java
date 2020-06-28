package com.tmendes.birthdaydroid.contact.android;

public class AndroidContact {
    private final String lookupKey;
    private final String startDate;
    private final int eventType;
    private final String eventLabel;
    private final String photoThumbnailUri;
    private final String displayName;

    public AndroidContact(String lookupKey, String startDate, int eventType, String eventLabel, String photoThumbnailUri, String displayName) {
        this.lookupKey = lookupKey;
        this.startDate = startDate;
        this.eventType = eventType;
        this.eventLabel = eventLabel;
        this.photoThumbnailUri = photoThumbnailUri;
        this.displayName = displayName;
    }

    public String getLookupKey() {
        return lookupKey;
    }

    public String getStartDate() {
        return startDate;
    }

    public int getEventType() {
        return eventType;
    }

    public String getEventLabel() {
        return eventLabel;
    }

    public String getPhotoThumbnailUri() {
        return photoThumbnailUri;
    }

    public String getDisplayName() {
        return displayName;
    }
}
