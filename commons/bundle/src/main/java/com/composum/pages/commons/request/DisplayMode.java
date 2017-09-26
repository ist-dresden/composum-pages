package com.composum.pages.commons.request;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Stack;

import static com.composum.pages.commons.PagesConstants.PAGES_PREFIX;

public class DisplayMode extends RequestAspect<Stack<DisplayMode.Value>> {

    public enum Value {NONE, PREVIEW, BROWSE, EDIT, DEVELOP}

    public static final String PARAMETER_NAME = "pages.mode";
    public static final String ATTRIBUTE_KEY = PAGES_PREFIX + "display-mode";

    public static DisplayMode instance = new DisplayMode();

    /**
     * returns the current (topmost) value of the stack instance in the given context
     */
    public static Value current(BeanContext context) {
        return instance.getAspect(context).peek();
    }

    /**
     * returns the initial value of the stack instance in the given context
     */
    public static Value requested(BeanContext context) {
        return instance.getAspect(context).firstElement();
    }

    /**
     * returns the display mode stack instance determined using the given context (request)
     */
    public static Stack<DisplayMode.Value> get(BeanContext context) {
        return instance.getAspect(context);
    }

    /**
     * creates a display mode stack instance initialized with the given value
     */
    public static Stack<DisplayMode.Value> create(DisplayMode.Value value) {
        return instance.createInstance(value.name());
    }

    // request aspect

    protected DisplayMode() {
    }

    @Override
    protected String getValue(Stack<Value> instance) {
        return instance.peek().name();
    }

    @Override
    protected String getParameterName() {
        return PARAMETER_NAME;
    }

    @Override
    protected String getAttributeKey() {
        return ATTRIBUTE_KEY;
    }

    @Override
    protected Stack<Value> createInstance(String value) {
        Stack<Value> instance = new Stack<>();
        instance.push(Value.valueOf(value.toUpperCase()));
        return instance;
    }

    @Override
    protected Stack<Value> createInstance(SlingHttpServletRequest request) {
        return createInstance(Value.NONE.name());
    }
}
