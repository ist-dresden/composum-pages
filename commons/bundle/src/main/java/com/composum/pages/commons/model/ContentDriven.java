package com.composum.pages.commons.model;

import com.composum.pages.commons.util.LinkUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Objects;

import static com.composum.pages.commons.PagesConstants.PROP_TEMPLATE;

/**
 * the abstract base class for all resource with a 'jcr:content' child which contains the resource properties
 *
 * @param <ContentType> the concrete model type of the content child
 */
public abstract class ContentDriven<ContentType extends ContentModel> extends AbstractModel {

    protected Boolean valid;
    protected ContentType content;

    private transient String template;
    private transient String editUrl;

    // initializer extensions

    protected void initializeWithResource(Resource resource) {
        if (JcrConstants.JCR_CONTENT.equals(resource.getName())) {
            resource = Objects.requireNonNull(resource.getParent());
        }
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

    public String getTemplatePath() {
        if (template == null) {
            content.getProperty(PROP_TEMPLATE, null, "");
        }
        return template;
    }

    protected String getCssBaseType() {
        return content.getCssBaseType();
    }

    // editing

    /**
     * Returns the URL to the edit frame (/bin/pages.html) to edit the content of this bean.
     *
     * @see LinkUtil#getUrl(SlingHttpServletRequest, String)
     */
    public String getEditUrl() {
        if (editUrl == null) {
            SlingHttpServletRequest request = context.getRequest();
            editUrl = LinkUtil.getUrl(request, "/bin/pages.html" + getPath(), null, null);
        }
        return editUrl;
    }

    // get properties from model with fallback to the content child

    @Override
    public <T> T getProperty(@Nonnull String key, @Nonnull Class<T> type) {
        T value = super.getProperty(key, type);
        if (value == null && content.resource != resource) {
            value = content.getProperty(key, type);
        }
        return value;
    }

    @Override
    public <T> T getProperty(@Nonnull String key, Locale locale, @Nonnull Class<T> type) {
        T value = super.getProperty(key, locale, type);
        if (value == null && content.resource != resource) {
            value = content.getProperty(key, locale, type);
        }
        return value;
    }

    @Override
    public <T> T getInherited(@Nonnull String key, @Nonnull Class<T> type) {
        T value = super.getInherited(key, type);
        if (value == null && content.resource != resource) {
            value = content.getInherited(key, type);
        }
        return value;
    }

    @Override
    public <T> T getInherited(@Nonnull String key, Locale locale, @Nonnull Class<T> type) {
        T value = super.getInherited(key, locale, type);
        if (value == null && content.resource != resource) {
            value = content.getInherited(key, locale, type);
        }
        return value;
    }
}
