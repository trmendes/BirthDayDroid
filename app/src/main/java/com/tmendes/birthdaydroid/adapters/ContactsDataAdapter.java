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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tmendes.birthdaydroid.Contact;
import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.comparators.BirthDayComparator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class ContactsDataAdapter extends RecyclerView.Adapter<ContactsDataAdapter.ContactViewHolder>
        implements Filterable {
    private List<Contact> contacts;
    private final List<Contact> contactsOrignal;

    private final Context ctx;

    private final SharedPreferences prefs;

    private final int lateBDDListTreshold;
    private final boolean isNowLeapYear;

    private final int YEAR_LEN = 365;
    private final int LEAP_YEAR_LEN = 366;

    private int sortOrder;
    private int sortType;

    public ContactsDataAdapter(Context ctx, List<Contact> contacts) {
        this.contacts = contacts;
        this.contactsOrignal = contacts;
        this.ctx = ctx;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        this.isNowLeapYear = new GregorianCalendar().isLeapYear(
                Calendar.getInstance().get(Calendar.YEAR));

        int WEEK_LEN = 7;
        if (isNowLeapYear) {
            this.lateBDDListTreshold = this.LEAP_YEAR_LEN- WEEK_LEN;
        } else {
            this.lateBDDListTreshold = this.YEAR_LEN - WEEK_LEN;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);

        boolean hideZoadiac = prefs.getBoolean("hide_zodiac", false);
        boolean showCurrentAge = prefs.getBoolean("show_current_age", false);

        int age = contact.getAge();
        int daysOld = contact.getDaysOld();
        int bornOnDay = contact.getBornOnDay();
        int daysUntilNextBirthday = contact.getDaysUntilNextBirthday();
        String bornOnMonthName = contact.getBornOnMonthName();
        String name = contact.getName();
        String photoUri = contact.getPhotoURI();
        String nextBirthdayWeekName = contact.getNextBirthDayWeekName();
        String zodiacSign = contact.getZodiacSymbol();
        String zodiacSignElement = contact.getZodiacElementSymbol();
        String eventTypeLabel = contact.getEventTypeLabel();

        if (!showCurrentAge) {
            ++age;
        }

        holder.ignoreContactLayout.setVisibility(View.INVISIBLE);
        holder.favoriteContactLayout.setVisibility(View.INVISIBLE);
        holder.name.setText("");
        holder.contactStatus.setText("");
        holder.birthDayWeekName.setText("");
        holder.daysOld.setText("");
        holder.ageBadge.setText("");
        holder.daysToGo.setText("");
        holder.zodiacElement.setText("");
        holder.bornOn.setText("");
        holder.emojis.setText("");

        if (contact.isIgnore()) {
            holder.contactStatus.setText(ctx.getResources().getString(R.string.emoji_block));
        }
        if (contact.isFavorite()) {
            holder.contactStatus.setText(ctx.getResources().getString(R.string.emoji_heart));
        }

        holder.name.setText(name);

        holder.birthDayWeekName
                .setText(ctx.getResources()
                        .getString(R.string.next_week_name, eventTypeLabel, nextBirthdayWeekName));

        if (hideZoadiac) {
            holder.zodiacElement.setVisibility(View.INVISIBLE);
        } else {
            holder.zodiacElement.setText(
                    ctx.getResources().getString(R.string.zodiac,
                            zodiacSign, zodiacSignElement));
        }

        holder.bornOn.setText(
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

                holder.picture.setImageDrawable(dr);
            } catch (IOException e) {
                holder.picture.setImageDrawable(
                        ContextCompat.getDrawable(
                                ctx, R.drawable.ic_account_circle_black_24dp));
            }

        } else {
            holder.picture.setImageDrawable(
                    ContextCompat.getDrawable(
                            ctx, R.drawable.ic_account_circle_black_24dp));
        }

        /* Party */
        if (contact.getDaysUntilNextBirthday() >= 0 && contact.shallWePartyToday()) {
            if (contact.getDaysUntilNextBirthday() == 0) {
                holder.daysToGo.setText(
                        ctx.getResources().getString(R.string.party_message));
                holder.emojis.setText(ctx.getResources().getString(R.string.emoji_today_party));
            } else {
                holder.daysToGo
                        .setText(
                                ctx.getResources().getQuantityString(
                                        R.plurals.days_until,
                                        daysUntilNextBirthday,
                                        daysUntilNextBirthday,
                                        eventTypeLabel));
                holder.emojis.setText(ctx.getResources().getString(R.string.emoji_tomorrow_party));
            }


        } else if (contact.getDaysUntilNextBirthday() == 1) {
            holder.daysToGo
                    .setText(
                            ctx.getResources().getQuantityString(
                                    R.plurals.days_until,
                                    daysUntilNextBirthday,
                                    daysUntilNextBirthday,
                                    eventTypeLabel));
            holder.emojis.setText(ctx.getResources().getString(R.string.emoji_tomorrow_party));
        } else {
            /* Days Ago */
            if (daysUntilNextBirthday >= this.lateBDDListTreshold && !contact.isNotYetBorn()) {
                long daysAgo;

                if (this.isNowLeapYear) {
                    daysAgo = this.LEAP_YEAR_LEN - contact.getDaysUntilNextBirthday();
                } else {
                    daysAgo = this.YEAR_LEN - contact.getDaysUntilNextBirthday();
                }

                holder.birthDayWeekName
                        .setText(ctx.getResources()
                                .getString(R.string.prev_week_name, eventTypeLabel,
                                        contact.getPrevBirthDayWeekName()));

                holder.daysToGo
                        .setText(
                                ctx.getResources().getQuantityString(
                                        R.plurals.days_ago,
                                        (int) daysAgo,
                                        (int) daysAgo));
            } else {
                /* All the rest */
                holder.daysToGo
                        .setText(
                                ctx.getResources().getQuantityString(
                                        R.plurals.days_until,
                                        daysUntilNextBirthday,
                                        daysUntilNextBirthday,
                                        eventTypeLabel));
            }
        }

        if (contact.isHeSheNotEvenOneYearOld() && showCurrentAge) {
            holder.daysOld.setText(ctx.getResources().getQuantityString(
                    R.plurals.days_old, daysOld, daysOld));
            holder.daysOld.setVisibility(View.VISIBLE);
        }

        String ageText;

        if (showCurrentAge) {
            ageText = String.valueOf(age);
        } else {
            ageText = "↑" + age;
        }

        holder.ageBadge.setText(ageText);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                if (constraint.length() == 0) {
                    contacts = contactsOrignal;
                } else {
                    contacts = (ArrayList<Contact>) results.values;
                }
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();

                if (charSequence == null || charSequence.length() == 0) {
                    results.count = contactsOrignal.size();
                    results.values = contactsOrignal;
                } else {
                    String filterString = charSequence.toString().toLowerCase();
                    ArrayList<Contact> nlist = new ArrayList<>();

                    for (int idx = 0; idx < contactsOrignal.size(); ++idx) {
                        Contact contact = contactsOrignal.get(idx);
                        if (applyFilter(contact, filterString)) {
                            nlist.add(contact);
                        }
                    }

                    results.count = nlist.size();
                    results.values = nlist;
                }

                return results;
            }

            boolean applyFilter(Contact contact, String filter) {
                String name = contact.getName().toLowerCase();
                String monthName = contact.getBornOnMonthName().toLowerCase();
                String birthdayWeekName = contact.getNextBirthDayWeekName().toLowerCase();
                String zodiac = contact.getZodiac().toLowerCase();
                String zodiacElement = contact.getZodiacElement().toLowerCase();
                String age = Integer.toString(contact.getAge());
                String daysOld = Integer.toString(contact.getDaysOld());

                return name.contains(filter) ||
                        age.startsWith(filter) ||
                        daysOld.startsWith(filter) ||
                        monthName.contains(filter) ||
                        birthdayWeekName.contains(filter) ||
                        zodiac.contains(filter) ||
                        zodiacElement.contains(filter);
            }
        };
    }

        @NonNull
        @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);
        return new ContactViewHolder(itemView);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView birthDayWeekName;
        private final TextView daysOld;
        private final TextView ageBadge;
        private final TextView daysToGo;
        private final TextView zodiacElement;
        private final TextView bornOn;
        private final TextView contactStatus;
        private final TextView emojis;
        private final ImageView picture;
        public final RelativeLayout ignoreContactLayout;
        public final RelativeLayout favoriteContactLayout;
        public final RelativeLayout itemLayout;

        ContactViewHolder(View view) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Contact contact = contacts.get(pos);
                    Intent i;
                    i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(
                            ContactsContract.Contacts.CONTENT_LOOKUP_URI
                                    + "/" + contact.getKey()));
                    ctx.startActivity(i);
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    Contact contact = contacts.get(pos);
                    Intent i;
                    i = new Intent(Intent.ACTION_EDIT);
                    i.setData(Uri.parse(
                            ContactsContract.Contacts.CONTENT_LOOKUP_URI
                                    + "/" + contact.getKey()));
                    ctx.startActivity(i);
                    return true;
                }
            });
            name = view.findViewById(R.id.tvContactName);
            contactStatus = view.findViewById(R.id.tvStatus);
            birthDayWeekName = view.findViewById(R.id.tvContactNextBirthDayWeekName);
            daysOld = view.findViewById(R.id.tvDaysOld);
            ageBadge = view.findViewById(R.id.tvAgeBadge);
            daysToGo = view.findViewById(R.id.tvContactDaysUntil);
            zodiacElement = view.findViewById(R.id.tvZodiac);
            bornOn = view.findViewById(R.id.tvBirthDay);
            picture = view.findViewById(R.id.ivContactPicture);
            emojis = view.findViewById(R.id.tvEmojis);
            itemLayout = view.findViewById(R.id.view_item);
            ignoreContactLayout = view.findViewById(R.id.view_background_ignore);
            favoriteContactLayout = view.findViewById(R.id.view_background_favorite);
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void sort(int order, int sortType) {
        contacts.sort(new BirthDayComparator(order, sortType));
        this.sortType = sortType;
        this.sortOrder = order;
        notifyDataSetChanged();
    }

    public void ignoreItem(int position, boolean hide) {
        if (hide) {
            this.contacts.remove(position);
            notifyItemRemoved(position);
        } else {
            notifyItemChanged(position);
        }
    }

    public void favoriteItem(int position) {
        notifyItemChanged(position);
    }

    public Contact getContact(int position) {
        return this.contacts.get(position);
    }

    public void restoreContact(Contact contact) {
        this.contacts.add(contact);
        this.sort(this.sortOrder, this.sortType);
    }

}