package com.composum.pages.commons.taglib;

import com.composum.pages.commons.model.AbstractModel;
import com.composum.pages.commons.model.properties.ValueSet;

import java.lang.reflect.Array;

/**
 * a special model to handle properties and property values during editing in an i18n context
 */
public class PropertyEditHandle<T> extends AbstractModel {

    /**
     * the property values (possibly multiValue values) if determined by the widget (tag);
     * is initial 'null' if this model is using a 'property' name/path instead of the 'value'
     */
    private transient ValueSet<T> values;

    /**
     * the values determined by the model getter (with the locale cascade); in a non-default language context
     * this contains the values of the default language as possible placeholders for the input field(s)
     */
    private transient ValueSet<T> defaultValues;

    protected String propertyName;  // the property name without the i18n context
    protected String propertyPath;  // the path to the i18n variant of the property
    protected boolean i18n;

    protected final Class<T> type;
    protected transient Boolean multiValue;

    protected EditWidgetTag widget; // the widget itself which is using this as model is accessible here

    public PropertyEditHandle(Class<T> type) {
        this.type = type;
    }

    public void setWidget(EditWidgetTag tag) {
        widget = tag;
    }

    /**
     * declares the property of the models content resource for this handle
     */
    public void setProperty(String name, String path, boolean i18n) {
        propertyName = name;
        propertyPath = path;
        this.i18n = i18n;
    }

    /**
     * the values can be set by the context (an edit widget tag) as 'value' instead of a 'property' name
     */
    public void setValue(Object object) {
        values = object != null ? new ValueSet<>(object) : null;
    }

    /**
     * Determines the current values of the given path(name) - extension hook for complex data
     */
    @SuppressWarnings("unchecked")
    protected ValueSet<T> retrieveValue(String path) {
        T single;
        if (isMultiValue() || (single = getProperty(path, i18n ? getLocale() : null, type)) == null) {
            T[] multi = (T[]) getProperty(path, i18n ? getLocale() : null, Array.newInstance(type, 0).getClass());
            if (multiValue == null && multi != null) {
                multiValue = true;
            }
            return new ValueSet(multi);
        } else {
            if (multiValue == null) {
                multiValue = false;
            }
            return new ValueSet(single);
        }
    }

    /**
     * @return 'true' if the model has a value for the referenced property
     */
    public boolean isValueSet() {
        T value = getValue();
        return value != null;
    }

    public boolean isMultiValue() {
        return multiValue != null ? multiValue : false;
    }

    public void setMultiValue(boolean isMultiValue) {
        multiValue = isMultiValue;
    }

    /**
     * usr the next value of a multi value property as the current value (during iteration in a mutli value tag)
     */
    public void nextValue() {
        ValueSet set;
        if ((set = getValues()) != null) {
            set.next();
        }
        if ((set = getDefaultValues()) != null) {
            set.next();
        }
    }

    public T getValue() {
        ValueSet<T> values = getValues();
        return values != null ? values.getValue() : null;
    }

    /**
     * returns the current values; retrieves it if necessary
     */
    public ValueSet<T> getValues() {
        if (values == null) {
            values = retrieveValue(propertyPath);
        }
        return values;
    }

    public T getDefaultValue() {
        ValueSet<T> defaultValues = getDefaultValues();
        return defaultValues != null ? defaultValues.getValue() : null;
    }

    /**
     * returns the current values by property name as default; retrieves it if necessary
     */
    public ValueSet<T> getDefaultValues() {
        if (defaultValues == null) {
            defaultValues = propertyName == null || propertyName.equals(propertyPath) ? null : retrieveValue(propertyName);
        }
        return defaultValues;
    }
}
