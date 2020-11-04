package com.composum.pages.commons.model;

import com.composum.pages.commons.util.LinkUtil;
import com.composum.pages.commons.util.ResolverUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.SlingResourceUtil;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.jcr.RepositoryException;
import java.util.Locale;
import java.util.Objects;

import static com.composum.pages.commons.PagesConstants.PAGES_FRAME_PATH;
import static com.composum.pages.commons.PagesConstants.PROP_TEMPLATE;

/**
 * the abstract base class for all resource with a 'jcr:content' child which contains the resource properties
 *
 * @param <ContentType> the concrete model type of the content child
 */
public abstract class ContentDriven<ContentType extends ContentModel> extends AbstractModel {

    private static final Logger LOG = LoggerFactory.getLogger(ContentDriven.class);

    protected Boolean valid;
    protected ContentType content;

    private transient Resource template;
    private transient String templatePath;
    private transient String editUrl;

    private transient ContentVersion.StatusModel releaseStatus;
    private transient PlatformVersionsService platformVersionsService;

    // initializer extensions

    protected void initializeWithResource(@Nonnull Resource resource) {
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
        if (templatePath == null) {
            templatePath = determineTemplatePath();
        }
        return templatePath;
    }

    public String determineTemplatePath() {
        return content.getProperty(PROP_TEMPLATE, null, "");
    }

    public Resource getTemplate() {
        if (template == null) {
            String templatePath = getTemplatePath();
            if (StringUtils.isNotBlank(templatePath)) {
                template = ResolverUtil.getTemplate(getContext().getResolver(), templatePath);
            }
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
            editUrl = LinkUtil.getUrl(request, PAGES_FRAME_PATH + ".html" + getPath(), null, null);
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

    // release

    public boolean isLocked() {
        return getContent().isLocked();
    }

    public String getLockOwner() {
        return getContent().getLockOwner();
    }

    public boolean isCheckedOut() {
        return getContent().isCheckedOut();
    }

    public ContentVersion.StatusModel getReleaseStatus() {
        if (releaseStatus == null) {
            try {
                PlatformVersionsService.Status status = getPlatformVersionsService().getStatus(getResource(), null);
                if (status == null) { // rare strange case - needs to be investigated.
                    LOG.warn("No release status for {}", SlingResourceUtil.getPath(getResource()));
                }
                releaseStatus = new ContentVersion.StatusModel(status);
            } catch (RepositoryException ex) {
                LOG.error("Error calculating status for " + SlingResourceUtil.getPath(getResource()), ex);
            }
        }
        if (releaseStatus != null && releaseStatus.releaseStatus == null)
            return null;
        return releaseStatus;
    }

    protected PlatformVersionsService getPlatformVersionsService() {
        if (platformVersionsService == null) {
            platformVersionsService = context.getService(PlatformVersionsService.class);
        }
        return platformVersionsService;
    }
}
