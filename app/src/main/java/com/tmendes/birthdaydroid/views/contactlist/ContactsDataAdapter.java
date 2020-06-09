package com.tmendes.birthdaydroid.views.contactlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.comparators.BirthDayComparatorFactory;
import com.tmendes.birthdaydroid.contact.Contact;
import com.tmendes.birthdaydroid.date.DateLocaleHelper;
import com.tmendes.birthdaydroid.zodiac.ZodiacResourceHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ContactsDataAdapter extends RecyclerView.Adapter<ContactViewHolder>
        implements Filterable {
    private List<Contact> contacts;
    private final List<Contact> readyOnlyOriginalContacts;
    private final ZodiacResourceHelper zodiacResourceHelper;
    private final DateLocaleHelper dateLocaleHelper;

    private final Context ctx;

    private int sortOrder;
    private int sortType;

    private final boolean hideZodiac;
    private final boolean showCurrentAge;

    public ContactsDataAdapter(Context ctx) {
        this.contacts = new ArrayList<>();
        this.readyOnlyOriginalContacts = this.contacts;
        this.ctx = ctx;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        this.hideZodiac = prefs.getBoolean("hide_zodiac", false);
        this.showCurrentAge = prefs.getBoolean("show_current_age", false);
        this.zodiacResourceHelper = new ZodiacResourceHelper(this.ctx);
        this.dateLocaleHelper = new DateLocaleHelper();
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.setupContact(contact, showCurrentAge, hideZodiac);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                if (constraint.length() == 0) {
                    contacts = readyOnlyOriginalContacts;
                } else {
                    contacts = (List<Contact>) results.values;
                }
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
                String monthName = dateLocaleHelper.getMonthString(contact.getBornOn().getMonth(), ctx).toLowerCase();
                String birthdayWeekName = dateLocaleHelper.getDayOfWeek(contact.getNextBirthday().getDayOfWeek(), ctx).toLowerCase();
                String zodiacName = zodiacResourceHelper.getZodiacName(contact.getZodiac()).toLowerCase();
                String zodiacElement = zodiacResourceHelper.getZodiacElementName(contact.getZodiac()).toLowerCase();
                String age = Integer.toString(contact.getAgeInYears());
                String daysOld = Integer.toString(contact.getAgeInDays());

                return name.contains(filter) ||
                        age.startsWith(filter) ||
                        daysOld.startsWith(filter) ||
                        monthName.contains(filter) ||
                        birthdayWeekName.contains(filter) ||
                        zodiacName.contains(filter) ||
                        zodiacElement.contains(filter);
            }
        };
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);
        return new ContactViewHolder(itemView, zodiacResourceHelper);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void refreshContacts(List<Contact> contacts) {
        this.contacts = contacts;
        this.sort(this.sortOrder, this.sortType);
    }

    public void sort(int sortOrder, int sortType) {
        Comparator<Contact> comparator = new BirthDayComparatorFactory(ctx)
                .createBirthdayComparator(sortOrder, sortType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            contacts.sort(comparator);
        } else {
            Collections.sort(contacts, comparator);
        }
        this.sortType = sortType;
        this.sortOrder = sortOrder;
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

    public void restoreIgnoredContact(int position, boolean hide, Contact contact) {
        if (hide) {
            this.contacts.add(position, contact);
            notifyItemInserted(position);
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
}
