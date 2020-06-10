package com.tmendes.birthdaydroid.views.contactlist;

import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class SortAndFilterRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private List<T> allItems = new ArrayList<>();
    private List<T> filteredItems = new ArrayList<>();

    private Comparator<T> comparator;
    private Filter<T> filter;
    private CharSequence filterTerm;

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    public void setData(List<T> data) {
        if(this.allItems.equals(data)) {
            return;
        }

        if (data == null) {
            data = new ArrayList<>();
        }

        this.sort(data, this.comparator);
        this.allItems = data;
        this.filteredItems = this.filter(this.allItems, this.filter, this.filterTerm);
        this.notifyDataSetChanged();
    }

    public void setComparator(Comparator<T> comparator) {
        if(Objects.equals(this.comparator, comparator)){
            return;
        }
        this.comparator = comparator;

        this.sort(this.allItems, this.comparator);
        this.sort(this.filteredItems, this.comparator);
        this.notifyDataSetChanged();
    }

    public void setFilter(Filter<T> filter) {
        if(Objects.equals(this.filter, filter)) {
            return;
        }
        this.filter = filter;

        this.filteredItems = this.filter(this.allItems, this.filter, this.filterTerm);
        this.notifyDataSetChanged();
    }

    public void setFilterTerm(CharSequence filterTerm) {
        if(Objects.equals(this.filterTerm, filterTerm)){
            return;
        }
        this.filterTerm = filterTerm;

        this.filteredItems = this.filter(this.allItems, this.filter, this.filterTerm);
        this.notifyDataSetChanged();
    }


    private List<T> filter(List<T> list, Filter<T> filter, CharSequence filterTerm) {
        if (filter == null || filterTerm == null || filterTerm.length() == 0 || list.isEmpty()) {
            return new ArrayList<>(list);
        } else {
            return list.stream()
                    .filter(o -> filter.filter(o, filterTerm))
                    .collect(Collectors.toList());
        }
    }

    private void sort(List<T> list, Comparator<T> comparator) {
        if (comparator != null && !list.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                list.sort(comparator);
            } else {
                Collections.sort(list, comparator);
            }
        }
    }

    public void removeItem(int index) {
        final T item = getItem(index);
        filteredItems.remove(item);
        allItems.remove(item);
        notifyItemRemoved(index);
    }

    public void addItem(T item) {
        final int allDateInsertIndex = getInsertIndex(filteredItems, comparator, item);
        final int filteredItemsInsertIndex = getInsertIndex(filteredItems, comparator, item);

        this.allItems.add(allDateInsertIndex, item);
        this.filteredItems.add(filteredItemsInsertIndex, item);
        notifyItemInserted(filteredItemsInsertIndex);
    }

    private int getInsertIndex(List<T> list, Comparator<T> comparator, T item) {
        for (int i = 0; i < list.size(); i++) {
            if (comparator.compare(list.get(i), item) > 0) {
                return i;
            }
        }
        return list.size();
    }

    public T getItem(int index) {
        return filteredItems.get(index);
    }

    public interface Filter<T> {
        boolean filter(T data, CharSequence filterTerm);
    }
}
