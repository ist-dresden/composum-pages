package com.composum.pages.commons.taglib;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.properties.Language;
import com.composum.pages.commons.model.properties.Languages;
import com.composum.pages.commons.util.TagCssClasses;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.composum.pages.commons.servlet.EditServlet.EDIT_RESOURCE_KEY;
import static com.composum.pages.commons.servlet.EditServlet.EDIT_RESOURCE_TYPE_KEY;
import static com.composum.pages.commons.taglib.AbstractPageTag.STAGE_COMPONENT_BASE;
import static com.composum.pages.commons.taglib.ElementTag.PAGES_EDIT_DATA;
import static com.composum.pages.commons.taglib.ElementTag.PAGES_EDIT_DATA_NAME;
import static com.composum.pages.commons.taglib.ElementTag.PAGES_EDIT_DATA_PATH;
import static com.composum.pages.commons.taglib.ElementTag.PAGES_EDIT_DATA_TYPE;
import static com.composum.pages.commons.util.TagCssClasses.cssOfType;
import static com.composum.platform.models.annotations.InternationalizationStrategy.I18NFOLDER.I18N_PROPERTY_PATH;

/**
 * the EditDialogTag creates the HTML code for an edit dialog of a component
 */
@SuppressWarnings("JavaDoc")
public class EditDialogTag extends AbstractWrappingTag {

    public static final String DIALOG_VAR = "dialog";
    public static final String DIALOG_CSS_VAR = DIALOG_VAR + "CssBase";

    public static final String DEFAULT_SELECTOR = "edit";
    public static final String SELECTOR_CREATE = "create";
    public static final List<String> KNOWN_SELECTORS = Arrays.asList(
            DEFAULT_SELECTOR,
            SELECTOR_CREATE,
            "new",
            "delete",
            "generic",
            "wizard"
    );

    public static final String TYPE_NONE = "none";

    public static final String DEFAULT_CSS_BASE = "composum-pages-stage-edit-dialog";

    public static final String DIALOG_PATH = "/edit/dialog";

    public static final String SLING_POST_SERVLET_ACTION = "Sling-POST";
    public static final String CUSTOM_POST_SERVLET_ACTION = "Custom-POST";

    public static final String ATTR_SUBMIT_LABEL = "submitLabel";
    public static final String DEFAULT_SUBMIT_LABEL = "Submit";

    public static final String ATTR_INITIAL_ALERT = "alert-";

    public static final String PAGES_EDIT_SUCCESS_EVENT = PAGES_EDIT_DATA + "-success";

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

    private transient String editPath;

    protected String submit;
    protected String submitLabel;
    private transient EditDialogAction action;

    protected String successEvent;

    protected String alertKey;
    protected String alertText;

    @Override
    protected void clear() {
        alertText = null;
        alertKey = null;
        successEvent = null;
        action = null;
        submit = null;
        submitLabel = null;
        editPath = null;
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

    /**
     * Determines the resource to edit by the form of the dialog to create by this tag. This resource is mainly
     * determined by a 'EDIT_RESOURCE_KEY' reuqest attribute which is declared by the edit servlet during the dialog
     * load request; if the dialog is loaded by a dialog component URL the edit resource is expected as the URLs suffix.
     *
     * @param context the current request context
     * @return the resource to edit
     */
    @Override
    public Resource getModelResource(BeanContext context) {
        if (editResource == null) {
            editResource = request.getAttribute(EDIT_RESOURCE_KEY);
        }
        if (editResource == null) {
            String suffix = request.getRequestPathInfo().getSuffix();
            if (StringUtils.isNotBlank(suffix) && !"/".equals(suffix)) {
                editResource = request.getResourceResolver().getResource(suffix);
                if (editResource != null) {
                    request.setAttribute(EDIT_RESOURCE_KEY, editResource);
                }
            }
        }
        return editResource instanceof Resource ? ((Resource) editResource) : super.getModelResource(context);
    }

    public String getEditPath() {
        if (editPath == null) {
            Resource editResource = getModelResource(context);
            editPath = editResource != resource ? editResource.getPath() : "";
        }
        return editPath;
    }

    /**
     * @return the result of 'getModelResource()' as the resource to drive this tag
     */
    @Override
    public Resource getResource() {
        return getModelResource(context);
    }

    // tag attributes

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String id) {
        tagId = id;
    }

    public String getTitle() {
        if (titleValue == null) {
            titleValue = i18n(eval(title, "Edit Element"));
        }
        return titleValue;
    }

    public void setTitle(String text) {
        title = text;
    }

    /**
     * tag attribute - switch the language context hint on/off
     *
     * @param languageContext boolean; show the hint (default: true)
     */
    public void setLanguageContext(boolean languageContext) {
        this.languageContext = languageContext;
    }

    public boolean isHasLanguageContext() {
        return languageContext;
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

    // create: 'sling:resourceType'

    /**
     * the resource type of a new element created by a dialog (hidden 'sling:resourceType' property value)
     */
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

    /**
     * if this returns 'true' the hidden resource type property value must be inserted in the form
     */
    public boolean isUseResourceType() {
        return (!TYPE_NONE.equalsIgnoreCase(resourceType)
                && (StringUtils.isNotBlank(resourceType) || SELECTOR_CREATE.equalsIgnoreCase(getSelector())
                || ResourceUtil.isNonExistingResource(getModelResource(context))));
    }

    // create: 'jcr:primaryType'

    /**
     * the primary type of a new element created by a dialog (hidden 'jcr:primaryType' property value)
     */
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

    /**
     * if this returns 'true' the hidden primary type property value must be inserted in the form
     */
    public boolean isUsePrimaryType() {
        return (SELECTOR_CREATE.equalsIgnoreCase(getSelector()) || primaryType != null)
                && !TYPE_NONE.equalsIgnoreCase(primaryType);
    }

    // the dialog variation selector

    /**
     * returns the first not empty value of the cascade, as key to select the right dialog snippets:
     * <ul>
     * <li>the eval() result of the declared selector</li>
     * <li>the selector string of the request</li>
     * <li>the default 'edit' selector (main snippet)</li>
     * </ul>
     *
     * @see /libs/composum/pages/stage/edit/dialog/...
     */
    public String getSelector() {
        if (selectorValue == null) {
            selectorValue = eval(selector, "");
            if (StringUtils.isBlank(selectorValue)) {
                selectorValue = request.getRequestPathInfo().getSelectorString();
                if (StringUtils.isNotBlank(selectorValue) && !KNOWN_SELECTORS.contains(selectorValue)) {
                    selectorValue = null;
                }
                if (StringUtils.isBlank(selectorValue)) {
                    selectorValue = DEFAULT_SELECTOR;
                }
            }
        }
        return selectorValue;
    }

    // initial alert message

    public boolean isAlertSet() {
        return StringUtils.isNotBlank(alertKey);
    }

    public String getAlertKey() {
        return isAlertSet() ? alertKey : "warning hidden";
    }

    public String getAlertText() {
        return isAlertSet() ? i18n(alertText) : "";
    }

    /**
     * filters dynamic attributes for special purposes:
     * <ul>
     * <li>initial 'alert' settings for initial hints</li>
     * <li>'submit' button label for the 'generic' dialog</li>
     * </ul>
     */
    @Override
    protected boolean acceptDynamicAttribute(String key, Object value) throws JspException {
        if (key.startsWith(ATTR_INITIAL_ALERT)) {
            alertKey = key.substring(ATTR_INITIAL_ALERT.length());
            alertText = (String) value;
            return false;
        } else if (ATTR_SUBMIT_LABEL.equals(key)) {
            submitLabel = (String) value;
            return false;
        } else {
            return super.acceptDynamicAttribute(key, value);
        }
    }

    /**
     * tag attribute - defines the optional selector for the rendering of the dialog component;
     * this selector string is mainly used to select the right dialog 'start' and 'end' snippets
     *
     * @param selector the selector key
     * @see /libs/composum/pages/stage/edit/dialog/...
     */
    public void setSelector(String selector) {
        this.selector = selector;
    }

    // dialog submit action ...

    /**
     * @return the localized submit button label mainly for the generic dialog type (selector)
     */
    public String getSubmitLabel() {
        return i18n(StringUtils.isNotBlank(submitLabel) ? submitLabel : DEFAULT_SUBMIT_LABEL);
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

    // post dialog event

    public String getSuccessEvent() {
        return successEvent;
    }

    public void setSuccessEvent(String eventKey) {
        this.successEvent = eventKey;
    }

    //
    //
    //

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

    @Override
    protected void collectCssClasses(TagCssClasses.CssSet collection) {
        super.collectCssClasses(collection);
        collection.add(getCssBase() + "_action_" + getAction().getName().toLowerCase());
        String selector = getSelector();
        if (StringUtils.isNotBlank(selector)) {
            collection.add(getCssBase() + "_selector_" + selector);
        }
        collection.add(getCssBase() + "_action_" + getAction().getName().toLowerCase());
        collection.add("dialog");
        collection.add("modal");
        collection.add("fade");
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
        if (StringUtils.isNotBlank(value = getSuccessEvent())) {
            attributeSet.put(PAGES_EDIT_SUCCESS_EVENT, value);
        }
    }

    // tag rendering

    protected String getSnippetResourceType() {
        return STAGE_COMPONENT_BASE + DIALOG_PATH;
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
    protected void renderTagStart() throws IOException {
        includeSnippet(getSnippetResourceType(), getSelector() + "-dialog-start");
    }

    @Override
    protected void renderTagEnd() throws IOException {
        includeSnippet(getSnippetResourceType(), getSelector() + "-dialog-end");
    }

    @Override
    protected void finishTagEnd() {
    }

    // dialog submit action ...

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
