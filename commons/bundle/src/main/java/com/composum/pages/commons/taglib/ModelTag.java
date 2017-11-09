package com.composum.pages.commons.taglib;

import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.properties.Languages;
import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.servlet.EditServlet;
import com.composum.pages.commons.util.AttributeSet;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.SlingBean;
import com.composum.sling.cpnl.ComponentTag;
import com.composum.sling.cpnl.CpnlElFunctions;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import java.util.Map;

import static com.composum.pages.commons.model.AbstractModel.cssOfType;

/**
 * a tag to instantiate a model object
 */
public class ModelTag extends ComponentTag implements DynamicAttributes {

    protected String cssBase;
    protected Object test;
    private transient Boolean testResult;

    protected DisplayMode.Value displayMode;

    protected AttributeSet dynamicAttributes = new AttributeSet();

    @Override
    protected void clear() {
        dynamicAttributes = new AttributeSet();
        displayMode = null;
        testResult = null;
        test = null;
        cssBase = null;
        super.clear();
    }

    /**
     * the display mode for this tag rendering and all included content
     */
    public void setMode(String mode) {
        displayMode = DisplayMode.Value.valueOf(mode.toUpperCase());
    }

    public DisplayMode.Value getDisplayMode() {
        if (displayMode == null) {
            displayMode = DisplayMode.current(context);
        }
        return displayMode;
    }

    public boolean isEditMode() {
        DisplayMode.Value mode = getDisplayMode();
        return mode == DisplayMode.Value.EDIT || mode == DisplayMode.Value.DEVELOP;
    }

    /**
     * the 'test' expression for conditional tags
     */
    public void setTest(Object value) {
        test = value;
    }

    /**
     * evaluates the test expression if present and returns the evaluation result; default: 'true'
     */
    protected boolean getTestResult() {
        if (testResult == null) {
            testResult = eval(test, test instanceof Boolean ? (Boolean) test : Boolean.TRUE);
        }
        return testResult;
    }

    /**
     * the 'cssBase' attribute; default: derived from the components resource type
     */
    public String getCssBase() {
        return cssBase;
    }

    public void setCssBase(String cssKey) {
        cssBase = cssKey;
    }

    /**
     * generates the default 'cssBase' by delegation to the model or transforming the resource type
     */
    protected String buildCssBase() {
        String cssBase = null;
        if (component instanceof Model) {
            cssBase = ((Model) component).getCssBase();
        }
        if (StringUtils.isBlank(cssBase)) {
            String type = resource.getResourceType();
            cssBase = StringUtils.isNotBlank(type) ? cssOfType(type) : null;
        }
        return cssBase;
    }

    //
    // dynamic tag attributes
    //

    /**
     * extension hook to check and filter dynamic attributes
     */
    protected boolean acceptDynamicAttribute(String key, Object value) throws JspException {
        return value != null;
    }

    protected void setDynamicAttribute(String key, Object value) throws JspException {
        if (acceptDynamicAttribute(key, value)) {
            dynamicAttributes.setAttribute(key, value);
        }
    }

    public void setDynamicAttribute(String namespace, String name, Object value) throws JspException {
        String key = name;
        if (StringUtils.isNotBlank(namespace)) {
            key = namespace + ":" + key;
        }
        setDynamicAttribute(key, value);
    }

    /**
     * collects the set of tag attributes classes (extension hook)
     * adds all dynamic attributes as tag attributes
     */
    protected void collectAttributes(Map<String, String> attributeSet) {
        collectDynamicAttributes(attributeSet);
    }

    /**
     * adds all dynamic attributes to the attribute set
     */
    protected void collectDynamicAttributes(Map<String, String> attributeSet) {
        for (Map.Entry<String, Object> entry : dynamicAttributes) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                value = eval(value, value);
            }
            useDynamicAttribute(attributeSet, key, value.toString());
        }
    }

    /**
     * adds a dynamic attribute to the attribute set (extension hook)
     */
    protected void useDynamicAttribute(Map<String, String> attributeSet, String key, String value) {
        attributeSet.put(key, value);
    }

    /**
     * gets and removes a dynamic attribute
     */
    public <T> T consumeDynamicAttribute(String key, T defaultValue) {
        return dynamicAttributes.consumeAttribute(key, defaultValue);
    }

    //
    //
    //

    @Override
    public String getType() {
        String type = super.getType();
        if (StringUtils.isBlank(type)) {
            type = Element.class.getName();
        }
        return type;
    }

    public Resource getModelResource(BeanContext context) {
        return context.getResource();
    }

    public Object getModel() {
        return component;
    }

    public Resource getResource() {
        return resource;
    }

    public Languages getLanguages() {
        return Languages.get(context);
    }

    public String getNameHint() {
        return getResource().getName();
    }

    public String getPathHint() {
        return Element.getPathHint(getResource());
    }

    public String getResourceType() {
        String type = (String) request.getAttribute(EditServlet.EDIT_RESOURCE_TYPE_KEY);
        if (type == null) {
            type = getResource().getResourceType();
        }
        return type;
    }

    public String getTypeHint() {
        return Element.getTypeHint(getResourceType());
    }

    public String i18n(String text) {
        return CpnlElFunctions.i18n(request, text);
    }

    //
    // rendering ...
    //

    /**
     * collects all CSS classes and attributes, prepares the rendering (prepareTagStart())
     * and performs the rendering of the tag start (renderTagStart()) if 'renderTag()' returns 'true'
     * stores the resource to edit as request attribute for further use if not always present
     * if 'cssBase' is specified the '{var}CssBase' attribute is set in the page context
     */
    @Override
    public int doStartTag() throws JspException {
        super.doStartTag();
        if (StringUtils.isBlank(cssBase)) {
            cssBase = buildCssBase();
        } else {
            cssBase = eval(cssBase, cssBase);
        }
        String var = getVar();
        if (var != null) {
            if (StringUtils.isNotBlank(cssBase)) {
                setAttribute(var + "CssBase", cssBase, getVarScope());
            }
        }
        return EVAL_BODY_INCLUDE;
    }
}
