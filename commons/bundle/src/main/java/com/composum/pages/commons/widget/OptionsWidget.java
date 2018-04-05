package com.composum.pages.commons.widget;

import com.composum.pages.commons.taglib.EditWidgetTag;
import com.composum.pages.commons.taglib.PropertyEditHandle;
import com.composum.sling.cpnl.CpnlElFunctions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class OptionsWidget extends PropertyEditHandle<String> {

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

    public class Option {

        private final String label;
        private final String value;
        private final Object data;

        public Option(String label, String value) {
            this(label, value, null);
        }

        public Option(String label, String value, Object data) {
            this.label = label;
            this.value = value;
            this.data = data;
        }

        public String getLabel() {
            return CpnlElFunctions.i18n(context.getRequest(), label);
        }

        public String getValue() {
            return value;
        }

        public Object getData() {
            return data;
        }

        public boolean isSelected() {
            return value.equals(getCurrent());
        }
    }

    public OptionsWidget() {
        super(String.class);
    }

    public void setWidget(EditWidgetTag tag) {
        super.setWidget(tag);
        options = retrieveOptions(); // consume the options attribute
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

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public List<Option> getOptions() {
        return options;
    }

    /**
     * @return the configured options as a JSON array
     */
    public String getOptionsData() {
        JsonArray data = new JsonArray();
        for (Option option : getOptions()) {
            JsonObject item = new JsonObject();
            item.addProperty("value", option.getValue());
            item.addProperty("label", option.getLabel());
            data.add(item);
        }
        return data.toString();
    }

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
        } else {
            options = new ArrayList<>();
        }
        return options;
    }

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
            options.add(new Option((keyAndLabel.length > 1 ? keyAndLabel[1] : key), key));
        }
        return options;
    }

    protected List<Option> useMap(Map<String, Object> map) {
        List<Option> options = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            options.add(new Option(entry.getValue().toString(), entry.getKey(), entry.getValue()));
        }
        return options;
    }

    protected List<Option> useList(List<String> list) {
        List<Option> options = new ArrayList<>();
        for (String value : list) {
            options.add(new Option(value, value));
        }
        return options;
    }

    protected List<Option> useResource(Resource resource) {
        List<Option> options = new ArrayList<>();
        for (Resource node : resource.getChildren()) {
            options.add(new Option(getLabel(node), node.getName(), resource));
        }
        return options;
    }

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
