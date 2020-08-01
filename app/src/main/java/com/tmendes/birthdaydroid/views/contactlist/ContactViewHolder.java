package com.tmendes.birthdaydroid.views.contactlist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.zodiac.ZodiacResourceHelper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ContactViewHolder extends RecyclerView.ViewHolder {
    private static final int MAX_DAYS_AGO = 7;

    private final ZodiacResourceHelper zodiacResourceHelper;

    private final TextView name;
    private final TextView lineTwo;
    private final TextView lineFour;
    private final TextView ageBadge;
    private final TextView lineThree;
    private final TextView lineOne;
    private final TextView contactStatus;
    private final ImageView picture;
    private final RelativeLayout ignoreContactLayout;
    private final RelativeLayout favoriteContactLayout;
    private final View itemLayout;
    private final Context context;

    private Contact contact;

    public ContactViewHolder(View view, ZodiacResourceHelper zodiacResourceHelper) {
        super(view);
        this.context = view.getContext();
        this.zodiacResourceHelper = zodiacResourceHelper;

        view.setOnClickListener(v ->
                openContactReadView()
        );

        view.setOnLongClickListener(v -> {
            openContactEditView();
            return true;
        });

        name = view.findViewById(R.id.tvContactName);
        contactStatus = view.findViewById(R.id.tvStatus);

        lineOne = view.findViewById(R.id.tvLineOne);
        lineTwo = view.findViewById(R.id.tvLineTwo);
        lineThree = view.findViewById(R.id.tvLineThree);
        lineFour = view.findViewById(R.id.tvLineFour);

        picture = view.findViewById(R.id.ivContactPicture);
        ageBadge = view.findViewById(R.id.tvAgeBadge);

        itemLayout = view.findViewById(R.id.view_item);
        ignoreContactLayout = view.findViewById(R.id.view_background_ignore);
        favoriteContactLayout = view.findViewById(R.id.view_background_favorite);
    }

    private void openContactReadView() {
        openContactView(Intent.ACTION_VIEW);
    }

    private void openContactEditView() {
        openContactView(Intent.ACTION_EDIT);
    }

    private void openContactView(String actionView) {
        Intent i = new Intent(actionView);
        i.setData(Uri.parse(
                ContactsContract.Contacts.CONTENT_LOOKUP_URI
                        + "/" + contact.getKey()));
        this.context.startActivity(i);
    }

    public void setupContact(Contact contact, boolean showCurrentAge, boolean hideZodiac) {
        this.ignoreContactLayout.setVisibility(View.INVISIBLE);
        this.favoriteContactLayout.setVisibility(View.INVISIBLE);

        this.contact = contact;

        setupName(contact);
        setupLineOne(contact);
        setupLineTwo(contact);
        setupLineThree(contact);
        setupLineFour(contact);

        setupPicture(contact);
        setupAgeBadge(contact, showCurrentAge);

        setupStatus(contact, hideZodiac);
    }

    private void setupStatus(Contact contact, boolean hideZodiac) {
        final StringBuilder status = new StringBuilder();
        if (contact.isBirthdayToday()) {
            status.append(" ").append(context.getResources().getString(R.string.emoji_today_party));
        }

        /* Zodiac Icons */
        if (!hideZodiac) {
            status.append(" ").append(zodiacResourceHelper.getZodiacSymbol(contact.getZodiac()));
            status.append(" ").append(zodiacResourceHelper.getZodiacElementSymbol(contact.getZodiac()));
        }

        /* Favorite/Ignore Icons */
        if (contact.isIgnore()) {
            status.append(" ").append(context.getResources().getString(R.string.emoji_block));
        }
        if (contact.isFavorite()) {
            status.append(" ").append(context.getResources().getString(R.string.emoji_heart));
        }

        this.contactStatus.setText(status);
    }

    private void setupLineFour(Contact contact) {
        if (contact.isMissingYearInfo()) {
            this.lineFour.setVisibility(View.INVISIBLE);
            return;
        } else {
            this.lineFour.setVisibility(View.VISIBLE);
        }

        final int ageInYears = contact.getAgeInYears();
        final String ageText;
        if (contact.isBirthdayToday()) {
            ageText = context.getResources().getQuantityString(
                    R.plurals.years_old, ageInYears, ageInYears);
        } else if (ageInYears == 0) {
            int ageInDays = contact.getAgeInDays();
            ageText = context.getResources().getQuantityString(
                    R.plurals.days_old, ageInDays, ageInDays);
        } else {
            ageText = context.getResources().getQuantityString(
                    R.plurals.years_old, ageInYears, ageInYears);
        }
        this.lineFour.setText(ageText);
    }

    private void setupLineTwo(Contact contact) {
        this.lineTwo.setText(contact.getEventTypeLabel());
    }

    private void setupLineOne(Contact contact) {
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM/dd - E", Locale.getDefault());
        this.lineOne.setText(dateFormatter.format(contact.getNextBirthday()));
    }

    private void setupLineThree(Contact contact) {
        final String partyMsg;
        if (contact.isBirthdayToday()) {
            partyMsg = context.getResources().getString(R.string.party_message);
        } else if (contact.getDaysSinceLastBirthday() <= MAX_DAYS_AGO && !contact.isBornInFuture()) {
            partyMsg = context.getResources().getQuantityString(R.plurals.days_ago,
                    contact.getDaysSinceLastBirthday(),
                    contact.getDaysSinceLastBirthday());
        } else {
            partyMsg = context.getResources().getQuantityString(
                    R.plurals.days_to_go,
                    contact.getDaysUntilNextBirthday(),
                    contact.getDaysUntilNextBirthday()
            );
        }
        this.lineThree.setText(partyMsg);
    }

    private void setupName(Contact contact) {
        this.name.setText(contact.getName());
    }

    private void setupAgeBadge(Contact contact, boolean showCurrentAge) {
        if (contact.isMissingYearInfo()) {
            this.ageBadge.setVisibility(View.INVISIBLE);
            return;
        } else {
            this.ageBadge.setVisibility(View.VISIBLE);
        }

        final String ageText;
        if (contact.isBirthdayToday() || showCurrentAge) {
            ageText = String.valueOf(contact.getAgeInYears());
        } else {
            ageText = "↑" + (contact.getAgeInYears() + (contact.isBornInFuture() ? 0 : 1));
        }
        this.ageBadge.setText(ageText);
    }

    private void setupPicture(Contact contact) {
        if (contact.getPhotoUri() != null) {
            try {
                final Bitmap src = MediaStore.Images.Media
                        .getBitmap(context.getContentResolver(), Uri.parse(contact.getPhotoUri()));
                final RoundedBitmapDrawable picture = RoundedBitmapDrawableFactory.create(context.getResources(), src);
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
                ContextCompat.getDrawable(context, R.drawable.ic_account_circle_black_48dp)
        );
    }

    public void setItemLayout() {
        favoriteContactLayout.setVisibility(View.INVISIBLE);
        ignoreContactLayout.setVisibility(View.INVISIBLE);
    }

    public void setSwipeIgnoreLayout() {
        favoriteContactLayout.setVisibility(View.INVISIBLE);
        ignoreContactLayout.setVisibility(View.VISIBLE);
    }

    public void setSwipeFavoriteLayout() {
        favoriteContactLayout.setVisibility(View.VISIBLE);
        ignoreContactLayout.setVisibility(View.INVISIBLE);
    }

    public View getItemLayout() {
        return itemLayout;
    }
}
