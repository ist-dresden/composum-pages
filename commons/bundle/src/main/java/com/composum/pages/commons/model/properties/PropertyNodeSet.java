package com.composum.pages.commons.model.properties;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * a set of property nodes as a model for the multi value form widget
 *
 * @param <T> the model type of the elements
 */
public class PropertyNodeSet<T extends PropertyNode> extends PropertyNode implements Collection<T> {

    public static final Pattern ITEM_NAME_PATTERN = Pattern.compile("^([a-zA-Z_]+).*$");

    protected String itemName;
    protected LinkedHashMap<String, T> propertySet;

    public PropertyNodeSet() {
    }

    public PropertyNodeSet(final BeanContext context, final Resource resource) {
        super(context, resource);
    }

    @Override
    protected void initialize() {
        super.initialize();
        propertySet = new LinkedHashMap<>();
        for (Resource child : resource.getChildren()) {
            String name = child.getName();
            if (itemName == null) {
                Matcher matcher = ITEM_NAME_PATTERN.matcher(name);
                itemName = matcher.matches() ? matcher.group(1) : "";
            }
            add(child);
        }
    }

    @SuppressWarnings("unchecked")
    protected T createProperty(final Resource resource) {
        return (T) new GenericNode(context, resource);
    }

    protected T add(final Resource resource) {
        T property = createProperty(resource);
        add(property);
        return property;
    }

    public String getItemName() {
        return itemName;
    }

    @Nonnull
    @Override
    public Iterator<T> iterator() {
        return propertySet.values().iterator();
    }

    @Nonnull
    @Override
    public Object[] toArray() {
        return propertySet.values().toArray();
    }

    @Nonnull
    @Override
    public <TT> TT[] toArray(@Nonnull TT[] array) {
        //noinspection SuspiciousToArrayCall
        return propertySet.values().toArray(array);
    }

    @Override
    public boolean add(T property) {
        if (property != null) {
            propertySet.put(property.getName(), property);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object object) {
        if (object != null) {
            for (Map.Entry<String, T> entry : propertySet.entrySet()) {
                if (object.equals(entry.getValue())) {
                    propertySet.remove(entry.getKey());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> collection) {
        return propertySet.values().containsAll(collection);
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends T> collection) {
        for (T item : collection) {
            add(item);
        }
        return true;
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> collection) {
        boolean result = false;
        for (Map.Entry<String, T> entry : propertySet.entrySet()) {
            if (collection.contains(entry.getValue())) {
                propertySet.remove(entry.getKey());
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> collection) {
        boolean result = false;
        for (Map.Entry<String, T> entry : propertySet.entrySet()) {
            if (!collection.contains(entry.getValue())) {
                propertySet.remove(entry.getKey());
                result = true;
            }
        }
        return result;
    }

    @Override
    public void clear() {
        propertySet.clear();
    }

    @Override
    public int size() {
        return propertySet.size();
    }

    @Override
    public boolean isEmpty() {
        return propertySet.isEmpty();
    }

    @Override
    public boolean contains(Object object) {
        //noinspection SuspiciousMethodCalls
        return propertySet.containsValue(object);
    }
}
