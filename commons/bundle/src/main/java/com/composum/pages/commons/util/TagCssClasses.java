package com.composum.pages.commons.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * the delegate to collect CSS class attributes of a custom JSP tag
 */
public class TagCssClasses {

    /**
     * the set of CSS classes ensures the uniqueness off the collected class names
     */
    public class CssSet extends ArrayList<String> {

        @Override
        public boolean add(String cssClass) {
            return StringUtils.isNotBlank(cssClass) && !contains(cssClass = cssClass.trim()) && super.add(cssClass);
        }

        @Override
        public boolean addAll(Collection<? extends String> cssClasses) {
            boolean result = false;
            for (String cssClass : cssClasses) {
                result = add(cssClass) || result;
            }
            return result;
        }

        @Override
        public String toString() {
            return StringUtils.join(this, " ");
        }
    }

    protected String cssSet;
    protected String cssAdd;

    private transient CssSet collection;

    @Override
    public String toString() {
        return getCssClasses().toString();
    }

    /**
     * collects the set of CSS classes (extension hook)
     * adds the 'cssBase' itself as CSS class and the transformed resource super type if available
     */
    protected void collectCssClasses() {
        if (StringUtils.isNotBlank(cssSet)) {
            collection.addAll(Arrays.asList(cssSet.split(" +")));
        }
        if (StringUtils.isNotBlank(cssAdd)) {
            collection.addAll(Arrays.asList(cssAdd.split(" +")));
        }
    }

    public CssSet getCssClasses() {
        if (collection == null) {
            collection = new CssSet();
            collectCssClasses();
        }
        return collection;
    }

    public String getCssAdd() {
        return cssAdd;
    }

    public void setCssAdd(String cssAdd) {
        this.cssAdd = cssAdd;
    }

    public String getCssSet() {
        return cssSet;
    }

    public void setCssSet(String cssSet) {
        this.cssSet = cssSet;
    }

    /**
     * Transforms a resource type into a CSS class name; replaces all '/' by '-'.
     */
    public static String cssOfType(String type) {
        if (StringUtils.isNotBlank(type)) {
            type = type.replaceAll("^/((apps|libs)/)?", "");
            type = type.replace('/', '-').replace(':', '-');
        }
        return type;
    }
}
