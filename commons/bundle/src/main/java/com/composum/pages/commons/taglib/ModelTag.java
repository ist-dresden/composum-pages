/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.taglib;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.GenericModel;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.properties.LanguageSet;
import com.composum.pages.commons.model.properties.Languages;
import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.request.PagesLocale;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.composum.pages.commons.util.TagCssClasses.cssOfType;
import static com.composum.platform.models.annotations.InternationalizationStrategy.I18NFOLDER.I18N_PROPERTY_PATH;

/**
 * a tag to instantiate a model object and the tag base for the edit tags
 */
public class ModelTag extends ComponentTag implements DynamicAttributes {

    private static final Logger LOG = LoggerFactory.getLogger(ModelTag.class);

    public static final String DEFAULT_VAR_NAME = "target";

    public static final String TAG_ID = "id";

    public static final String PAGES_EDIT_DATA = "data-pages-edit";
    public static final String PAGES_EDIT_DATA_ENCODED = PAGES_EDIT_DATA + "-encoded";
    public static final String PAGES_EDIT_DATA_PATH = PAGES_EDIT_DATA + "-path";
    public static final String PAGES_EDIT_DATA_TYPE = PAGES_EDIT_DATA + "-type";

    public static final String EDIT_CSS_BASE = "composum-pages-edit";

    public static final String PARAM_LOCALE = "locale";

    private transient Page currentPage;

    private transient TagCssClasses tagCssClasses;
    protected String cssBase;
    protected Object test;
    private transient Boolean testResult;

    private transient String attributes;
    protected DisplayMode.Value displayMode;

    private transient String language;
    private transient LanguageSet languageSet;

    protected AttributeSet dynamicAttributes = new AttributeSet();

    private transient String tagDebug;

    @Override
    protected void clear() {
        tagDebug = null;
        dynamicAttributes = new AttributeSet();
        languageSet = null;
        language = null;
        attributes = null;
        displayMode = null;
        testResult = null;
        test = null;
        cssBase = null;
        tagCssClasses = null;
        currentPage = null;
        super.clear();
    }

    /**
     * the display mode for this tag rendering and all included content
     */
    public void setMode(String mode) {
        if (StringUtils.isNotBlank(mode)) {
            displayMode = DisplayMode.Value.valueOf(mode.toUpperCase());
        }
    }

    public DisplayMode.Value getDisplayMode() {
        if (displayMode == null) {
            displayMode = DisplayMode.current(context);
        }
        return displayMode;
    }

    public boolean isEditMode() {
        DisplayMode.Value mode = getDisplayMode();
        return DisplayMode.isEditMode(getDisplayMode());
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

    /**
     * @return the CSS class collection of this tag
     */
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
     * gets a dynamic attribute from the attribute set and removes the attribute key from the set
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
     * adds the set of editing tag attributes for the wrapping tag
     */
    protected void addEditAttributes(@Nonnull Map<String, Object> attributeSet,
                                     @Nonnull Resource resource, @Nullable String resourceType) {
        ResourceManager resourceManager = context.getService(ResourceManager.class);
        ResourceManager.ResourceReference reference = resourceManager.getReference(resource, resourceType);
        attributeSet.put(PAGES_EDIT_DATA_ENCODED,
                Base64.encodeBase64String(reference.getEditData().toString().getBytes(StandardCharsets.UTF_8)));
    }

    //
    //
    //

    /**
     * @return the name of the 'var' model object declared by this tag
     */
    @Override
    public String getVar() {
        String varName = super.getVar();
        if (StringUtils.isBlank(varName)) {
            varName = DEFAULT_VAR_NAME;
        }
        return varName;
    }

    /**
     * @return the type (the full class name) of the 'var' to provide by this tag
     */
    @Override
    public String getType() {
        String type = super.getType();
        if (StringUtils.isBlank(type)) {
            type = GenericModel.class.getName();
        }
        return type;
    }

    /**
     * retrieves the resource to use for the model construction
     * @param context the current request context
     * @return the resource base of the model instance
     */
    public Resource getModelResource(BeanContext context) {
        return context.getResource();
    }

    /**
     * @return the model instance generated by this tag
     */
    public Object getModel() {
        return component;
    }

    /**
     * @return the resource of the rendering request
     */
    public Resource getResource() {
        return resource;
    }

    //
    // I18N
    //

    public String getRequestLanguage() {
        if (language == null) {
            PagesLocale locale = request.adaptTo(PagesLocale.class);
            language = (locale != null ? locale.getLocale() : request.getLocale()).getLanguage();
        }
        return language;
    }

    @Nullable
    public Page getCurrentPage() {
        if (currentPage == null) {
            currentPage = context.getAttribute(PagesConstants.RA_CURRENT_PAGE, Page.class);
        }
        return currentPage;
    }

    /**
     * @return the set of languages of the current page; fallback: the set of languages of the site
     */
    @Nonnull
    public LanguageSet getLanguageSet() {
        if (languageSet == null) {
            Page currentPage = getCurrentPage();
            languageSet = currentPage != null
                    ? currentPage.getPageLanguages().getLanguageSet()
                    : getLanguages().getLanguageSet();
        }
        return languageSet;
    }

    /**
     * @return the languages of the current site
     */
    @Nonnull
    public Languages getLanguages() {
        return Languages.get(context);
    }

    /**
     * @param text the text to translate
     * @return the text translated to the requests language if such a translation can be found
     */
    public String i18n(String text) {
        return I18N.get(request, text);
    }

    /**
     * @param relativePath a relative path with a closing '/' which is prepended if not empty (include path)
     * @param name         the name (property name or path) which has to be extended in the language context
     * @return the path with inserted 'i18n' segment if the language context is not the default language context
     * @see InternationalizationStrategy
     */
    @Nonnull
    protected String getI18nPath(String relativePath, String name) {
        String language = getRequestLanguage();
        LanguageSet languageScope = getLanguageSet();
        if (!language.equals(languageScope.getDefaultLanguage().getKey())) {
            return relativePath + I18N_PROPERTY_PATH + language + "/" + name;
        }
        return relativePath + name;
    }

    //
    // rendering ...
    //

    public String getNameHint() {
        return getResource().getName();
    }

    public String getPathHint() {
        return Element.getPathHint(getResource());
    }

    public String getTypeHint() {
        return Element.getTypeHint(getResourceType());
    }

    /**
     * @return the resource type of the resource to edit
     */
    public String getResourceType() {
        String type = (String) request.getAttribute(EditServlet.EDIT_RESOURCE_TYPE_KEY);
        if (type == null) {
            type = getResource().getResourceType();
        }
        return type;
    }

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

    @SuppressWarnings("Duplicates")
    public String getTagDebug() {
        if (tagDebug == null) {
            try {
                StringWriter writer = new StringWriter();
                writer.append("<!-- \n");
                getTagDebug(writer);
                writer.append("\n -->");
                tagDebug = writer.toString();
            } catch (IOException ioex) {
                LOG.error(ioex.getMessage(), ioex);
            }
        }
        return tagDebug;
    }

    @SuppressWarnings("Duplicates")
    protected void getTagDebug(Writer writer) throws IOException {
        writer.append("    var: '").append(getVar())
                .append("'; model: ").append(getModel().toString())
                .append("; tagClass: ").append(getClass().getName());
        writer.append("\n    resource: ").append(getModelResource(context).getPath());
    }
}
