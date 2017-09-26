package com.composum.pages.commons.model.properties;

import com.composum.pages.commons.model.Model;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FloatTuple extends ValueSet<FloatTuple.Value> {

    private static final Logger LOG = LoggerFactory.getLogger(FloatTuple.class);

    protected abstract String getKeyOne();

    protected abstract String getKeyTwo();

    public static final float DEFAULT_VAL = 50f;

    public static class Value {

        public final String one;
        public final String two;
        public final float oneVal;
        public final float twoVal;

        public Value(String one, String two) {
            this.one = one;
            this.two = two;
            oneVal = parse(one);
            twoVal = parse(two);
        }

        protected float parse(String value) {
            try {
                return StringUtils.isNotBlank(value) ? Float.parseFloat(value) : DEFAULT_VAL;
            } catch (NumberFormatException nfex) {
                LOG.warn(nfex.getMessage(), nfex);
                return DEFAULT_VAL;
            }
        }
    }

    public FloatTuple(Model model, String name) {
        if (!initialize(model.getProperty(name + getKeyOne(), String.class),
                model.getProperty(name + getKeyTwo(), String.class))) {
            initialize(model.getProperty(name + getKeyOne(), String[].class),
                    model.getProperty(name + getKeyTwo(), String[].class));
        }
    }

    public FloatTuple(Resource resource, String name) {
        ValueMap values = resource.adaptTo(ValueMap.class);
        if (!initialize(values.get(name + getKeyOne(), String.class),
                values.get(name + getKeyTwo(), String.class))) {
            initialize(values.get(name + getKeyOne(), String[].class),
                    values.get(name + getKeyTwo(), String[].class));
        }
    }

    public FloatTuple(String width, String height) {
        initialize(width, height);
    }

    protected boolean initialize(String w, String h) {
        if (w != null && h != null) {
            add(new Value(w, h));
            return true;
        }
        return false;
    }

    protected void initialize(String[] w, String[] h) {
        if (w != null && h != null) {
            for (int i = 0; i < w.length; i++) {
                add(new Value(w[i], h[i]));
            }
        }
    }

    protected String getOne() {
        return size() > 0 ? get(0).one : "";
    }

    protected String getTwo() {
        return size() > 0 ? get(0).two : "";
    }

    protected float getOneVal() {
        return size() > 0 ? get(0).oneVal : DEFAULT_VAL;
    }

    protected float getTwoVal() {
        return size() > 0 ? get(0).twoVal : DEFAULT_VAL;
    }

    public String toString() {
        return getOne() + "," + getTwo();
    }
}
