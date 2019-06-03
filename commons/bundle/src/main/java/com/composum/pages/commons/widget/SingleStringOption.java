package com.composum.pages.commons.widget;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * the general 1 string value of M string options value widget model (can be part of a multi value form group)
 */
public class SingleStringOption extends OptionsWidget<String> {

    private transient String current;

    public class SingleOption extends Option {

        public SingleOption(@Nonnull final String label, @Nonnull final String value, @Nullable final Object data) {
            super(label, value, data);
        }

        public boolean isSelected() {
            return getValue().equals(getCurrent());
        }
    }

    public SingleStringOption() {
        super(String.class);
    }

    @Nonnull
    protected Option newOption(@Nonnull final String label, @Nonnull final String value, @Nullable final Object data) {
        return new SingleOption(label, value, data);
    }

    @Nullable
    public String getCurrent() {
        if (current == null) {
            current = getValue();
            if (StringUtils.isBlank(current)) {
                String defaultValue = getDefaultValue();
                if (defaultValue != null) {
                    current = defaultValue;
                }
            }
        }
        return current;
    }
}
