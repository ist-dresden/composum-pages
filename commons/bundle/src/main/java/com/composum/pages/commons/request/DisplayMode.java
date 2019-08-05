package com.composum.pages.commons.request;

import com.composum.sling.core.BeanContext;

import java.util.Stack;

/**
 * the display mode stack is mainly controlled by the various JSP tags with a 'mode' attribute set
 */
public class DisplayMode extends Stack<DisplayMode.Value> {

    public enum Value {

        NONE, PREVIEW, BROWSE, EDIT, DEVELOP, REQUEST;

        public static DisplayMode.Value displayModeValue(Object value, Value defaultValue) {
            Value mode = null;
            if (value != null) {
                try {
                    mode = Enum.valueOf(DisplayMode.Value.class, value.toString().trim().toUpperCase());
                } catch (IllegalArgumentException iaex) {
                    // ok, null...
                }
            }
            return mode != null ? mode : defaultValue;
        }
    }

    /**
     * display mode string values
     */
    public static final String DISPLAY_MODE_NONE = Value.NONE.name();
    public static final String DISPLAY_MODE_PREVIEW = Value.PREVIEW.name();
    public static final String DISPLAY_MODE_BROWSE = Value.BROWSE.name();
    public static final String DISPLAY_MODE_EDIT = Value.EDIT.name();
    public static final String DISPLAY_MODE_DEVELOP = Value.DEVELOP.name();

    // static accessors

    public static boolean isEditMode(BeanContext context) {
        return isEditMode(current(context));
    }

    public static boolean isEditMode(Value value) {
        return value == Value.EDIT || value == Value.DEVELOP;
    }

    public static boolean isPreviewMode(BeanContext context) {
        return isPreviewMode(current(context));
    }

    public static boolean isPreviewMode(Value value) {
        return value == Value.PREVIEW || value == Value.BROWSE;
    }

    public static boolean isLiveMode(BeanContext context) {
        return isLiveMode(current(context));
    }

    public static boolean isLiveMode(Value value) {
        return value == Value.NONE;
    }

    public static boolean isDevelopMode(BeanContext context) {
        return isDevelopMode(current(context));
    }

    public static boolean isDevelopMode(Value value) {
        return value == Value.DEVELOP;
    }

    /**
     * returns the current (topmost) value of the stack instance in the given context
     */
    public static Value current(BeanContext context) {
        Value value = get(context).peek();
        return value == Value.REQUEST ? requested(context) : value;
    }

    /**
     * returns the initial value of the stack instance in the given context
     */
    public static Value requested(BeanContext context) {
        return get(context).firstElement();
    }

    /**
     * adapts the current mode from the request of the context
     */
    public static DisplayMode get(BeanContext context) {
        return context.getRequest().adaptTo(DisplayMode.class);
    }

    // stack instance

    /**
     * package access - use request.adaptTo() to get an instance
     */
    DisplayMode(Value requestedValue) {
        reset(requestedValue);
    }

    public void reset(Value requestedValue) {
        clear();
        push(requestedValue);
    }

    /**
     * at least one value must always be in the stack
     */
    @Override
    public synchronized Value pop() {
        return size() > 1 ? super.pop() : peek();
    }
}
