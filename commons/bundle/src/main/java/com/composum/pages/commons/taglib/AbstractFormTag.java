package com.composum.pages.commons.taglib;

import com.composum.pages.commons.util.LinkUtil;
import com.composum.pages.commons.util.TagCssClasses;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.Map;

import static com.composum.pages.commons.taglib.AbstractPageTag.COMMONS_COMPONENT_BASE;

/**
 * the EditDialogTag creates the HTML code for an edit dialog of a component
 */
@SuppressWarnings("JavaDoc")
public abstract class AbstractFormTag extends AbstractWrappingTag {

    public static final String SLING_POST_SERVLET_ACTION = "Sling-POST";

    public static final String ATTR_INITIAL_ALERT = "alert-";

    protected String tagId;

    private transient FormAction formAction;

    protected String validation;
    protected String validationValue;

    protected String alertKey;
    protected String alertText;

    protected Object disabled;
    protected Boolean disabledValue;

    @Override
    protected void clear() {
        disabledValue = null;
        disabled = null;
        alertText = null;
        alertKey = null;
        validationValue = null;
        validation = null;
        formAction = null;
        tagId = null;
        super.clear();
    }

    // tag attributes

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String id) {
        tagId = id;
    }

    // form disabling

    /**
     * the general 'disabled' expression for the tags scope
     */
    public void setDisabled(Object value) {
        disabled = value;
    }

    protected boolean hasDisabledAttribute() {
        return disabled != null;
    }

    /**
     * the 'disabled' expression value
     */
    protected boolean getDisabledValue() {
        if (disabledValue == null) {
            disabledValue = eval(disabled, disabled instanceof Boolean ? (Boolean) disabled : Boolean.FALSE);
        }
        return disabledValue;
    }

    public boolean isDisabledSet() {
        return hasDisabledAttribute() ? getDisabledValue() : false;
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
     * </ul>
     */
    @Override
    protected boolean acceptDynamicAttribute(String key, Object value) throws JspException {
        if (key.startsWith(ATTR_INITIAL_ALERT)) {
            alertKey = key.substring(ATTR_INITIAL_ALERT.length());
            alertText = (String) value;
            return false;
        } else {
            return super.acceptDynamicAttribute(key, value);
        }
    }

    // validation request

    public String getValidation() {
        return validation;
    }

    public String getValidationValue() {
        if (validation != null) {
            if (validationValue == null) {
                validationValue = eval(validation, "");
            }
        }
        return validationValue;
    }

    public void setValidation(String requestRule) {
        this.validation = requestRule;
    }

    @Override
    protected void collectAttributes(Map<String, Object> attributeSet) {
        super.collectAttributes(attributeSet);
        String value;
        if (StringUtils.isNotBlank(value = getTagId())) {
            attributeSet.put(TAG_ID, value);
        }
    }

    public interface FormAction {

        String getName();

        String getUrl();

        String getMethod();

        String getEncType();

        String getPropertyPath(String relativePath, String name);
    }

    public class SlingPostServletAction implements FormAction {

        public String getName() {
            return SLING_POST_SERVLET_ACTION;
        }

        public String getUrl() {
            String url = request.getContextPath() + LinkUtil.encodePath(getResource().getPath());
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

        public String getPropertyPath(String relativePath, String name) {
            return getI18nPath(relativePath, name);
        }
    }

    public FormAction getFormAction() {
        if (formAction == null) {
            formAction = getDefaultAction();
        }
        return formAction;
    }

    public FormAction getDefaultAction() {
        return null;
    }
}
