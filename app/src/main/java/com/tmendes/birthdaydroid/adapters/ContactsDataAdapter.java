package com.tmendes.birthdaydroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.tmendes.birthdaydroid.Contact;
import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.comparators.BirthDayComparator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ContactsDataAdapter extends RecyclerView.Adapter<ContactsDataAdapter.ContactViewHolder>
        implements Filterable {
    private List<Contact> contacts;
    private final List<Contact> readyOnlyOriginalContacts;

    private final Context ctx;

    private int sortOrder;
    private int sortType;

    private final boolean hideZoadiac;
    private final boolean showCurrentAge;

    public ContactsDataAdapter(Context ctx, List<Contact> contacts) {
        this.contacts = contacts;
        this.readyOnlyOriginalContacts = contacts;
        this.ctx = ctx;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        this.hideZoadiac = prefs.getBoolean("hide_zodiac", false);
        this.showCurrentAge = prefs.getBoolean("show_current_age", false);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);

        RoundedBitmapDrawable picture = null;

        int age = contact.getAge();
        int daysOld = contact.getDaysOld();

        int daysUntilNextBirthday = contact.getDaysUntilNextBirthday();

        String status = "";
        String ageText;
        String partyMsg;
        String birthdayMsg;

        String photoUri = contact.getPhotoURI();
        String name = contact.getName();
        String zodiacSign = contact.getZodiacSymbol();
        String zodiacSignElement = contact.getZodiacElementSymbol();
        String eventTypeLabel = contact.getEventTypeLabel();

        holder.ignoreContactLayout.setVisibility(View.INVISIBLE);
        holder.favoriteContactLayout.setVisibility(View.INVISIBLE);

        holder.name.setText("");
        holder.contactStatus.setText("");
        holder.ageBadge.setText("");

        holder.lineOne.setText("");
        holder.lineTwo.setText("");
        holder.lineThree.setText("");
        holder.lineFour.setText("");

        /* Contact Picture */
        if (photoUri != null) {
            try {
                Bitmap src = MediaStore
                        .Images
                        .Media
                        .getBitmap(ctx.getContentResolver(), Uri.parse(photoUri));

                picture = RoundedBitmapDrawableFactory.create(ctx.getResources(), src);
                picture.setCornerRadius(Math.max(src.getWidth(), src.getHeight()) / 2.0f);
            } catch (IOException _ignored) {
                /* Do nothing */
            }
        }

        /* Age badge */
        if (showCurrentAge && !contact.shallWePartyToday()) {
            --age;
        }
        if (!showCurrentAge && !contact.shallWePartyToday()) {
            ageText = "↑" + age;
        } else {
            ageText = String.valueOf(age);
        }

        birthdayMsg = contact.getNextBirthDayInfo();

        /* Party */
        int MAX_DAYS_AGO = -7;
        if (contact.shallWePartyToday()) {
            partyMsg = ctx.getResources().getString(R.string.party_message);
            status = status + " " + ctx.getResources()
                    .getString(R.string.emoji_today_party);

        } else if (daysUntilNextBirthday < 0 && daysUntilNextBirthday >= MAX_DAYS_AGO) {
            daysUntilNextBirthday = Math.abs(daysUntilNextBirthday);
            partyMsg = ctx.getResources().getQuantityString(R.plurals.days_ago,
                    daysUntilNextBirthday,
                    daysUntilNextBirthday);
        } else {
            if (daysUntilNextBirthday < 0) {
                daysUntilNextBirthday = daysUntilNextBirthday + Calendar.getInstance()
                        .getMaximum(Calendar.DAY_OF_YEAR);
            }
            partyMsg = ctx.getResources().getQuantityString(R.plurals.days_to_go,
                    daysUntilNextBirthday,
                    daysUntilNextBirthday);
        }

        /* Capitalize it */
        if(!contact.isCustomTypeLabel()) {
            eventTypeLabel = eventTypeLabel.toLowerCase();
            eventTypeLabel = Character.toString(eventTypeLabel.charAt(0)).toUpperCase()
                    + eventTypeLabel.substring(1);
        }

        if (picture == null) {
            holder.picture.setImageDrawable(
                    ContextCompat.getDrawable(
                            ctx, R.drawable.ic_account_circle_black_48dp));
        } else {
            holder.picture.setImageDrawable(picture);
        }

        holder.name.setText(name);

        holder.ageBadge.setText(ageText);

        holder.lineOne.setText(birthdayMsg);
        holder.lineTwo.setText(eventTypeLabel);
        holder.lineThree.setText(partyMsg);

        if(contact.shallWePartyToday()) {
            holder.lineFour.setText(ctx.getResources().getQuantityString(
                    R.plurals.years_old, age, age));
        } else {
            if (age == 1) {
                holder.lineFour.setText(ctx.getResources().getQuantityString(
                        R.plurals.days_old, daysOld, daysOld));
            } else if(age > 1) {
                holder.lineFour.setText(ctx.getResources().getQuantityString(
                        R.plurals.years_old, age, age));
            }
        }


        /* Zodiac Icons */
        if (!hideZoadiac) {
            status = status + " " + zodiacSign + " " + zodiacSignElement;
        }

        /* Favorite/Ignore Icons */
        if (contact.isIgnore()) {
            status = status + " " + ctx.getResources().getString(R.string.emoji_block);
        }
        if (contact.isFavorite()) {
            status = status + " " + ctx.getResources().getString(R.string.emoji_heart);
        }

        holder.contactStatus.setText(status);

        if (contact.isMissingYearInfo()) {
            holder.ageBadge.setVisibility(View.INVISIBLE);
            holder.lineFour.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                if (constraint.length() == 0) {
                    contacts = readyOnlyOriginalContacts;
                } else contacts = (ArrayList<Contact>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence query) {
                FilterResults filterResults = new FilterResults();
                if (query == null || query.length() == 0) {
                    filterResults.count = contacts.size();
                    filterResults.values = contacts;
                } else {
                    String filterString = query.toString().toLowerCase();
                    ArrayList<Contact> filteredContacts = new ArrayList<>();

                    for (Contact contact : readyOnlyOriginalContacts) {
                        if (applyFilter(contact, filterString)) {
                            filteredContacts.add(contact);
                        }
                    }
                    filterResults.count = filteredContacts.size();
                    filterResults.values = filteredContacts;
                }
                return filterResults;
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
        private final TextView lineTwo;
        private final TextView lineFour;
        private final TextView ageBadge;
        private final TextView lineThree;
        private final TextView lineOne;
        private final TextView contactStatus;
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
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void sort(int order, int sortType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            contacts.sort(new BirthDayComparator(order, sortType));
        } else {
            Collections.sort(contacts, new BirthDayComparator(order, sortType));
        }
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
