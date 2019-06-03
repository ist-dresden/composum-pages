package com.composum.pages.commons.widget;

import com.composum.pages.commons.model.properties.Language;
import com.composum.pages.commons.model.properties.Languages;
import com.composum.pages.commons.taglib.EditWidgetTag;
import com.composum.pages.commons.taglib.PropertyEditHandle;
import com.composum.sling.cpnl.CpnlElFunctions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class OptionsWidget<T> extends PropertyEditHandle<T> {

    // dynamic attributes consumed by the model itself

    /**
     * the 'options' are used to build the options list
     */
    public static final String ATTR_OPTIONS = "options";

    /**
     * the separators are useful if the default separators (',' and ':'; separators=", :") of a string based
     * options list are not useful to split the options attribute string (e.g. if the default separators are
     * necessary for the values ot labels of the options); the 'separators' attribute is splitted by a ' ' to
     * separate the list separator (default ',') from the key separator (default ':')
     */
    public static final String ATTR_SEPARATORS = "separators";
    public static final String DEFAULT_SEPARATORS = ", :";

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
            return CpnlElFunctions.i18n(context.getRequest(), label);
        }

        @Nonnull
        public T getValue() {
            return value;
        }

        @Nullable
        public Object getData() {
            return data;
        }

        public abstract boolean isSelected();
    }

    public OptionsWidget(Class<T> type) {
        super(type);
    }

    public void setWidget(EditWidgetTag tag) {
        super.setWidget(tag);
        options = retrieveOptions(); // consume the options attribute
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public List<Option> getOptions() {
        if (options == null) {
            options = retrieveOptions(); // lazy load if not configured during initialization
        }
        return options;
    }

    public List<T> getOptionValues() {
        if (optionValues == null) {
            optionValues = new ArrayList<>();
            for (Option option : getOptions()) {
                optionValues.add(option.getValue());
            }
        }
        return optionValues;
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
            String[] separators = StringUtils.split(widget.consumeDynamicAttribute(
                    ATTR_SEPARATORS, DEFAULT_SEPARATORS), " ", 2);
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
        return options;
    }

    @Nonnull
    protected List<Option> useString(String string, String listSeparator, String keySeparator) {
        Resource resource = resolver.getResource(string);
        if (resource != null) {
            return useResource(resource);
        }
        List<Option> options = new ArrayList<>();
        String[] values = string.split(listSeparator);
        for (String value : values) {
            String[] keyAndLabel = StringUtils.split(value.trim(), keySeparator, 2);
            String key = (keyAndLabel.length > 0 ? keyAndLabel[0] : value).trim();
            options.add(newOption((keyAndLabel.length > 1 ? keyAndLabel[1] : key), key, null));
        }
        return options;
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
        for (Language language : languages.getLanguageList()) {
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
