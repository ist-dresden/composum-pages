package com.composum.pages.commons.widget;

import com.composum.pages.commons.taglib.EditWidgetTag;
import com.composum.pages.commons.taglib.PropertyEditHandle;
import com.composum.sling.cpnl.CpnlElFunctions;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class OptionsWidget extends PropertyEditHandle<String> {

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

    protected List<Option> retrieveOptions() {
        List<Option> options;
        Object optionsObject = widget.consumeDynamicAttribute("options", Object.class);
        if (optionsObject instanceof String) {
            options = useString((String) optionsObject);
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

    protected List<Option> useString(String string) {
        Resource resource = resolver.getResource(string);
        if (resource != null) {
            return useResource(resource);
        }
        List<Option> options = new ArrayList<>();
        String[] values = string.split(",");
        for (String value : values) {
            String[] keyAndLabel = StringUtils.split(value.trim(), ':');
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
