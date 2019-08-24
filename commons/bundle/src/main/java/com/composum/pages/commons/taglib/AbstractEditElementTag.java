package com.composum.pages.commons.taglib;

/**
 * the base tag class for all dialog structure tags (dialog, dialogTag, dialogGroup, widget)
 */
public abstract class AbstractEditElementTag extends AbstractWrappingTag {

    protected Object disabled;
    protected Boolean disabledValue;

    @Override
    protected void clear() {
        disabledValue = null;
        disabled = null;
        super.clear();
    }

    /**
     * the general 'disabled' expression for the tags scope
     */
    public void setDisabled(Object value) {
        disabled = value;
    }

    protected boolean hasDisabledAttribute() {
        return disabled != null;
    }

    /**
     * the 'disabled' expression value
     */
    protected boolean getDisabledValue() {
        if (disabledValue == null) {
            disabledValue = eval(disabled, disabled instanceof Boolean ? (Boolean) disabled : Boolean.FALSE);
        }
        return disabledValue;
    }
}
