package com.tmendes.birthdaydroid.views.contactlist;

import android.view.View;
import android.widget.TextView;

import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.zodiac.ZodiacResourceHelper;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ContactCompactViewHolder extends AbstractContactViewHolder {

    private final ZodiacResourceHelper zodiacResourceHelper;

    private final TextView name;
    private final TextView eventTypeTextView;
    private final TextView celebrationTextView;
    private final TextView eventTextView;
    private final TextView badgesTextView;

    public ContactCompactViewHolder(View view, ZodiacResourceHelper zodiacResourceHelper) {
        super(view);

        this.zodiacResourceHelper = zodiacResourceHelper;

        name = view.findViewById(R.id.contactNameTextView);

        eventTextView = view.findViewById(R.id.dateTextView);
        eventTypeTextView = view.findViewById(R.id.eventTypeTextView);
        celebrationTextView = view.findViewById(R.id.missingDaysTextView);
        badgesTextView = view.findViewById(R.id.contactStatusTextView);
    }

    public void setupNewContact(Contact contact) {
        super.setupNewContact(contact);

        setupName();
        setupEventDateMessage();
        setupEventType();
        setupCelebrationMessage();
        setupContactBadges();
    }

    private void setupEventType() {
        String eventTypeLabel = getContact().getEventTypeLabel();

        if(!getContact().isEventMissingYear()) {
            int ageOnBirthDay = getContact().getAgeInYears();
            if (!getContact().isFromFuture() && !getContact().isCelebrationToday()) {
                ageOnBirthDay += 1;
            }
            eventTypeLabel += " (" + ageOnBirthDay + ")";
        }

        this.eventTypeTextView.setText(eventTypeLabel);
    }

    private void setupEventDateMessage() {
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM/dd - E", Locale.getDefault());
        if (getContact().isCelebrationThisYear() || getContact().isCelebrationRecent()) {
            this.eventTextView.setText(dateFormatter.format(getContact().getCurrentYearEvent()));
        } else {
            this.eventTextView.setText(dateFormatter.format(getContact().getNextYearEvent()));
        }
    }

    private void setupCelebrationMessage() {
        final String celebrationMsg;
        if (getContact().isCelebrationToday()) {
            celebrationMsg = getContext().getResources().getString(R.string.party_message_compact);
        } else if (getContact().isCelebrationRecent()) {
            celebrationMsg = getContext().getResources().getQuantityString(R.plurals.days_ago,
                    getContact().getDaysSinceLastEvent(),
                    getContact().getDaysSinceLastEvent());
        } else {
            celebrationMsg = getContext().getResources().getQuantityString(
                    R.plurals.days_to_go,
                    getContact().getDaysUntilNextEvent(),
                    getContact().getDaysUntilNextEvent()
            );
        }
        this.celebrationTextView.setText(celebrationMsg);
    }

    private void setupName() {
        this.name.setText(getContact().getName());
    }

    private void setupContactBadges() {
        final StringBuilder status = new StringBuilder();
        if (getContact().isCelebrationToday()) {
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

        this.badgesTextView.setText(status);
    }
}
