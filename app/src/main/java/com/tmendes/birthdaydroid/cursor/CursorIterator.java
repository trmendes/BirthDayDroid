package com.tmendes.birthdaydroid.cursor;

import android.database.Cursor;

import java.util.Iterator;

public abstract class CursorIterator<T> implements Iterator<T> {
    private final Cursor cursor;

    protected CursorIterator(Cursor cursor) {
        if(cursor == null) {
            throw new IllegalArgumentException("Cursor can not be null");
        }

        if(cursor.isClosed()) {
            throw new IllegalArgumentException("Cursor must be open");
        }

        this.cursor = cursor;

        if(!cursor.moveToFirst()){
            cursor.close();
            throw new IllegalArgumentException("Cursor can not move to first element");
        }
    }

    @Override
    public boolean hasNext() {
        boolean last = cursor.isLast();
        if(last) {
            closeCursor();
        }
        return !last;
    }

    private void closeCursor() {
        if(!cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    public T next() {
        cursor.moveToNext();
        return convertCursor(cursor);
    }

    public abstract T convertCursor(Cursor cursor);
}
