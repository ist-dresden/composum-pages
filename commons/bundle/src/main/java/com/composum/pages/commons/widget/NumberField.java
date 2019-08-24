package com.composum.pages.commons.widget;

import com.composum.pages.commons.taglib.PropertyEditHandle;

import javax.annotation.Nonnull;

import static com.composum.pages.commons.widget.Util.getIntegerOption;

public class NumberField extends PropertyEditHandle<Integer> implements WidgetModel {

    public static final String ATTR_OPTIONS = "options";

    public static final int DEFAULT_MIN = 0;
    public static final int DEFAULT_STEP = 1;

    public class Options {

        public final Integer min;
        public final Integer max;
        public final Integer step;
        public final Integer def;

        public Integer getMin() {
            return min;
        }

        public Integer getMax() {
            return max;
        }

        public Integer getStep() {
            return step;
        }

        public Options(String attr) {
            String[] options = attr.split("[,;:]");
            min = getIntegerOption(options, 0, DEFAULT_MIN);
            max = getIntegerOption(options, 1, null);
            step = getIntegerOption(options, 2, DEFAULT_STEP);
            def = getIntegerOption(options, 3, null);
        }

        public String getRule() {
            StringBuilder builder = new StringBuilder();
            if (min != null) {
                builder.append(min);
            }
            builder.append(':');
            if (step != null) {
                builder.append(step);
            }
            builder.append(':');
            if (max != null) {
                builder.append(max);
            }
            if (def != null) {
                builder.append(':').append(def);
            }
            return builder.toString();
        }
    }

    private Options options;

    public NumberField() {
        super(Integer.class);
    }

    public String getText() {
        Integer value = getValue();
        return value != null ? Integer.toString(value) : "";
    }

    public Options getOptions() {
        if (options == null) {
            options = new Options(widget.consumeDynamicAttribute(ATTR_OPTIONS, ""));
        }
        return options;
    }

    @Override
    public String filterWidgetAttribute(@Nonnull String attributeKey, Object attributeValue) {
        if (ATTR_OPTIONS.equals(attributeKey)) {
            options = new Options(attributeValue instanceof String ? (String) attributeValue : "");
            return null;
        }
        return attributeKey;
    }
}
