package com.composum.pages.commons.model.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class ValueSet<T> extends ArrayList<T> implements Iterator<T> {

    private transient T currentValue;
    private transient Iterator<T> iterator;
    private int index = -1;

    protected ValueSet() {
    }

    @SuppressWarnings("unchecked")
    public ValueSet(Object object) {
        if (object instanceof Object[]) {
            addAll(Arrays.asList((T[]) object));
        } else if (object instanceof Collection) {
            addAll((Collection<T>) object);
        } else if (object != null) {
            add((T) object);
        }
    }

    public T getValue() {
        if (currentValue == null) {
            next();
        }
        return currentValue;
    }

    public int getIndex() {
        return index;
    }

    protected Iterator<T> getIterator() {
        if (iterator == null) {
            iterator = iterator();
        }
        return iterator;
    }

    @Override
    public boolean hasNext() {
        Iterator<T> iterator = getIterator();
        return iterator != null && iterator.hasNext();
    }

    @Override
    public T next() {
        Iterator<T> iterator = getIterator();
        if (iterator != null) {
            if (iterator.hasNext()) {
                currentValue = iterator.next();
                index++;
            } else {
                currentValue = null;
                index = -1;
            }
        }
        return currentValue;
    }

    @Override
    public void remove() {
    }
}
