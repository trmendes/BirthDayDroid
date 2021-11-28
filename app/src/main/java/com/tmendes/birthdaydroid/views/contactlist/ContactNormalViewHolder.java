package com.tmendes.birthdaydroid.views.contactlist;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.zodiac.ZodiacResourceHelper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ContactNormalViewHolder extends AbstractContactViewHolder {

    private final ZodiacResourceHelper zodiacResourceHelper;

    private final TextView contactNameTextView;
    private final TextView eventTypeTextView;
    private final TextView ageTextView;
    private final TextView ageBadge;
    private final TextView celebrationMessageTextView;
    private final TextView eventTextView;
    private final TextView contactBadgesTextView;
    private final ImageView picture;

    public ContactNormalViewHolder(View view, ZodiacResourceHelper zodiacResourceHelper) {
        super(view);
        this.zodiacResourceHelper = zodiacResourceHelper;

        contactNameTextView = view.findViewById(R.id.contactNameTextView);
        contactBadgesTextView = view.findViewById(R.id.contactStatusTextView);

        eventTextView = view.findViewById(R.id.dateTextView);
        eventTypeTextView = view.findViewById(R.id.eventTypeTextView);
        celebrationMessageTextView = view.findViewById(R.id.missingDaysTextView);
        ageTextView = view.findViewById(R.id.ageTextView);

        picture = view.findViewById(R.id.ivContactPicture);
        ageBadge = view.findViewById(R.id.tvAgeBadge);
    }

    public void setupNewContact(Contact contact) {
        super.setupNewContact(contact);

        setupName();
        setupEventDateMessage();
        setupEventType();
        setupCelebrationMessage();
        setupAgeTextView();

        setupPicture();
        setupAgeBadge();

        setupStatus();
    }

    private void setupStatus() {
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

        this.contactBadgesTextView.setText(status);
    }

    private void setupAgeTextView() {
        if (getContact().isEventMissingYear()) {
            this.ageTextView.setVisibility(View.INVISIBLE);
            return;
        } else {
            this.ageTextView.setVisibility(View.VISIBLE);
        }

        final int ageInYears = getContact().getAgeInYears();
        final String ageText;
        if (getContact().isCelebrationToday()) {
            ageText = getContext().getResources().getQuantityString(
                    R.plurals.years_old, ageInYears, ageInYears);
        } else if (ageInYears == 0) {
            int ageInDays = getContact().getAgeInDays();
            ageText = getContext().getResources().getQuantityString(
                    R.plurals.days_old, ageInDays, ageInDays);
        } else {
            ageText = getContext().getResources().getQuantityString(
                    R.plurals.years_old, ageInYears, ageInYears);
        }
        this.ageTextView.setText(ageText);
    }

    private void setupEventType() {
        this.eventTypeTextView.setText(getContact().getEventTypeLabel());
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
            celebrationMsg = getContext().getResources().getString(R.string.party_message);
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
        this.celebrationMessageTextView.setText(celebrationMsg);
    }

    private void setupName() {
        this.contactNameTextView.setText(getContact().getName());
    }

    private void setupAgeBadge() {
        if (getContact().isEventMissingYear()) {
            this.ageBadge.setVisibility(View.INVISIBLE);
            return;
        } else {
            this.ageBadge.setVisibility(View.VISIBLE);
        }

        final String ageText;
        final boolean showCurrentAge = getPrefs().getBoolean("show_current_age", false);
        if (getContact().isCelebrationToday() || showCurrentAge) {
            ageText = String.valueOf(getContact().getAgeInYears());
        } else {
            ageText = "↑" + (getContact().getAgeInYears() + (getContact().isFromFuture() ? 0 : 1));
        }
        this.ageBadge.setText(ageText);
    }

    private void setupPicture() {
        if (getContact().getPhotoUri() != null) {
            try {
                final Bitmap src = MediaStore.Images.Media
                        .getBitmap(getContext().getContentResolver(), Uri.parse(getContact().getPhotoUri()));
                final RoundedBitmapDrawable picture = RoundedBitmapDrawableFactory.create(getContext().getResources(), src);
                picture.setCornerRadius(Math.max(src.getWidth(), src.getHeight()) / 2.0f);
                this.picture.setImageDrawable(picture);
            } catch (IOException | NullPointerException e) {
                setupDefaultPicture();
            }
        } else {
            setupDefaultPicture();
        }
    }

    private void setupDefaultPicture() {
        this.picture.setImageDrawable(
                ContextCompat.getDrawable(getContext(), R.drawable.ic_account_circle_black_48dp)
        );
    }
}
