package com.composum.pages.commons.widget;

/**
 * the general N string values of M string options multi value widget model (can NOT be part of a multi value form group)
 */
public class MultiStringOption extends OptionsWidget<String> {

    public class MultiOption extends Option {

        public MultiOption(String label, String value, Object data) {
            super(label, value, data);
        }

        public boolean isSelected() {
            return getValues().contains(getValue());
        }
    }

    public MultiStringOption() {
        super(String.class);
        super.setMultiValue(true);
    }

    /**
     * the 'multi' behaviour is always given for this model
     */
    @Override
    public void setMultiValue(boolean isMultiValue) {
    }

    @Override
    protected Option newOption(String label, String value, Object data) {
        return new MultiOption(label, value, data);
    }
}
