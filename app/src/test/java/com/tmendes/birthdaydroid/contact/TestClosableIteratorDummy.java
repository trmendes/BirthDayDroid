package com.tmendes.birthdaydroid.contact;

import com.tmendes.birthdaydroid.cursor.CloseableIterator;

import java.util.Iterator;

public class TestClosableIteratorDummy<T> implements CloseableIterator<T> {
    private final Iterator<T> iterator;
    public TestClosableIteratorDummy(Iterable<T> iterable) {
        iterator = iterable.iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        return iterator.next();
    }

    @Override
    public void close() {
    }
}
