package com.composum.pages.commons.widget;

import com.composum.pages.commons.taglib.PropertyEditHandle;

import static com.composum.pages.commons.widget.Util.getIntegerOption;

public class NumberField extends PropertyEditHandle<Integer> {

    public static final int DEFAULT_MIN = 0;
    public static final int DEFAULT_STEP = 1;

    public class Options {

        public final Integer min;
        public final Integer max;
        public final Integer step;

        public Integer getMin() {
            return min;
        }

        public Integer getMax() {
            return max;
        }

        public Integer getStep() {
            return step;
        }

        public Options() {
            String[] options = widget.consumeDynamicAttribute("options", "").split(";");
            min = getIntegerOption(options, 0, DEFAULT_MIN);
            max = getIntegerOption(options, 1, null);
            step = getIntegerOption(options, 2, DEFAULT_STEP);
        }

        public String getRule() {
            StringBuilder builder = new StringBuilder();
            if (step != null && step != DEFAULT_STEP) {
                builder.append(min).append(':').append(max != null ? Integer.toString(max) : "").append(':').append(step);
            } else if (max != null) {
                builder.append(min).append(':').append(max);
            } else if (min != DEFAULT_MIN) {
                builder.append(min);
            }
            return builder.toString();
        }
    }

    private transient Options options;

    public NumberField() {
        super(Integer.class);
    }

    public String getText() {
        Integer value = getValue();
        return value != null ? Integer.toString(value) : "";
    }

    public Options getOptions() {
        if (options == null) {
            options = new Options();
        }
        return options;
    }
}
