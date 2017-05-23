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

package com.tmendes.birthdaydroid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
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

import java.util.ArrayList;

class BirthDayArrayAdapter extends ArrayAdapter<ContactData> implements Filterable {

    private final Context ctx;
    private ArrayList<ContactData> contactsBirthDays;
    private final ArrayList<ContactData> orglBDList;

    public BirthDayArrayAdapter(Context ctx, ArrayList<ContactData> contactsBirthDays) {
        super(ctx, -1, contactsBirthDays);
        this.ctx = ctx;
        this.contactsBirthDays = contactsBirthDays;
        this.orglBDList = contactsBirthDays;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolderItem viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contact_list_item, parent, false);

            viewHolder = new ViewHolderItem();

            viewHolder.tvContactName = (TextView) convertView.findViewById(R.id.tvContactName);
            viewHolder.tvContactNextBirthDayWeekName = (TextView) convertView.findViewById(R.id.tvContactNextBirthDayWeekName);
            viewHolder.tvContactAge = (TextView) convertView.findViewById(R.id.tvContactAge);
            viewHolder.tvContactDaysToGo = (TextView) convertView.findViewById(R.id.tvContactDaysUntil);
            viewHolder.tvSignElement = (TextView) convertView.findViewById(R.id.tvSignAndElement);
            viewHolder.tvBirthDay = (TextView) convertView.findViewById(R.id.tvBirthDay);
            viewHolder.ivContactPicture = (ImageView) convertView.findViewById(R.id.ivContactPicture);
            viewHolder.emojiParty = (LinearLayout) convertView.findViewById(R.id.emojiParty);
            viewHolder.emojiPartyTomorrow = (LinearLayout) convertView.findViewById(R.id.emojiPartyTomorrow);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        final ContactData contact = contactsBirthDays.get(position);

        if (contact != null) {
            viewHolder.tvContactName.setText(contact.getName());
            viewHolder.tvContactNextBirthDayWeekName.setText(ctx.getResources().getString(R.string.next_week_name, contact.getNextBirtDayWeekName()));
            viewHolder.tvSignElement.setText(ctx.getResources().getString(R.string.dual_string, contact.getSign(), contact.getSignElement()));

            viewHolder.tvBirthDay.setText(ctx.getResources().getString(R.string.birthday_string, contact.getMonthName(), contact.getDay()));

            if (contact.getPhotoURI() != null) {
                viewHolder.ivContactPicture.setImageURI(Uri.parse(contact.getPhotoURI()));
            } else {
                viewHolder.ivContactPicture.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_account_circle_white_24dp));
            }

            if (contact.isThereAPartyToday()) {
                viewHolder.tvContactDaysToGo.setText(ctx.getResources().getString(R.string.days_until_birthday));
                viewHolder.emojiPartyTomorrow.setVisibility(View.INVISIBLE);
                viewHolder.emojiParty.setVisibility(View.VISIBLE);
            } else {
                if (contact.getDaysUntilNextBirthDay() == 1) {
                    viewHolder.tvContactDaysToGo.setText(ctx.getResources().getString(R.string.birthday_tomorrow));
                    viewHolder.emojiPartyTomorrow.setVisibility(View.VISIBLE);
                    viewHolder.emojiParty.setVisibility(View.INVISIBLE);
                } else {
                    viewHolder.tvContactDaysToGo.setText(ctx.getResources().getString(R.string.days_until, contact.getDaysUntilNextBirthDay()));
                    viewHolder.emojiPartyTomorrow.setVisibility(View.INVISIBLE);
                    viewHolder.emojiParty.setVisibility(View.INVISIBLE);
                }
            }

            if (contact.hasYear()) {
                if (contact.getAge() != 0) {
                    viewHolder.tvContactAge.setText(ctx.getResources().getString(R.string.years_old, contact.getAge()));
                } else {
                    if (contact.getMonthAge() != 0 ) {
                        viewHolder.tvContactAge.setText(ctx.getResources().getString(R.string.months_old, contact.getMonthAge()));
                    } else {
                        viewHolder.tvContactAge.setText(ctx.getResources().getString(R.string.days_old, contact.getDaysAge()));
                    }
                }
            } else {
                viewHolder.tvContactAge.setText(ctx.getResources().getString(R.string.contact_has_no_year));
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i;
                    i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(ContactsContract.Contacts.CONTENT_LOOKUP_URI + "/" + contact.getKey()));
                    ctx.startActivity(i);
                }
            });
        }

        return convertView;
    }

    static class ViewHolderItem {
        TextView tvContactName;
        TextView tvContactNextBirthDayWeekName;
        TextView tvContactAge;
        TextView tvContactDaysToGo;
        TextView tvSignElement;
        TextView tvBirthDay;
        ImageView ivContactPicture;
        LinearLayout emojiParty;
        LinearLayout emojiPartyTomorrow;
    }

    @Override
    public int getCount() {
        return contactsBirthDays.size();
    }

    @Override
    public ContactData getItem(int position) {
        return contactsBirthDays.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                contactsBirthDays = (ArrayList<ContactData>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<ContactData> FilteredArrList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    results.count = orglBDList.size();
                    results.values = orglBDList;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < orglBDList.size(); i++) {
                        String data = orglBDList.get(i).getName();
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            ContactData contact = new ContactData(ctx, orglBDList.get(i).getKey(), orglBDList.get(i).getName(), orglBDList.get(i).getDate(), orglBDList.get(i).getPhotoURI());
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
