/*
 * Copyright (C) 2015-2016 The Food Restriction Project Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tmendes.birthdaydroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tmendes.birthdaydroid.Contact;
import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.comparators.BirthDayComparator;
import com.tmendes.birthdaydroid.providers.BirthdayDataProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

public class BirthDayArrayAdapter extends ArrayAdapter<Contact> implements Filterable {

    private final Context ctx;
    private final ArrayList<Contact> bdListToRestoreAfterFiltering;
    private ArrayList<Contact> contactList;

    private final boolean isNowLeapYear;
    private final boolean hideZoadiac, hideNoYearMsg, showCurrentAge;

    private final int LATE_BDD_LIST_TRESHOLD;
    private final int WEEK_LEN = 7;
    private final int YEAR_LEN = 365;
    private final int LEAP_YEAR_LEN = 366;

    public BirthDayArrayAdapter(Context ctx, ArrayList<Contact> contactList) {
        super(ctx, -1, contactList);

        this.ctx = ctx;
        this.contactList = contactList;
        this.bdListToRestoreAfterFiltering = contactList;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        hideZoadiac = prefs.getBoolean("hide_zodiac", false);
        hideNoYearMsg = prefs.getBoolean("hide_no_year_msg", false);
        showCurrentAge = prefs.getBoolean("show_current_age", false);


        this.isNowLeapYear = new GregorianCalendar().isLeapYear(
                Calendar.getInstance().get(Calendar.YEAR));

        if (this.isNowLeapYear) {
            this.LATE_BDD_LIST_TRESHOLD = this.LEAP_YEAR_LEN- this.WEEK_LEN;
        } else {
            this.LATE_BDD_LIST_TRESHOLD = this.YEAR_LEN - this.WEEK_LEN;
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolderItem viewHolder;

        final Contact contact = contactList.get(position);

        String name = contact.getName();
        String photoUri = contact.getPhotoURI();

        int age = contact.getAge();
        int daysOld = contact.getDaysOld();

        if (!showCurrentAge) {
            ++age;
        }

        String bornOnMonthName = contact.getBornOnMonthName();

        int bornOnDay = contact.getBornOnDay();

        String nextBirthdayWeekName = contact.getNextBirthDayWeekName();
        int daysUntilNextBirthday = contact.getDaysUntilNextBirthday();

        String zodiacSign = contact.getZodiac();
        String zodiacSignElement = contact.getZodiacElement();

        String eventTypeLabel = contact.getEventTypeLabel();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(inflater)
                    .inflate(R.layout.contact_list_item, parent, false);

            viewHolder = new ViewHolderItem();

            viewHolder.name = convertView.findViewById(R.id.tvContactName);

            viewHolder.birthDayWeekName =
                    convertView.findViewById(R.id.tvContactNextBirthDayWeekName);

            viewHolder.age = convertView.findViewById(R.id.tvContactAge);

            viewHolder.daysToGo =
                    convertView.findViewById(R.id.tvContactDaysUntil);

            viewHolder.zodiacElement = convertView.findViewById(R.id.tvZodiac);
            if (this.hideZoadiac) {
                viewHolder.zodiacElement.setVisibility(View.INVISIBLE);
            }

            viewHolder.bornOn = convertView.findViewById(R.id.tvBirthDay);

            viewHolder.picture =
                    convertView.findViewById(R.id.ivContactPicture);

            viewHolder.emojiParty = convertView.findViewById(R.id.emojiParty);

            viewHolder.emojiPartyTomorrow =
                    convertView.findViewById(R.id.emojiPartyTomorrow);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }


        viewHolder.name.setText(name);
        viewHolder.birthDayWeekName
                .setText(ctx.getResources()
                        .getString(R.string.next_week_name, eventTypeLabel, nextBirthdayWeekName));
        viewHolder.zodiacElement.setText(
                ctx.getResources().getString(R.string.dual_string,
                        zodiacSign, zodiacSignElement));
        viewHolder.bornOn.setText(
                ctx.getResources().getString(
                        R.string.birthday_string, bornOnMonthName, bornOnDay));


        if (photoUri != null) {
            try {
                Bitmap src = MediaStore
                        .Images
                        .Media
                        .getBitmap(ctx.getContentResolver(), Uri.parse(photoUri));

                RoundedBitmapDrawable dr =
                        RoundedBitmapDrawableFactory.create(ctx.getResources(), src);
                dr.setCornerRadius(Math.max(src.getWidth(), src.getHeight()) / 2.0f);

                viewHolder.picture.setImageDrawable(dr);
            } catch (IOException e) {
                viewHolder.picture.setImageDrawable(
                        ContextCompat.getDrawable(
                                ctx, R.drawable.ic_account_circle_black_24dp));
            }

        } else {
            viewHolder.picture.setImageDrawable(
                    ContextCompat.getDrawable(
                            ctx, R.drawable.ic_account_circle_black_24dp));
        }

        /* Party */
        if (contact.getDaysUntilNextBirthday() >= 0 && contact.shallWePartyToday()) {
            if (contact.getDaysUntilNextBirthday() == 0) {
                viewHolder.daysToGo.setText(
                        ctx.getResources().getString(R.string.party_message));
            } else {
                viewHolder.daysToGo
                        .setText(
                                ctx.getResources().getQuantityString(
                                        R.plurals.days_until,
                                        daysUntilNextBirthday,
                                        daysUntilNextBirthday,
                                        eventTypeLabel));
            }
            viewHolder.emojiPartyTomorrow.setVisibility(View.INVISIBLE);
            viewHolder.emojiParty.setVisibility(View.VISIBLE);
        } else if (contact.getDaysUntilNextBirthday() == 1) {
            viewHolder.daysToGo
                    .setText(
                            ctx.getResources().getQuantityString(
                                    R.plurals.days_until,
                                    daysUntilNextBirthday,
                                    daysUntilNextBirthday,
                                    eventTypeLabel));
            viewHolder.emojiPartyTomorrow.setVisibility(View.VISIBLE);
            viewHolder.emojiParty.setVisibility(View.INVISIBLE);
        } else {
            /* Days Ago */
            if (daysUntilNextBirthday >= this.LATE_BDD_LIST_TRESHOLD && !contact.isNotYetBorn()) {
                long daysAgo;

                if (this.isNowLeapYear) {
                    daysAgo = this.LEAP_YEAR_LEN - contact.getDaysUntilNextBirthday() + 1;
                } else {
                    daysAgo = this.YEAR_LEN - contact.getDaysUntilNextBirthday() + 1;
                }

                viewHolder.birthDayWeekName
                        .setText(ctx.getResources()
                                .getString(R.string.prev_week_name, eventTypeLabel,
                                        contact.getPrevBirthDayWeekName()));

                viewHolder.daysToGo
                        .setText(
                                ctx.getResources().getQuantityString(
                                        R.plurals.days_ago,
                                        (int) daysAgo,
                                        (int) daysAgo));
            } else {
                /* All the rest */
                viewHolder.daysToGo
                        .setText(
                                ctx.getResources().getQuantityString(
                                        R.plurals.days_until,
                                        daysUntilNextBirthday,
                                        daysUntilNextBirthday,
                                        eventTypeLabel));
            }
            viewHolder.emojiPartyTomorrow.setVisibility(View.INVISIBLE);
            viewHolder.emojiParty.setVisibility(View.INVISIBLE);
        }

        if (!contact.isYearSettled()) {
            if (hideNoYearMsg) {
                viewHolder.age.setText("");
            } else {
                viewHolder.age.setText(ctx.getResources().getString(R.string.contact_has_no_year));
            }
        } else if (contact.isHeSheNotEvenOneYearOld() && showCurrentAge) {
            viewHolder.age.setText(ctx.getResources().getQuantityString(
                        R.plurals.days_old, daysOld, daysOld));
        }
        else {
            if (showCurrentAge) {
                viewHolder.age.setText(ctx.getResources().getQuantityString(
                        R.plurals.years_old, age, age));
            } else {
                viewHolder.age.setText(ctx.getResources().getQuantityString(
                        R.plurals.turning_years_old, age, age));
            }
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(
                        ContactsContract.Contacts.CONTENT_LOOKUP_URI
                                + "/" + contact.getKey()));
                ctx.startActivity(i);
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Contact getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                if (constraint.length() == 0) {
                    contactList = bdListToRestoreAfterFiltering;
                } else {
                    contactList = (ArrayList<Contact>) results.values;
                }
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Contact> FilteredArrList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    results.count = bdListToRestoreAfterFiltering.size();
                    results.values = bdListToRestoreAfterFiltering;
                } else {
                    String name, age, daysOld, birthdayWeekName, monthName, zodiac, zodiacElement, constraintStr;
                    constraintStr = constraint.toString().toLowerCase();

                    for (int i = 0; i < bdListToRestoreAfterFiltering.size(); i++) {
                        name = bdListToRestoreAfterFiltering.get(i).getName().toLowerCase();
                        monthName = bdListToRestoreAfterFiltering.get(i).getBornOnMonthName().toLowerCase();
                        birthdayWeekName = bdListToRestoreAfterFiltering.get(i).getNextBirthDayWeekName().toLowerCase();
                        zodiac = bdListToRestoreAfterFiltering.get(i).getZodiac().toLowerCase();
                        zodiacElement = bdListToRestoreAfterFiltering.get(i).getZodiacElement().toLowerCase();
                        age = Integer.toString(bdListToRestoreAfterFiltering.get(i).getAge());
                        daysOld = Integer.toString(bdListToRestoreAfterFiltering.get(i).getDaysOld());

                        if (name.contains(constraintStr) ||
                                age.startsWith(constraintStr) ||
                                daysOld.startsWith(constraintStr) ||
                                monthName.contains(constraintStr) ||
                                birthdayWeekName.contains(constraint) ||
                                zodiac.contains(constraintStr) ||
                                zodiacElement.contains(constraintStr)) {

                            Contact contact = BirthdayDataProvider.getInstance().parseNewContact(
                                    bdListToRestoreAfterFiltering.get(i).getKey(),
                                    bdListToRestoreAfterFiltering.get(i).getName(),
                                    bdListToRestoreAfterFiltering.get(i).getPhotoURI(),
                                    bdListToRestoreAfterFiltering.get(i).getDate(),
                                    bdListToRestoreAfterFiltering.get(i).getEventType(),
                                    bdListToRestoreAfterFiltering.get(i).getEventTypeLabel()
                            );

                            FilteredArrList.add(contact);
                        }
                    }
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }

                return results;
            }
        };
    }

    public void sort(int order, int sortType) {
        super.sort(new BirthDayComparator(order, sortType));
        notifyDataSetChanged();
    }

    static class ViewHolderItem {
        TextView name;
        TextView birthDayWeekName;
        TextView age;
        TextView daysToGo;
        TextView zodiacElement;
        TextView bornOn;
        ImageView picture;
        LinearLayout emojiParty;
        LinearLayout emojiPartyTomorrow;
    }
}
