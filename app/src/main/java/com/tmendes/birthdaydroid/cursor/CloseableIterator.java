package com.tmendes.birthdaydroid.cursor;

import java.util.Iterator;

/**
 * Use CursorIterator as the following example:
 * <pre>
 * try (CloseableIterator<Object> closeableIterator = ...) {
 *     while (closeableIterator.hasNext()) {
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
public interface CloseableIterator<T> extends Iterator<T>, AutoCloseable {
}
