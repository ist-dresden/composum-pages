package com.composum.pages.commons.widget;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * the general 1 string value of M string options value widget model (can be part of a multi value form group)
 */
public class SingleStringOption extends OptionsWidget<String> {

    public class SingleOption extends Option {

        public SingleOption(@Nonnull final String label, @Nonnull final String value, @Nullable final Object data) {
            super(label, value, data);
        }

        @Override
        public boolean isSelected() {
            String current = getCurrent();
            String value = getValue();
            return current != null ? value.equals(current) : value.equals(getDefaultOption());
        }
    }

    public SingleStringOption() {
        super(String.class);
    }

    @Override
    @Nonnull
    protected Option newOption(@Nonnull final String label, @Nonnull final String value, @Nullable final Object data) {
        return new SingleOption(label, value, data);
    }

    @Nullable
    public String getCurrent() {
        return getValue();
    }
}
