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
    private static final int MAX_DAYS_AGO = 7;

    private final ZodiacResourceHelper zodiacResourceHelper;

    private final TextView contactNameTextView;
    private final TextView eventTypeTextView;
    private final TextView ageTextView;
    private final TextView ageBadge;
    private final TextView missingDaysTextView;
    private final TextView dateTextView;
    private final TextView contactStatusTextView;
    private final ImageView picture;

    public ContactNormalViewHolder(View view, ZodiacResourceHelper zodiacResourceHelper) {
        super(view);
        this.zodiacResourceHelper = zodiacResourceHelper;

        contactNameTextView = view.findViewById(R.id.contactNameTextView);
        contactStatusTextView = view.findViewById(R.id.contactStatusTextView);

        dateTextView = view.findViewById(R.id.dateTextView);
        eventTypeTextView = view.findViewById(R.id.eventTypeTextView);
        missingDaysTextView = view.findViewById(R.id.missingDaysTextView);
        ageTextView = view.findViewById(R.id.ageTextView);

        picture = view.findViewById(R.id.ivContactPicture);
        ageBadge = view.findViewById(R.id.tvAgeBadge);
    }

    public void setupNewContact(Contact contact) {
        super.setupNewContact(contact);

        setupName();
        setupDate();
        setupEventType();
        setupMissingDays();
        setupAgeTextView();

        setupPicture();
        setupAgeBadge();

        setupStatus();
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

    private void setupAgeTextView() {
        if (getContact().isMissingYearInfo()) {
            this.ageTextView.setVisibility(View.INVISIBLE);
            return;
        } else {
            this.ageTextView.setVisibility(View.VISIBLE);
        }

        final int ageInYears = getContact().getAgeInYears();
        final String ageText;
        if (getContact().isBirthdayToday()) {
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

    private void setupDate() {
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM/dd - E", Locale.getDefault());
        this.dateTextView.setText(dateFormatter.format(getContact().getNextBirthday()));
    }

    private void setupMissingDays() {
        final String partyMsg;
        if (getContact().isBirthdayToday()) {
            partyMsg = getContext().getResources().getString(R.string.party_message);
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
        this.contactNameTextView.setText(getContact().getName());
    }

    private void setupAgeBadge() {
        if (getContact().isMissingYearInfo()) {
            this.ageBadge.setVisibility(View.INVISIBLE);
            return;
        } else {
            this.ageBadge.setVisibility(View.VISIBLE);
        }

        final String ageText;
        final boolean showCurrentAge = getPrefs().getBoolean("show_current_age", false);
        if (getContact().isBirthdayToday() || showCurrentAge) {
            ageText = String.valueOf(getContact().getAgeInYears());
        } else {
            ageText = "↑" + (getContact().getAgeInYears() + (getContact().isBornInFuture() ? 0 : 1));
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
