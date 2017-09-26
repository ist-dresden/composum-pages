package com.composum.pages.commons.taglib;

import javax.servlet.jsp.jstl.core.LoopTagStatus;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 */
public abstract class EditMultiLoopStatus<T> implements LoopTagStatus, Iterator<T> {

    protected final Collection<T> set;
    private transient Iterator<T> iterator;
    private transient T current;
    private transient int index;

    public EditMultiLoopStatus(Collection<T> set) {
        this.set = set;
        iterator = set.iterator();
        current = null;
        index = -1;
    }

    protected abstract T createEmptyPlaceholder();

    protected abstract void exposeCurrent(T current);

    public void exposeVariables() {
        exposeCurrent(iterator != null ? current : null);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        if (hasNext()) {
            current = iterator.next();
            index++;
        } else {
            if (index < 0) {
                // if set is empty or non existing use a dummy resource for the first item
                current = createEmptyPlaceholder();
            } else {
                current = null;
            }
            index = -1;
        }
        return current;
    }

    @Override
    public void remove() {
    }

    @Override
    public Object getCurrent() {
        return current;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int getCount() {
        return set.size();
    }

    @Override
    public boolean isFirst() {
        return index == 0;
    }

    @Override
    public boolean isLast() {
        return index == getCount() - 1;
    }

    @Override
    public Integer getBegin() {
        return null;
    }

    @Override
    public Integer getEnd() {
        return null;
    }

    @Override
    public Integer getStep() {
        return null;
    }
}
