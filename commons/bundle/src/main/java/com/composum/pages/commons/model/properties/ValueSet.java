package com.composum.pages.commons.model.properties;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A value model which can encapsulate the values of a multi value property with an embedded iterator and
 * a general 'getValue()' access to the current value of the iterator or to the single value. This is used
 * in the widgets embedded in a multi widget tag to access the values in transparent way without knowledge
 * about the multi value context.
 *
 * @param <T> the value type
 * @see com.composum.pages.commons.taglib.PropertyEditHandle
 */
public class ValueSet<T> extends ArrayList<T> implements Iterator<T> {

    private transient T currentValue;
    private transient Iterator<T> iterator;
    private int index = -1;

    /**
     * prevent from instantiation without a value (protected)
     */
    protected ValueSet() {
    }

    /**
     * let an instance of this type handle any object (single or multi value)
     *
     * @param object the value or the set of values
     */
    public ValueSet(Object object, Class<T> type) {
        if (object instanceof Object[]) {
            addAll((Object[]) object, type);
        } else if (object instanceof Collection) {
            addAll((Collection<?>) object, type);
        } else if (object != null) {
            add(object, type);
        }
    }

    protected boolean addAll(Object[] objects, Class<T> type) {
        List<T> values = new ArrayList<>();
        for (Object object : objects) {
            values.add(valueOf(object, type));
        }
        return super.addAll(values);
    }

    protected boolean addAll(Collection<?> objects, Class<T> type) {
        List<T> values = new ArrayList<>();
        for (Object object : objects) {
            values.add(valueOf(object, type));
        }
        return super.addAll(values);
    }

    protected boolean add(Object object, Class<T> type) {
        return super.add(valueOf(object, type));
    }

    /**
     * @return the single value or the current value of the embedded iterator
     */
    public T getValue() {
        if (currentValue == null) {
            next();
        }
        return currentValue;
    }

    /**
     * @return the current index in case of a multi value; -1 for a single value
     */
    public int getIndex() {
        return index;
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

    protected Iterator<T> getIterator() {
        if (iterator == null) {
            iterator = iterator();
        }
        return iterator;
    }

    /**
     * @return object value converted to the sets type
     */
    @SuppressWarnings("unchecked")
    public T valueOf(Object object, Class<T> type) {
        T value = null;
        if (object != null) {
            if (type.isInstance(object)) {
                value = (T) object;
            } else {
                Constructor<T> constructor;
                try {
                    constructor = type.getConstructor(object.getClass());
                    value = constructor.newInstance(object);
                } catch (Exception ex) {
                    try {
                        constructor = type.getConstructor(String.class);
                        value = constructor.newInstance(object.toString());
                    } catch (Exception exx) {
                        throw new RuntimeException("can't convert '" + object + "' to '" + type.getName() + "'");
                    }
                }
            }
        }
        return value;
    }
}
