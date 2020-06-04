package com.tmendes.birthdaydroid.contact;

import android.content.Context;
import android.provider.ContactsContract;
import android.text.TextUtils;

public class EventTypeLabelService {
    private final Context context;

    public EventTypeLabelService(Context context) {
        this.context = context;
    }

    public String getEventTypeLabel(int eventType, String eventTypeLabel) {
        if (isCustomEventTypeLabel(eventType, eventTypeLabel)) {
            return eventTypeLabel;
        } else {
            final String localeLabel = ContactsContract.CommonDataKinds.Event.getTypeLabel(context.getResources(), eventType, eventTypeLabel).toString();
            return localeLabel.substring(0,1).toUpperCase() + localeLabel.substring(1).toLowerCase();
        }
    }

    private boolean isCustomEventTypeLabel(int eventType, String eventTypeLabel){
        return eventType == ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM && !TextUtils.isEmpty(eventTypeLabel);
    }
}
