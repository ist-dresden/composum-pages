package com.composum.pages.commons.taglib;

import com.composum.pages.commons.service.WidgetManager;
import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

/**
 * the EditWidgetTag is rendering a dialog widget as an element of the edit dialog form
 */
public abstract class AbstractWidgetTag extends AbstractWrappingTag {

    public static final String PROPERTY_RESOURCE_ATTR = "propertyResource";
    public static final String PROPERTY_PATH_ATTR = "propertyPath";

    protected String label;
    protected String property;
    protected String name;
    protected boolean i18n = false;
    protected String modelClass;

    @Override
    protected void clear() {
        modelClass = null;
        i18n = false;
        name = null;
        property = null;
        label = null;
        super.clear();
    }

    public String getLabel() {
        return i18n(label);
    }

    public void setLabel(String text) {
        label = text;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String name) {
        property = name;
    }

    public boolean isI18n() {
        return i18n;
    }

    public void setI18n(boolean val) {
        i18n = val;
    }

    public String getModelClass() {
        return modelClass;
    }

    public void setModelClass(String className) {
        modelClass = className;
    }

    /**
     * @return the name of the form input element probably with a prepended relative path and the i18n path
     */
    public String getName() {
        return getRelativePath() + getPropertyName();
    }

    public void setName(String key) {
        name = key;
    }

    /**
     * returns the resource type (the path to the component) for a widget type
     */
    protected String getWidgetResourceType(String widgetType) {
        return context.getService(WidgetManager.class).getWidgetTypeResourcePath(context, widgetType);
    }

    /**
     * returns the property resource to edit
     */
    @Override
    public Resource getModelResource(BeanContext context) {
        Resource resource = context.getAttribute(PROPERTY_RESOURCE_ATTR, Resource.class);
        if (resource == null) {
            resource = super.getModelResource(context);
        }
        return resource;
    }

    public String getRelativePath() {
        String relativePath = context.getAttribute(PROPERTY_PATH_ATTR, String.class);
        if (StringUtils.isBlank(relativePath)) {
            String resourcePath = getResource().getPath();
            String actionPath = getDialog().getResource().getPath();
            if (!resourcePath.equals(actionPath) && resourcePath.startsWith(actionPath)) {
                relativePath = resourcePath.substring(actionPath.length() + 1);
            }
        }
        if (relativePath == null) {
            relativePath = "";
        } else {
            if (StringUtils.isNotBlank(relativePath) && !relativePath.endsWith("/")) {
                relativePath += "/";
            }
        }
        return relativePath;
    }

    /**
     * @return the name of the property of the resource probably with a prepended i18n path
     */
    public String getPropertyName() {
        String propertyName = name != null ? name : getProperty();
        return isI18n() ? getI18nName(propertyName) : propertyName;
    }

    protected String getI18nName(String name) {
        if (isI18n()) {
            EditDialogTag dialog = getDialog();
            if (dialog != null) {
                return dialog.getPropertyPath(name);
            }
        }
        return name;
    }

    protected EditDialogTag getDialog() {
        return (EditDialogTag) pageContext.findAttribute(EditDialogTag.DIALOG_VAR);
    }

    protected String getDialogActionType() {
        return getDialog().getAction().getName().toLowerCase();
    }
}
