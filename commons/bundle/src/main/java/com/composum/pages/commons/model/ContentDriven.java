package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.sling.api.resource.Resource;

import java.util.Locale;

/**
 * the abstract base class for all resource with a 'jcr:content' child which contains the resource properties
 * @param <ContentType> the concrete model type of the content child
 */
public abstract class ContentDriven<ContentType extends ContentModel> extends AbstractModel {

    protected Boolean valid;
    protected ContentType content;

    // initializer extensions

    protected void initializeWithResource(Resource resource) {
        super.initializeWithResource(resource);
        Resource contentRes = resource.getChild(ResourceUtil.CONTENT_NODE);
        if (contentRes == null || ResourceUtil.isNonExistingResource(contentRes)) {
            contentRes = resource;
            valid = false;
        }
        content = createContentModel(context, contentRes);
        content.parent = this;
    }

    protected abstract ContentType createContentModel(BeanContext context, Resource contentResource);

    public ContentType getContent() {
        return content;
    }

    public boolean isValid() {
        if (valid == null) {
            valid = resource != null && !ResourceUtil.isNonExistingResource(resource);
        }
        return valid;
    }

    protected String getCssBaseType() {
        return content.getCssBaseType();
    }

    // get properties from model with fallback to the content child

    @Override
    public <T> T getProperty(String key, Class<T> type) {
        T value = super.getProperty(key, type);
        if (value == null && content.resource != resource) {
            value = content.getProperty(key, type);
        }
        return value;
    }

    @Override
    public <T> T getProperty(String key, Locale locale, Class<T> type) {
        T value = super.getProperty(key, locale, type);
        if (value == null && content.resource != resource) {
            value = content.getProperty(key, locale, type);
        }
        return value;
    }

    @Override
    public <T> T getInherited(String key, Class<T> type) {
        T value = super.getInherited(key, type);
        if (value == null && content.resource != resource) {
            value = content.getInherited(key, type);
        }
        return value;
    }

    @Override
    public <T> T getInherited(String key, Locale locale, Class<T> type) {
        T value = super.getInherited(key, locale, type);
        if (value == null && content.resource != resource) {
            value = content.getInherited(key, locale, type);
        }
        return value;
    }
}
