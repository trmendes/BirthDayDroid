package com.tmendes.birthdaydroid.views.contactlist;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ContactViewHolderTouchHelper extends ItemTouchHelper.SimpleCallback {
    private final SwipeListener listener;

    public ContactViewHolderTouchHelper(SwipeListener listener) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.listener = listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            getDefaultUIUtil().onSelected(((AbstractContactViewHolder) viewHolder).getItemLayout());
        }
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        getDefaultUIUtil().onDrawOver(c, recyclerView, ((AbstractContactViewHolder) viewHolder).getItemLayout(), dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        getDefaultUIUtil().clearView(((AbstractContactViewHolder) viewHolder).getItemLayout());
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        final AbstractContactViewHolder abstractContactViewHolder = (AbstractContactViewHolder) viewHolder;

        if (dX > 0) {
            abstractContactViewHolder.setSwipeFavoriteLayout();
        } else if (dX < 0) {
            abstractContactViewHolder.setSwipeIgnoreLayout();
        } else {
            abstractContactViewHolder.setItemLayout();
        }

        getDefaultUIUtil().onDraw(c, recyclerView, abstractContactViewHolder.getItemLayout(), dX, dY, actionState,
                isCurrentlyActive);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.LEFT) {
            listener.onSwipeIgnore(viewHolder.getAdapterPosition());
        } else if (direction == ItemTouchHelper.RIGHT) {
            listener.onSwipeFavorite(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public interface SwipeListener {
        void onSwipeIgnore(int position);
        void onSwipeFavorite(int position);
    }
}