package com.composum.pages.commons.taglib;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.properties.Language;
import com.composum.pages.commons.model.properties.Languages;
import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.Map;

import static com.composum.pages.commons.model.AbstractModel.I18N_PROPERTY_PATH;
import static com.composum.pages.commons.model.AbstractModel.addCssClass;
import static com.composum.pages.commons.model.AbstractModel.cssOfType;
import static com.composum.pages.commons.servlet.EditServlet.EDIT_RESOURCE_KEY;
import static com.composum.pages.commons.servlet.EditServlet.EDIT_RESOURCE_TYPE_KEY;
import static com.composum.pages.commons.taglib.AbstractPageTag.STAGE_COMPONENT_BASE;
import static com.composum.pages.commons.taglib.ElementTag.PAGES_EDIT_DATA_NAME;
import static com.composum.pages.commons.taglib.ElementTag.PAGES_EDIT_DATA_PATH;
import static com.composum.pages.commons.taglib.ElementTag.PAGES_EDIT_DATA_TYPE;

/**
 * the EditDialogTag creates the HTML code for an edit dialog of a component
 */
public class EditDialogTag extends AbstractWrappingTag {

    public static final String DIALOG_VAR = "dialog";
    public static final String DIALOG_CSS_VAR = DIALOG_VAR + "CssBase";

    public static final String SELECTOR_CREATE = "create";

    public static final String TYPE_NONE = "none";

    public static final String DEFAULT_CSS_BASE = "composum-pages-stage-edit-dialog";

    public static final String DIALOG_PATH = "/edit/dialog";
    public static final String DEFAULT_SELECTOR = "edit";

    public static final String SLING_POST_SERVLET_ACTION = "Sling-POST";
    public static final String CUSTOM_POST_SERVLET_ACTION = "Custom-POST";

    private transient String dialogId;
    protected String tagId;

    protected String title;
    protected String titleValue;

    protected String selector;
    protected String selectorValue;

    protected boolean languageContext = true;

    protected String resourceType;
    protected String primaryType;
    private transient String defaultPrimaryType;

    protected String submit;
    private transient EditDialogAction action;

    protected String alertKey;
    protected String alertText;

    @Override
    protected void clear() {
        alertText = null;
        alertKey = null;
        action = null;
        submit = null;
        defaultPrimaryType = null;
        primaryType = null;
        resourceType = null;
        selectorValue = null;
        selector = null;
        titleValue = null;
        title = null;
        tagId = null;
        dialogId = null;
        super.clear();
    }

    @Override
    public Resource getModelResource(BeanContext context) {
        if (editResource == null) {
            editResource = request.getAttribute(EDIT_RESOURCE_KEY);
        }
        return editResource instanceof Resource ? ((Resource) editResource) : super.getModelResource(context);
    }

    @Override
    public Resource getResource() {
        return getModelResource(context);
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String id) {
        tagId = id;
    }

    public String getTitle() {
        if (titleValue == null) {
            titleValue = eval(title, "Edit Element");
        }
        return i18n(titleValue);
    }

    public void setTitle(String text) {
        title = text;
    }

    public void setLanguageContext(boolean languageContext) {
        this.languageContext = languageContext;
    }

    public boolean isHasLanguageContext() {
        return languageContext;
    }

    public String getSubmitLabel() {
        return dynamicAttributes.getAttribute("submitLabel", "Submit");
    }

    public String getCreateNameHint() {
        String hint = null;
        String type = getResourceType();
        if (StringUtils.isNotBlank(type)) {
            hint = type.substring(type.lastIndexOf("/") + 1);
        }
        if (StringUtils.isBlank(hint)) {
            type = getPrimaryType();
            if (StringUtils.isNotBlank(type)) {
                hint = type.substring(type.lastIndexOf(":") + 1);
            }
        }
        return StringUtils.isNotBlank(hint) ? hint : "element";
    }

    public String getResourceType() {
        return StringUtils.isNotBlank(resourceType) ? resourceType : getDefaultResourceType();
    }

    public void setResourceType(String type) {
        resourceType = type;
    }

    public String getDefaultResourceType() {
        String requestedType = (String) request.getAttribute(EDIT_RESOURCE_TYPE_KEY);
        return requestedType != null ? requestedType : "";
    }

    public boolean isUseResourceType() {
        return (SELECTOR_CREATE.equalsIgnoreCase(getSelector()) || resourceType != null)
                && !TYPE_NONE.equalsIgnoreCase(resourceType);
    }

    public String getPrimaryType() {
        return StringUtils.isNotBlank(primaryType) ? primaryType : getDefaultPrimaryType();
    }

    public void setPrimaryType(String type) {
        primaryType = type;
    }

    public String getDefaultPrimaryType() {
        if (defaultPrimaryType == null) {
            defaultPrimaryType = PagesConstants.ComponentType.getPrimaryType(
                    PagesConstants.ComponentType.typeOf(resourceResolver, getResource(), getResourceType()));
        }
        return defaultPrimaryType;
    }

    public boolean isUsePrimaryType() {
        return (SELECTOR_CREATE.equalsIgnoreCase(getSelector()) || primaryType != null)
                && !TYPE_NONE.equalsIgnoreCase(primaryType);
    }

    public String getSelector() {
        if (selectorValue == null) {
            selectorValue = eval(selector, "");
            if (StringUtils.isBlank(selectorValue)) {
                selectorValue = request.getRequestPathInfo().getSelectorString();
                if (StringUtils.isBlank(selectorValue)) {
                    selectorValue = DEFAULT_SELECTOR;
                }
            }
        }
        return selectorValue;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public String getPropertyPath(String name) {
        return getAction().getPropertyPath(name);
    }

    protected String getI18nPath(String name) {
        String path = name;
        Languages languages = getLanguages();
        if (languages != null) {
            Language defaultLanguage = languages.getDefaultLanguage();
            if (!defaultLanguage.isCurrent()) {
                Language language = languages.getLanguage();
                path = I18N_PROPERTY_PATH + language.getKey() + "/" + path;
            }
        }
        return path;
    }

    public String getDialogId() {
        if (dialogId == null) {
            String type = resource.getResourceType();
            dialogId = cssOfType(type);
        }
        return dialogId;
    }

    protected String getSnippetResourceType() {
        return STAGE_COMPONENT_BASE + DIALOG_PATH;
    }

    @Override
    protected void collectCssClasses(java.util.List collection) {
        super.collectCssClasses(collection);
        addCssClass(collection, getCssBase() + "_action_" + getAction().getName().toLowerCase());
        addCssClass(collection, "dialog");
        addCssClass(collection, "modal");
        addCssClass(collection, "fade");
    }

    @Override
    protected void collectAttributes(Map<String, String> attributeSet) {
        String value;
        if (StringUtils.isNotBlank(value = getTagId())) {
            attributeSet.put(TAG_ID, value);
        }
        super.collectAttributes(attributeSet);
        attributeSet.put("role", "dialog");
        attributeSet.put("aria-hidden", "true");
        Resource resourceToEdit = getResource();
        if (resourceToEdit != null) {
            attributeSet.put(PAGES_EDIT_DATA_NAME, resourceToEdit.getName());
            attributeSet.put(PAGES_EDIT_DATA_PATH, resourceToEdit.getPath());
            attributeSet.put(PAGES_EDIT_DATA_TYPE, getResourceType());
        }
    }

    /**
     * filter dynamic attributes for special purposes
     * <ul>
     *     <li>initial 'alert' settings for initial hints</li>
     * </ul>
     */
    @Override
    protected boolean acceptDynamicAttribute(String key, Object value) throws JspException {
        if (key.startsWith("alert-")) {
            if (value instanceof String) {
                alertKey = key;
                alertText = (String) value;
            }
            return false;
        } else {
            return super.acceptDynamicAttribute(key, value);
        }
    }

    public boolean isAlertSet() {
        return StringUtils.isNotBlank(alertKey);
    }

    public String getAlertKey() {
        return isAlertSet() ? alertKey : "alert-warning alert-hidden";
    }

    public String getAlertText() {
        return isAlertSet() ? i18n(alertText) : "";
    }

    @Override
    public int doStartTag() throws JspException {
        if (StringUtils.isBlank(cssBase)) {
            cssBase = DEFAULT_CSS_BASE;
        }
        return super.doStartTag();
    }

    @Override
    protected void prepareTagStart() {
        setAttribute(DIALOG_VAR, this, PageContext.REQUEST_SCOPE);
        if (StringUtils.isNotBlank(cssBase)) {
            setAttribute(DIALOG_CSS_VAR, cssBase, PageContext.REQUEST_SCOPE);
        }
    }

    @Override
    protected void renderTagStart() throws JspException, IOException {
        includeSnippet(getSnippetResourceType(), getSelector() + "-dialog-start");
    }

    @Override
    protected void renderTagEnd() throws JspException, IOException {
        includeSnippet(getSnippetResourceType(), getSelector() + "-dialog-end");
    }

    @Override
    protected void finishTagEnd() {
    }

    public void setSubmit(String actionKey) {
        submit = actionKey;
        switch (actionKey) {
            case "SlingPostServlet":
            case SLING_POST_SERVLET_ACTION:
                action = new SlingPostServletAction();
                break;
            default:
                action = new CustomPostAction(actionKey);
                break;
        }
    }

    public EditDialogAction getDefaultAction() {
        return new SlingPostServletAction();
    }

    public EditDialogAction getAction() {
        if (action == null) {
            action = getDefaultAction();
        }
        return action;
    }

    public interface EditDialogAction {

        String getName();

        String getUrl();

        String getMethod();

        String getEncType();

        String getPropertyPath(String name);
    }

    public class SlingPostServletAction implements EditDialogAction {

        public String getName() {
            return SLING_POST_SERVLET_ACTION;
        }

        public String getUrl() {
            String url = request.getContextPath() + getResource().getPath();
            String nameParam = request.getParameter("name");
            if ("*".equals(nameParam)) {
                // append a '/*' on element creation
                if (!url.endsWith(nameParam)) {
                    url += "/" + nameParam;
                }
            }
            return url;
        }

        public String getMethod() {
            return "POST";
        }

        public String getEncType() {
            return "multipart/form-data";
        }

        public String getPropertyPath(String name) {
            return getI18nPath(name);
        }
    }

    public class CustomPostAction implements EditDialogAction {

        protected final String uri;

        public CustomPostAction(String uri) {
            this.uri = uri;
        }

        public String getName() {
            return CUSTOM_POST_SERVLET_ACTION;
        }

        public String getUrl() {
            return request.getContextPath() + eval(uri, "/");
        }

        public String getMethod() {
            return "POST";
        }

        public String getEncType() {
            return "multipart/form-data";
        }

        public String getPropertyPath(String name) {
            return getI18nPath(name);
        }
    }
}
