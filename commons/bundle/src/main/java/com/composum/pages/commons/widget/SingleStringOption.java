package com.composum.pages.commons.widget;

import org.apache.commons.lang3.StringUtils;

/**
 * the general 1 string value of M string options value widget model (can be part of a multi value form group)
 */
public class SingleStringOption extends OptionsWidget<String> {

    public class SingleOption extends Option {

        public SingleOption(String label, String value, Object data) {
            super(label, value, data);
        }

        public boolean isSelected() {
            return getValue().equals(getCurrent());
        }
    }

    public SingleStringOption() {
        super(String.class);
    }

    protected Option newOption(String label, String value, Object data) {
        return new SingleOption(label, value, data);
    }

    public String getCurrent() {
        String value = getValue();
        if (StringUtils.isBlank(value)) {
            String defaultValue = getDefaultValue();
            if (defaultValue != null) {
                value = defaultValue;
            }
        }
        return value;
    }
}
