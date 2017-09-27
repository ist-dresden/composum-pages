package com.composum.pages.commons.taglib;

import com.composum.pages.commons.model.properties.ValueSet;
import com.composum.pages.commons.util.ResolverUtil;
import com.composum.pages.commons.widget.WidgetModel;
import com.composum.sling.core.SlingBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.core.LoopTag;
import javax.servlet.jsp.jstl.core.LoopTagStatus;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.composum.pages.commons.taglib.AbstractPageTag.COMMONS_COMPONENT_BASE;
import static com.composum.pages.commons.taglib.EditMultiWidgetTag.MULTIWIDGET_TYPE;

/**
 * the EditWidgetTag is rendering a dialog widget as an element of the edit dialog form
 */
public class EditWidgetTag extends AbstractWidgetTag implements LoopTag {

    public static final List<String> RULES_OPTIONS = Arrays.asList(
            "mandatory", "blank", "unique");
    public static final String RULES_ATTR = "rules";
    public static final String DATA_RULES_ATTR = "data-" + RULES_ATTR;

    public static final String WIDGET_VAR = "widget";
    public static final String WIDGET_CSS_VAR = WIDGET_VAR + "CssBase";

    public static final String DEFAULT_CSS_BASE = "composum-pages-edit-widget";

    public static final String MODEL_CLASS = "modelClass";
    public static final String DEFAULT_MODEL_CLASS = "";

    public static final String COMMONS_WIDGET_PATH = COMMONS_COMPONENT_BASE + "widget/";

    protected String widgetType;
    protected Object value;
    protected boolean multi = false;
    protected String hint;
    protected String placeholder;
    protected Boolean disabled;
    protected String rules;

    protected String status;
    private transient MultiValueStatus loopStatus;

    @Override
    protected void clear() {
        loopStatus = null;
        status = null;
        rules = null;
        disabled = null;
        placeholder = null;
        hint = null;
        multi = false;
        value = null;
        widgetType = null;
        super.clear();
    }

    public String getWidgetType() {
        return widgetType;
    }

    public void setWidgetType(String key) {
        widgetType = key;
    }

    public String getCssName() {
        return getName().replace('/', '-');
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object val) {
        value = val;
    }

    public boolean isMulti() {
        return multi;
    }

    public void setMulti(boolean val) {
        multi = val;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String var) {
        status = var;
    }

    public void setHint(String val) {
        hint = val;
    }

    public String getHint() {
        return hint;
    }

    public boolean getHasHint() {
        return StringUtils.isNotBlank(getHint());
    }

    public String getPlaceholder() {
        String result = placeholder;
        if (component instanceof PropertyEditHandle) {
            Object defaultValue = ((PropertyEditHandle) component).getDefaultValue();
            if (defaultValue != null) {
                result = defaultValue.toString();
            }
        }
        return i18n(result);
    }

    public void setPlaceholder(String val) {
        placeholder = val;
    }

    public boolean isDisabled() {
        return disabled != null && disabled;
    }

    public void setDisabled(boolean val) {
        disabled = val;
    }

    @Override
    protected void collectAttributes(Map<String, String> attributeSet) {
        collectDynamicAttributes(attributeSet);
    }

    @Override
    protected void useDynamicAttribute(Map<String, String> attributeSet, String key, String value) {
        Object model = getModel();
        if (model instanceof WidgetModel) {
            String widgetKey = ((WidgetModel) model).getWidgetAttributeKey(key);
            if (widgetKey != null) {
                attributeSet.put(widgetKey, value);
            }
        } else {
            super.useDynamicAttribute(attributeSet, key, value);
        }
    }

    /**
     * collect the (validation) rules as dynamic attributes
     */
    @Override
    protected void setDynamicAttribute(String key, Object value) throws JspException {
        String attributeKey = key.toLowerCase();
        if (RULES_OPTIONS.contains(attributeKey)) {
            dynamicAttributes.setOption(DATA_RULES_ATTR, attributeKey, value);
        } else if (RULES_ATTR.equals(attributeKey) || DATA_RULES_ATTR.equals(attributeKey)) {
            String string = (String) value;
            if (StringUtils.isNotBlank(string)) {
                for (String option : StringUtils.split(string, ",")) {
                    dynamicAttributes.setOption(DATA_RULES_ATTR, option, Boolean.TRUE);
                }
            }
        } else {
            super.setDynamicAttribute(key, value);
        }
    }

    /**
     * the 'type' defined the widget type instead of the model component class
     */
    @Override
    public void setType(String type) {
        setWidgetType(type);
    }

    /**
     * the component type is defined as the 'modelClass' declared in the tag or by the widget resource type
     */
    @Override
    public String getType() {
        String modelClass = getModelClass();
        if (StringUtils.isBlank(modelClass)) {
            Resource widget = ResolverUtil.getResourceType(resourceResolver, getSnippetResourceType());
            while (StringUtils.isBlank(modelClass) && widget != null) {
                ValueMap widgetValues = widget.adaptTo(ValueMap.class);
                modelClass = widgetValues.get(MODEL_CLASS, DEFAULT_MODEL_CLASS);
                widget = ResolverUtil.getResourceType(resourceResolver, widget.getResourceSuperType());
            }
        }
        return modelClass;
    }

    @Override
    public String getVar() {
        return "";
    }

    @Override
    protected void additionalInitialization(SlingBean component) {
        if (component instanceof PropertyEditHandle) {
            PropertyEditHandle handle = (PropertyEditHandle) component;
            handle.setWidget(this);
            handle.setMultiValue(isMulti());
            handle.setProperty(property != null ? property : name, getPropertyName(), isI18n());
            handle.setValue(getValue());
        }
    }

    @Override
    public int doStartTag() throws JspException {
        if (StringUtils.isBlank(cssBase)) {
            cssBase = DEFAULT_CSS_BASE;
        }
        super.doStartTag();
        return SKIP_BODY;
    }

    protected String getSnippetResourceType() {
        return COMMONS_WIDGET_PATH + getWidgetType();
    }

    protected String getMultiResourceType() {
        return COMMONS_WIDGET_PATH + MULTIWIDGET_TYPE;
    }

    @Override
    protected void prepareTagStart() {
        if (multi) {
            PropertyEditHandle editHandle = (PropertyEditHandle) component;
            loopStatus = new MultiValueStatus(editHandle);
            if (status != null) {
                pageContext.setAttribute(status, loopStatus, PageContext.REQUEST_SCOPE);
            }
            loopStatus.next();
        }
        setAttribute(WIDGET_VAR, this, PageContext.REQUEST_SCOPE);
        if (StringUtils.isNotBlank(cssBase)) {
            setAttribute(WIDGET_CSS_VAR, cssBase, PageContext.REQUEST_SCOPE);
        }
    }

    @Override
    protected void renderTagStart() throws JspException, IOException {
        if (multi) {
            includeSnippet(getMultiResourceType(), "multiwidget-simple");
        }
    }

    @Override
    protected void renderTagEnd() throws JspException, IOException {
        includeSnippet(getSnippetResourceType(), getDialogActionType());
        if (multi) {
            try {
                while (loopStatus.hasNext()) {
                    includeSnippet(getMultiResourceType(), "multiwidget-next");
                    loopStatus.next();
                    includeSnippet(getSnippetResourceType(), getDialogActionType());
                }
            } catch (IOException ioex) {
                throw new JspException(ioex);
            }
            includeSnippet(getMultiResourceType(), "multiwidget-end");
        }
    }

    @Override
    protected void finishTagEnd() {
        if (multi) {
            if (status != null) {
                pageContext.removeAttribute(status, PageContext.REQUEST_SCOPE);
            }
        }
        if (StringUtils.isNotBlank(cssBase)) {
            pageContext.removeAttribute(WIDGET_CSS_VAR, PageContext.REQUEST_SCOPE);
        }
        pageContext.removeAttribute(WIDGET_VAR, PageContext.REQUEST_SCOPE);
    }

    // loop tag

    @Override
    public Object getCurrent() {
        return loopStatus.getCurrent();
    }

    @Override
    public LoopTagStatus getLoopStatus() {
        return loopStatus;
    }

    public class MultiValueStatus implements LoopTagStatus, Iterator<Object> {

        protected PropertyEditHandle propertyHandle;

        public MultiValueStatus(PropertyEditHandle editHandle) {
            propertyHandle = editHandle;
        }

        protected Object createEmptyPlaceholder() {
            return null;
        }

        @Override
        public Object getCurrent() {
            return propertyHandle.getValue();
        }

        @Override
        public int getIndex() {
            ValueSet values = propertyHandle.getValues();
            return values != null ? values.getIndex() : -1;
        }

        @Override
        public int getCount() {
            ValueSet values = propertyHandle.getValues();
            return values != null ? values.size() : 0;
        }

        @Override
        public boolean isFirst() {
            return getIndex() == 0;
        }

        @Override
        public boolean isLast() {
            return getIndex() == getCount() - 1;
        }

        @Override
        public Integer getBegin() {
            return null;
        }

        @Override
        public Integer getEnd() {
            return null;
        }

        @Override
        public Integer getStep() {
            return null;
        }

        @Override
        public boolean hasNext() {
            ValueSet values = propertyHandle.getValues();
            return values != null && values.hasNext();
        }

        @Override
        public Object next() {
            propertyHandle.nextValue();
            return propertyHandle.getValue();
        }

        @Override
        public void remove() {
        }
    }
}
