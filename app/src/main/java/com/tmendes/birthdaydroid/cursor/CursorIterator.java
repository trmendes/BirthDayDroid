package com.tmendes.birthdaydroid.cursor;

import android.database.Cursor;

import java.util.NoSuchElementException;

/**
 * Use CursorIterator as the following example:
 * <pre>
 * try (CursorIterator<Object> cursorIterator = ...) {
 *     while (cursorIterator.hasNext()) {
 *         ...
 *     }
 * }
 * </pre>
 *
 * Important is, that the iterator is initialized in the try resource part.
 * This force that the inner used cursor will be closed in all cases.
 *
 * @param <T>
 */
public abstract class CursorIterator<T> implements CloseableIterator<T> {
    private final Cursor cursor;
    private boolean hasNext = true;

    protected CursorIterator(Cursor cursor) {
        if(cursor == null) {
            throw new IllegalArgumentException("Cursor can not be null");
        }

        if(cursor.isClosed()) {
            throw new IllegalArgumentException("Cursor must be open");
        }

        this.cursor = cursor;

        if(!this.cursor.moveToFirst()){
            this.hasNext = false;
            this.close();
        }
    }

    @Override
    public boolean hasNext() {
        return this.hasNext;
    }

    @Override
    public T next() {
        if(!hasNext){
            throw new NoSuchElementException();
        }

        final T t = convertCursor(this.cursor);
        if(!this.cursor.moveToNext()) {
            this.hasNext = false;
            this.close();
        }
        return t;
    }

    public abstract T convertCursor(Cursor cursor);

    @Override
    public void close() {
        if(!this.cursor.isClosed()) {
            this.cursor.close();
        }
    }
}
