package com.tmendes.birthdaydroid.views.contactlist;

import android.view.View;
import android.widget.TextView;

import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.zodiac.ZodiacResourceHelper;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ContactCompactViewHolder extends AbstractContactViewHolder {
    private static final int MAX_DAYS_AGO = 7;

    private final ZodiacResourceHelper zodiacResourceHelper;

    private final TextView name;
    private final TextView eventTypeTextView;
    private final TextView missingDaysTextView;
    private final TextView dateTextView;
    private final TextView contactStatusTextView;

    public ContactCompactViewHolder(View view, ZodiacResourceHelper zodiacResourceHelper) {
        super(view);

        this.zodiacResourceHelper = zodiacResourceHelper;

        name = view.findViewById(R.id.contactNameTextView);

        dateTextView = view.findViewById(R.id.dateTextView);
        eventTypeTextView = view.findViewById(R.id.eventTypeTextView);
        missingDaysTextView = view.findViewById(R.id.missingDaysTextView);
        contactStatusTextView = view.findViewById(R.id.contactStatusTextView);
    }

    public void setupNewContact(Contact contact) {
        super.setupNewContact(contact);

        setupName();
        setupDate();
        setupEventType();
        setupMissingDays();
        setupStatus();
    }

    private void setupEventType() {
        String eventTypeLabel = getContact().getEventTypeLabel();

        if(!getContact().isMissingYearInfo()) {
            int ageOnBirthDay = getContact().getAgeInYears();
            if (!getContact().isBornInFuture() && !getContact().isBirthdayToday()) {
                ageOnBirthDay += 1;
            }
            eventTypeLabel += " (" + ageOnBirthDay + ")";
        }

        this.eventTypeTextView.setText(eventTypeLabel);
    }

    private void setupDate() {
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM/dd - E", Locale.getDefault());
        this.dateTextView.setText(dateFormatter.format(getContact().getNextBirthday()));
    }

    private void setupMissingDays() {
        final String partyMsg;
        if (getContact().isBirthdayToday()) {
            partyMsg = getContext().getResources().getString(R.string.party_message_compact);
        } else if (getContact().getDaysSinceLastBirthday() <= MAX_DAYS_AGO && !getContact().isBornInFuture()) {
            partyMsg = getContext().getResources().getQuantityString(R.plurals.days_ago,
                    getContact().getDaysSinceLastBirthday(),
                    getContact().getDaysSinceLastBirthday());
        } else {
            partyMsg = getContext().getResources().getQuantityString(
                    R.plurals.days_to_go,
                    getContact().getDaysUntilNextBirthday(),
                    getContact().getDaysUntilNextBirthday()
            );
        }
        this.missingDaysTextView.setText(partyMsg);
    }

    private void setupName() {
        this.name.setText(getContact().getName());
    }

    private void setupStatus() {
        final StringBuilder status = new StringBuilder();
        if (getContact().isBirthdayToday()) {
            status.append(" ").append(getContext().getResources().getString(R.string.emoji_today_party));
        }

        /* Zodiac Icons */
        final boolean hideZodiac = getPrefs().getBoolean("hide_zodiac", false);
        if (!hideZodiac) {
            status.append(" ").append(zodiacResourceHelper.getZodiacSymbol(getContact().getZodiac()));
            status.append(" ").append(zodiacResourceHelper.getZodiacElementSymbol(getContact().getZodiac()));
        }

        /* Favorite/Ignore Icons */
        if (getContact().isIgnore()) {
            status.append(" ").append(getContext().getResources().getString(R.string.emoji_block));
        }
        if (getContact().isFavorite()) {
            status.append(" ").append(getContext().getResources().getString(R.string.emoji_heart));
        }

        this.contactStatusTextView.setText(status);
    }
}
