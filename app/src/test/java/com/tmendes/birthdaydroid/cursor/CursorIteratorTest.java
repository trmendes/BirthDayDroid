package com.tmendes.birthdaydroid.cursor;

import android.database.Cursor;

import org.junit.Assert;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CursorIteratorTest {
    @Test
    public void testCursorNull() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> new CursorIterator<Cursor>(null){
                    @Override
                    public Cursor convertCursor(Cursor cursor) {
                        return cursor;
                    }
                });
    }

    @Test
    public void testClosedCursor() {
        final Cursor cursor = mock(Cursor.class);
        doReturn(true).when(cursor).isClosed();

        Assert.assertThrows(IllegalArgumentException.class,
                () -> new CursorIterator<Cursor>(cursor){
                    @Override
                    public Cursor convertCursor(Cursor cursor) {
                        return cursor;
                    }
                });
    }

    @Test
    public void testEmptyCursor() {
        final CursorIteratorTestImplementation cursorIterator = createCursorIteratorTestImplementation(0);

        assertThat(cursorIterator.hasNext(), is(false));
        Assert.assertThrows(NoSuchElementException.class, cursorIterator::next);
        verify(cursorIterator.getCursor(), times(1)).close();
    }

    @Test
    public void testCursorWith1Element() {
        final CursorIteratorTestImplementation cursorIterator = createCursorIteratorTestImplementation(1);

        assertThat(cursorIterator.hasNext(), is(true));
        assertThat(cursorIterator.next(), is(0));

        assertThat(cursorIterator.hasNext(), is(false));
        assertThat(cursorIterator.hasNext(), is(false));
        assertThat(cursorIterator.hasNext(), is(false));
        Assert.assertThrows(NoSuchElementException.class, cursorIterator::next);
        Assert.assertThrows(NoSuchElementException.class, cursorIterator::next);
        Assert.assertThrows(NoSuchElementException.class, cursorIterator::next);

        verify(cursorIterator.getCursor(), times(1)).close();
    }

    @Test
    public void testCursorWith3Element() {
        final CursorIteratorTestImplementation cursorIterator = createCursorIteratorTestImplementation(3);

        assertThat(cursorIterator.hasNext(), is(true));
        assertThat(cursorIterator.hasNext(), is(true));
        assertThat(cursorIterator.hasNext(), is(true));
        assertThat(cursorIterator.next(), is(0));

        assertThat(cursorIterator.hasNext(), is(true));
        assertThat(cursorIterator.next(), is(1));

        assertThat(cursorIterator.hasNext(), is(true));
        assertThat(cursorIterator.next(), is(2));

        assertThat(cursorIterator.hasNext(), is(false));
        Assert.assertThrows(NoSuchElementException.class, cursorIterator::next);

        verify(cursorIterator.getCursor(), times(1)).close();
    }





    //////////// Test Implementation /////////////////
    private static CursorIteratorTestImplementation createCursorIteratorTestImplementation(int elementCount) {
        final Cursor cursor = mock(Cursor.class);

        doReturn(false).when(cursor).isClosed();
        doReturn(elementCount > 0).when(cursor).moveToFirst();

        return new CursorIteratorTestImplementation(cursor, elementCount);
    }

    private static class CursorIteratorTestImplementation extends CursorIterator<Integer> {
        private int elementCount;
        private int i = 0;
        private Cursor cursor;

        private CursorIteratorTestImplementation(Cursor cursor, int elementCount) {
            super(cursor);
            this.cursor = cursor;
            this.elementCount = elementCount;
        }

        public Cursor getCursor() {
            return cursor;
        }

        @Override
        public Integer convertCursor(Cursor cursor) {
            final int testObject = i;
            i++;
            doReturn(i < elementCount).when(cursor).moveToNext();
            return testObject;
        }
    }
}
