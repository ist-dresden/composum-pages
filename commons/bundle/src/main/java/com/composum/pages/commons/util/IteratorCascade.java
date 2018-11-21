/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class IteratorCascade<T> implements Iterator<T> {

    protected final List<Iterator<T>> cascade;
    protected final Iterator<Iterator<T>> current;

    private Iterator<T> iterator;

    @SafeVarargs
    public IteratorCascade(Iterator<T>... iterators) {
        cascade = new ArrayList<>();
        Collections.addAll(cascade, iterators);
        current = cascade.iterator();
        iterator = current.hasNext() ? current.next() : Collections.<T>emptyIterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext() || (current.hasNext() && (iterator = current.next()).hasNext());
    }

    @Override
    public T next() {
        return hasNext() ? iterator.next() : null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() not supported in IteratorCascade");
    }
}
