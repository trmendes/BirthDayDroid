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
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tmendes.birthdaydroid.comparators.BirthDayComparator;
import com.tmendes.birthdaydroid.Contact;
import com.tmendes.birthdaydroid.R;

import java.util.ArrayList;
import java.util.Objects;

public class BirthDayArrayAdapter extends ArrayAdapter<Contact> implements Filterable {

    private final Context ctx;

    private ArrayList<Contact> birthDayList;
    private final ArrayList<Contact> bdListToRestoreAfterFiltering;

    public BirthDayArrayAdapter(Context ctx, ArrayList<Contact> contactsBirthDays) {
        super(ctx, -1, contactsBirthDays);
        this.ctx = ctx;
        this.birthDayList = contactsBirthDays;
        this.bdListToRestoreAfterFiltering = this.birthDayList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolderItem viewHolder;

        final Contact contact = birthDayList.get(position);

        if (!contact.isMissingData()) {

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

                viewHolder.signElement = convertView.findViewById(R.id.tvSignAndElement);

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


            viewHolder.name.setText(contact.getName());

            viewHolder.birthDayWeekName
                    .setText(ctx.getResources()
                            .getString(R.string.next_week_name, contact.getNextBirtDayWeekName()));

            viewHolder.signElement.setText(
                    ctx.getResources().getString(R.string.dual_string,
                            contact.getSign(), contact.getSignElement()));

            viewHolder.bornOn.setText(
                    ctx.getResources().getString(
                            R.string.birthday_string, contact.getMonthName(), contact.getDay())
            );

            if (contact.getPhotoURI() != null) {
                viewHolder.picture.setImageURI(Uri.parse(contact.getPhotoURI()));
            } else {
                viewHolder.picture.setImageDrawable(
                        ContextCompat.getDrawable(
                                ctx, R.drawable.ic_account_circle_black_24dp));
            }

            if (contact.shallWeCelebrateToday()) {
                viewHolder.daysToGo.setText(
                        ctx.getResources().getString(R.string.days_until_birthday));
                viewHolder.emojiPartyTomorrow.setVisibility(View.INVISIBLE);
                viewHolder.emojiParty.setVisibility(View.VISIBLE);
            } else {
                if (contact.getDaysUntilNextBirthDay() == 1) {
                    viewHolder.daysToGo.setText(
                            ctx.getResources().getString(R.string.birthday_tomorrow));
                    viewHolder.emojiPartyTomorrow.setVisibility(View.VISIBLE);
                    viewHolder.emojiParty.setVisibility(View.INVISIBLE);
                } else {
                    viewHolder.daysToGo
                            .setText(
                                    ctx.getResources().getString(
                                    R.string.days_until, contact.getDaysUntilNextBirthDay()));
                    viewHolder.emojiPartyTomorrow.setVisibility(View.INVISIBLE);
                    viewHolder.emojiParty.setVisibility(View.INVISIBLE);
                }
            }

            if (contact.isMissingYear()) {
                viewHolder.age.setText(ctx.getResources().getString(R.string.contact_has_no_year));
            } else {
                if (contact.getAge() != 0) {
                    viewHolder.age.setText(ctx.getResources()
                            .getString(R.string.years_old, contact.getAge()));
                } else {
                    viewHolder.age.setText(ctx.getResources()
                                .getString(R.string.days_old, contact.getDaysAge()));
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
        }

        return convertView;
    }

    static class ViewHolderItem {
        TextView name;
        TextView birthDayWeekName;
        TextView age;
        TextView daysToGo;
        TextView signElement;
        TextView bornOn;
        ImageView picture;
        LinearLayout emojiParty;
        LinearLayout emojiPartyTomorrow;
    }

    @Override
    public int getCount() {
        return birthDayList.size();
    }

    @Override
    public Contact getItem(int position) {
        return birthDayList.get(position);
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
                    birthDayList = bdListToRestoreAfterFiltering;
                } else {
                    //noinspection unchecked
                    birthDayList = (ArrayList<Contact>) results.values;
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
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < bdListToRestoreAfterFiltering.size(); i++) {
                        String data = bdListToRestoreAfterFiltering.get(i).getName();
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            Contact contact = new Contact(ctx,
                                    bdListToRestoreAfterFiltering.get(i).getKey(),
                                    bdListToRestoreAfterFiltering.get(i).getName(),
                                    bdListToRestoreAfterFiltering.get(i).getDate(),
                                    bdListToRestoreAfterFiltering.get(i).getPhotoURI());
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
}
