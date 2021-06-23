package com.composum.pages.commons.taglib;

import com.composum.pages.commons.util.TagCssClasses;
import com.composum.sling.core.util.SlingUrl;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.composum.pages.commons.taglib.AbstractPageTag.COMMONS_COMPONENT_BASE;

/**
 * the EditDialogTag creates the HTML code for an edit dialog of a component
 */
@SuppressWarnings("JavaDoc")
public class WidgetFormTag extends AbstractFormTag {

    public static final String FORM_VAR = "form";
    public static final String FORM_CSS_VAR = FORM_VAR + "CSS";

    public static final String DEFAULT_CSS_BASE = "composum-pages-widgets-form";

    public static final String FORM_PATH = "/widget/form";

    public static final String GENERIC_FORM_ACTION = "Gereric-Form";

    public static final Map<String, String> FORM_TYPES = new HashMap<>() {{
        put("sling", SLING_POST_SERVLET_ACTION);
        put("generic", GENERIC_FORM_ACTION);
    }};

    protected String formType;
    protected String action;
    protected String method;
    protected String encType;

    @Override
    protected void clear() {
        encType = null;
        method = null;
        action = null;
        formType = null;
        super.clear();
    }

    // tag attributes

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isPostMethod() {
        String method = getMethod();
        return method == null || "POST".equalsIgnoreCase(method);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEncType() {
        return encType;
    }

    public void setEncType(String encType) {
        this.encType = encType;
    }

    // tag rendering

    @Override
    protected void collectCssClasses(TagCssClasses.CssSet collection) {
        super.collectCssClasses(collection);
        collection.add(getCssBase() + "_action_" + getFormAction().getName().toLowerCase());
    }

    protected String getSnippetResourceType() {
        return COMMONS_COMPONENT_BASE + FORM_PATH;
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
        setAttribute(EditDialogTag.DIALOG_VAR, this, PageContext.REQUEST_SCOPE);
        setAttribute(FORM_VAR, this, PageContext.REQUEST_SCOPE);
        if (StringUtils.isNotBlank(cssBase)) {
            setAttribute(FORM_CSS_VAR, cssBase, PageContext.REQUEST_SCOPE);
        }
    }

    @Override
    protected void renderTagStart() throws IOException {
        includeSnippet(getSnippetResourceType(), "widget-form-start");
    }

    @Override
    protected void renderTagEnd() throws IOException {
        includeSnippet(getSnippetResourceType(), "widget-form-end");
    }

    // form submit action ...

    public FormAction getDefaultAction() {
        String type = getFormType();
        if (type != null) {
            type = FORM_TYPES.get(type);
        }
        if (type == null) {
            type = isPostMethod() ? SLING_POST_SERVLET_ACTION : GENERIC_FORM_ACTION;
        }
        switch (type) {
            case GENERIC_FORM_ACTION:
                return new GenericFormAction();
            default:
                return new SlingPostServletAction(getAction());
        }
    }

    public class GenericFormAction implements FormAction {

        @Override
        @Nonnull
        public String getName() {
            return GENERIC_FORM_ACTION;
        }

        @Override
        @Nonnull
        public String getUrl() {
            return new SlingUrl(request, eval(WidgetFormTag.this.getAction(), "")).getUrl();
        }

        @Override
        @Nonnull
        public String getMethod() {
            return eval(WidgetFormTag.this.getMethod(), "POST");
        }

        @Override
        @Nonnull
        public String getEncType() {
            return eval(WidgetFormTag.this.getEncType(), "multipart/form-data");
        }

        @Override
        @Nonnull
        public String getPropertyPath(String relativePath, String name) {
            return getI18nPath(relativePath, name);
        }
    }
}
