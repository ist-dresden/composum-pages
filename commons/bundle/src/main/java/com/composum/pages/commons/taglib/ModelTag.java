/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.taglib;

import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.GenericModel;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.properties.Language;
import com.composum.pages.commons.model.properties.Languages;
import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.service.ResourceManager;
import com.composum.pages.commons.servlet.EditServlet;
import com.composum.pages.commons.util.AttributeSet;
import com.composum.pages.commons.util.TagCssClasses;
import com.composum.platform.models.annotations.InternationalizationStrategy;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.I18N;
import com.composum.sling.cpnl.ComponentTag;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.composum.pages.commons.util.TagCssClasses.cssOfType;
import static com.composum.platform.models.annotations.InternationalizationStrategy.I18NFOLDER.I18N_PROPERTY_PATH;

/**
 * a tag to instantiate a model object
 */
public class ModelTag extends ComponentTag implements DynamicAttributes {

    public static final String DEFAULT_VAR_NAME = "target";

    public static final String TAG_ID = "id";

    public static final String PAGES_EDIT_DATA = "data-pages-edit";
    public static final String PAGES_EDIT_DATA_ENCODED = PAGES_EDIT_DATA + "-encoded";
    public static final String PAGES_EDIT_DATA_PATH = PAGES_EDIT_DATA + "-path";
    public static final String PAGES_EDIT_DATA_TYPE = PAGES_EDIT_DATA + "-type";

    public static final String EDIT_CSS_BASE = "composum-pages-edit";

    private transient TagCssClasses tagCssClasses;
    protected String cssBase;
    protected Object test;
    private transient Boolean testResult;

    private transient String attributes;
    protected DisplayMode.Value displayMode;

    protected AttributeSet dynamicAttributes = new AttributeSet();

    @Override
    protected void clear() {
        dynamicAttributes = new AttributeSet();
        attributes = null;
        displayMode = null;
        testResult = null;
        test = null;
        cssBase = null;
        tagCssClasses = null;
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
    public String buildCssBase() {
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

    protected TagCssClasses getTagCssClasses() {
        if (tagCssClasses == null) {
            tagCssClasses = new TagCssClasses();
        }
        return tagCssClasses;
    }

    /**
     * collects the set of CSS classes (extension hook)
     * adds the 'cssBase' itself as CSS class and the transformed resource super type if available
     */
    protected void collectCssClasses(TagCssClasses.CssSet collection) {
        if (StringUtils.isBlank(getTagCssClasses().getCssSet())) {
            collection.add(getCssBase());
        }
    }

    /**
     * builds the complete CSS classes string with the given classes and all collected classes
     */
    public String buildCssClasses() {
        TagCssClasses.CssSet collection = getTagCssClasses().getCssClasses();
        collectCssClasses(collection);
        return getTagCssClasses().toString();
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
    protected void collectAttributes(Map<String, Object> attributeSet) {
        collectDynamicAttributes(attributeSet);
    }

    /**
     * adds all dynamic attributes to the attribute set
     */
    protected void collectDynamicAttributes(Map<String, Object> attributeSet) {
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
    protected void useDynamicAttribute(Map<String, Object> attributeSet, String key, Object value) {
        attributeSet.putIfAbsent(key, value);
    }

    /**
     * gets and removes a dynamic attribute
     */
    public <T> T consumeDynamicAttribute(String key, Class<T> type) {
        return dynamicAttributes.consumeAttribute(key, type);
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
    public String getVar() {
        String varName = super.getVar();
        if (StringUtils.isBlank(varName)) {
            varName = DEFAULT_VAR_NAME;
        }
        return varName;
    }

    @Override
    public String getType() {
        String type = super.getType();
        if (StringUtils.isBlank(type)) {
            type = GenericModel.class.getName();
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

    public String getRequestLanguage() {
        return request.getLocale().getLanguage();
    }

    public Languages getLanguages() {
        return Languages.get(context);
    }

    /**
     * @param relativePath a relative path with a closing '/' if not empty which is prepended (include path)
     * @param name         the name (property name or path) which has to be extended in the language context
     * @return the path with inserted 'i18n' segment if the language context is not the default language context
     * @see InternationalizationStrategy
     */
    protected String getI18nPath(String relativePath, String name) {
        Languages languages = getLanguages();
        if (languages != null) {
            Language defaultLanguage = languages.getDefaultLanguage();
            if (defaultLanguage != null && !defaultLanguage.isCurrent()) {
                Language language = languages.getLanguage();
                return relativePath + I18N_PROPERTY_PATH + language.getKey() + "/" + name;
            }
        }
        return relativePath + name;
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
        return I18N.get(request, text);
    }

    /**
     * returns the complete set of attributes as one string value with a leading space
     * provided to embed all attributes in a template (JSP or something else)
     */
    public String getAttributes() {
        if (attributes == null) {
            StringBuilder builder = new StringBuilder();
            Map<String, Object> attributeSet = new LinkedHashMap<>();
            collectAttributes(attributeSet);
            for (Map.Entry<String, Object> attribute : attributeSet.entrySet()) {
                builder.append(" ").append(attribute.getKey()).append("=\"").append(attribute.getValue()).append("\"");
            }
            attributes = builder.toString();
        }
        return attributes;
    }

    /**
     * builds the list of tag attributes for the wrapping tag
     */
    protected void addEditAttributes(@Nonnull Map<String, Object> attributeSet,
                                     @Nonnull Resource resource, @Nullable String resourceType) {
        ResourceManager resourceManager = context.getService(ResourceManager.class);
        ResourceManager.ResourceReference reference = resourceManager.getReference(resource, resourceType);
        attributeSet.put(PAGES_EDIT_DATA_ENCODED,
                Base64.encodeBase64String(reference.getEditData().toString().getBytes(StandardCharsets.UTF_8)));
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
                setAttribute(var + "CSS", cssBase, getVarScope());
                setAttribute(var + "CssBase", cssBase, getVarScope());
            }
        }
        return EVAL_BODY_INCLUDE;
    }
}
