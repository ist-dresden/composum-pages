package com.composum.pages.commons.widget;

import com.composum.pages.commons.model.properties.Language;
import com.composum.pages.commons.model.properties.Languages;
import com.composum.pages.commons.taglib.PropertyEditHandle;
import com.composum.sling.core.util.I18N;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class OptionsWidget<T> extends PropertyEditHandle<T> implements WidgetModel {

    // dynamic attributes consumed by the model itself

    /**
     * the 'options' are used to build the options list
     */
    public static final String ATTR_OPTIONS = "options";

    /**
     * a 'default' option value triggers a property deletion if that default value  is selected
     */
    public static final String ATTR_DEFAULT = "default";

    /**
     * the separators are useful if the default separators (',' and ':'; separators=", :") of a string based
     * options list are not useful to split the options attribute string (e.g. if the default separators are
     * necessary for the values or labels of the options); the 'separators' attribute is splitted by a ' ' to
     * separate the list separator (default ',') from the key separator (default ':')
     */
    public static final String ATTR_SEPARATORS = "separators";
    public static final String DEFAULT_SEPARATORS = ", :";

    public static final String ATTR_PREPEND = "prepend";
    public static final String ATTR_APPEND = "append";

    protected String[] separators;
    protected String prepend;
    protected String append;

    private transient T defaultOption;
    private transient List<Option> options;
    private transient List<T> optionValues;

    public abstract class Option {

        private final String label;
        private final T value;
        private final Object data;

        public Option(@Nonnull final String label, @Nonnull final T value, @Nullable final Object data) {
            this.label = label;
            this.value = value;
            this.data = data;
        }

        @Nonnull
        public String getLabel() {
            return I18N.get(context.getRequest(), label);
        }

        @Nonnull
        public T getValue() {
            return value;
        }

        @Nullable
        public Object getData() {
            return data;
        }

        public boolean isDefault() {
            return value.equals(getDefaultOption());
        }

        public abstract boolean isSelected();
    }

    public OptionsWidget(Class<T> type) {
        super(type);
    }

    @Override
    public String filterWidgetAttribute(@Nonnull final String attributeKey, Object attributeValue) {
        switch (attributeKey) {
            case ATTR_DEFAULT:
                //noinspection unchecked
                defaultOption = (T) attributeValue;
                return null;
            case ATTR_PREPEND:
                prepend = (String) attributeValue;
                return null;
            case ATTR_APPEND:
                append = (String) attributeValue;
                return null;
            default:
                return attributeKey;
        }
    }

    @Nonnull
    protected String[] getSeparators() {
        if (separators == null) {
            separators = StringUtils.split(widget.consumeDynamicAttribute(ATTR_SEPARATORS, DEFAULT_SEPARATORS), " ", 2);
        }
        return separators;
    }

    public void setOptions(@Nonnull final List<Option> options) {
        this.options = options;
    }

    @Nonnull
    public List<Option> getOptions() {
        if (options == null) {
            options = retrieveOptions(); // lazy load(!)
        }
        return options;
    }

    @Nonnull
    public List<T> getOptionValues() {
        if (optionValues == null) {
            optionValues = new ArrayList<>();
            for (Option option : getOptions()) {
                optionValues.add(option.getValue());
            }
        }
        return optionValues;
    }

    @Nullable
    public T getDefaultOption() {
        return defaultOption;
    }

    public boolean isHasDefaultOption() {
        return getDefaultOption() != null;
    }

    protected abstract Option newOption(String label, String value, Object data);

    /**
     * @return the configured options as a JSON array
     */
    @Nonnull
    public String getOptionsData() {
        JsonArray data = new JsonArray();
        for (Option option : getOptions()) {
            JsonObject item = new JsonObject();
            item.addProperty("value", option.getValue().toString());
            item.addProperty("label", option.getLabel());
            data.add(item);
        }
        return data.toString();
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    protected List<Option> retrieveOptions() {
        List<Option> options;
        Object optionsObject = widget.consumeDynamicAttribute(ATTR_OPTIONS, Object.class);
        if (optionsObject instanceof String) {
            String[] separators = getSeparators();
            options = useString((String) optionsObject, separators[0], separators[1]);
        } else if (optionsObject instanceof Map) {
            options = useMap((Map<String, Object>) optionsObject);
        } else if (optionsObject instanceof List) {
            options = useList((List<String>) optionsObject);
        } else if (optionsObject instanceof Resource) {
            options = useResource((Resource) optionsObject);
        } else if (optionsObject instanceof Languages) {
            options = useLanguages((Languages) optionsObject);
        } else {
            options = new ArrayList<>();
        }
        if (StringUtils.isNotBlank(prepend)) {
            options.add(0, createOption(prepend, getSeparators()[1]));
        }
        if (StringUtils.isNotBlank(append)) {
            options.add(createOption(append, getSeparators()[1]));
        }
        return options;
    }

    @Nonnull
    protected List<Option> useString(@Nonnull final String string,
                                     @Nonnull final String listSeparator, @Nonnull final String keySeparator) {
        Resource resource = resolver.getResource(string);
        if (resource != null) {
            return useResource(resource);
        }
        List<Option> options = new ArrayList<>();
        String[] values = string.split(listSeparator);
        for (String value : values) {
            options.add(createOption(value, keySeparator));
        }
        return options;
    }

    @Nonnull
    protected Option createOption(@Nonnull final String value, @Nonnull final String keySeparator) {
        String[] keyAndLabel = StringUtils.splitPreserveAllTokens(value.trim(), keySeparator, 2);
        String key = (keyAndLabel.length > 0 ? keyAndLabel[0] : value).trim();
        return newOption((keyAndLabel.length > 1 ? keyAndLabel[1] : key), key, null);
    }

    @Nonnull
    protected List<Option> useMap(Map<String, Object> map) {
        List<Option> options = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            options.add(newOption(entry.getValue().toString(), entry.getKey(), entry.getValue()));
        }
        return options;
    }

    @Nonnull
    protected List<Option> useList(List<String> list) {
        List<Option> options = new ArrayList<>();
        for (String value : list) {
            options.add(newOption(value, value, null));
        }
        return options;
    }

    @Nonnull
    protected List<Option> useResource(Resource resource) {
        List<Option> options = new ArrayList<>();
        for (Resource node : resource.getChildren()) {
            options.add(newOption(getLabel(node), node.getName(), resource));
        }
        return options;
    }

    @Nonnull
    protected List<Option> useLanguages(Languages languages) {
        List<Option> options = new ArrayList<>();
        for (Language language : languages.getLanguages()) {
            options.add(newOption(language.getLabel(), language.getKey(), language));
        }
        return options;
    }

    @Nonnull
    protected String getLabel(Resource resource) {
        String label;
        if (StringUtils.isBlank(label = "label")) {
            if (StringUtils.isBlank(label = "title")) {
                if (StringUtils.isBlank(label = "jcr:title")) {
                    label = resource.getName();
                }
            }
        }
        return label;
    }
}
