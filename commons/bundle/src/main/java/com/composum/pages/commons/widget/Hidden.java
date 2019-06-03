package com.composum.pages.commons.widget;

import com.composum.pages.commons.service.ResourceManager;
import com.composum.pages.commons.taglib.PropertyEditHandle;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Hidden extends PropertyEditHandle<Object> implements WidgetModel {

    public static final String ATTR_REQUEST = "request";

    public Hidden() {
        super(Object.class);
    }

    /**
     * consumed and supported attributes:
     * - request: the name of a request attribute to retrieve the hidden input values
     */
    @Override
    public String filterWidgetAttribute(String attributeKey, Object attributeValue) {
        if (ATTR_REQUEST.equals(attributeKey)) {
            // retrieve values from request
            SlingHttpServletRequest request = getContext().getRequest();
            String requestAttributeName;
            if (request != null && attributeValue != null &&
                    StringUtils.isNotBlank(requestAttributeName = attributeValue.toString())) {
                Object value = getValue(request.getAttribute(requestAttributeName));
                if (value != null) {
                    setValue(value);
                }
            }
            return null;
        }
        return attributeKey;
    }

    protected Object getValue(Object value) {
        if (value instanceof Collection) {
            List<String> values = new ArrayList<>();
            for (Object val : ((Collection) value)) {
                if ((val = getValue(val)) != null) {
                    values.add((String) val);
                }
            }
            return values;
        } else if (value instanceof ResourceManager.ResourceReference) {
            return ((ResourceManager.ResourceReference) value).getPath();
        } else if (value instanceof Resource) {
            return ((Resource) value).getPath();
        } else if (value != null) {
            return value.toString();
        }
        return null;
    }
}
