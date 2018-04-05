package com.composum.pages.commons.widget;

import com.composum.pages.commons.taglib.EditWidgetTag;
import com.composum.pages.commons.taglib.PropertyEditHandle;

/**
 * the model for the 'checkbox' widget template is mapping a boolean property to the widget
 */
public class Checkbox extends PropertyEditHandle<Boolean> {

    // dynamic attributes consumed by the model itself

    /**
     * the 'storeFalse' is interpreted as boolean
     */
    public static final String ATTR_STORE_FALSE = "storeFalse";
    public static final String DEFAULT_STORE_FALSE = "false";

    // tag and attribute names for the generated HTML

    public static final String CHECKED_TAG_ATTR = "checked";

    protected Boolean storeFalse;

    public Checkbox() {
        super(Boolean.class);
    }

    /**
     * adds consuming the 'storeFalse' dynamic attribute from the tag
     */
    public void setWidget(EditWidgetTag tag) {
        super.setWidget(tag);
        storeFalse = Boolean.valueOf(tag.consumeDynamicAttribute(ATTR_STORE_FALSE, DEFAULT_STORE_FALSE));
    }

    /**
     * @return 'true' if the current value is 'true'
     */
    public boolean isChecked() {
        Boolean value = getValue();
        return value != null ? value : false;
    }

    /**
     * @return 'true' no second option available (no property if not checked)
     */
    public boolean isStoreFalse() {
        return storeFalse != null && storeFalse;
    }

    /**
     * @return 'checked' string if the checkbox has to be checked initially
     */
    public String getCheckedValue() {
        return isChecked() ? CHECKED_TAG_ATTR : "";
    }
}
