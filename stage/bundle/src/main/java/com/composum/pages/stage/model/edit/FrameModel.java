package com.composum.pages.stage.model.edit;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.Component;
import com.composum.pages.commons.model.ContentDriven;
import com.composum.pages.commons.model.ContentVersion;
import com.composum.pages.commons.model.GenericModel;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.service.PagesTenantSupport;
import com.composum.pages.commons.service.ResourceManager;
import com.composum.pages.commons.service.SiteManager;
import com.composum.pages.commons.util.ResolverUtil;
import com.composum.pages.commons.util.ResourceTypeUtil;
import com.composum.pages.commons.util.TagCssClasses;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.CoreConfiguration;
import com.composum.sling.core.util.XSS;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.composum.pages.commons.PagesConstants.PAGES_FRAME_PATH;
import static com.composum.pages.commons.model.AbstractModel.CSS_BASE_TYPE_RESTRICTION;
import static com.composum.pages.commons.util.ResourceTypeUtil.EDIT_TILE_PATH;
import static com.composum.sling.core.servlet.AbstractServiceServlet.PARAM_TYPE;

/**
 * a model of a frame component which is an editing component of an element; this model is a wrapper for the element to edit
 */
public class FrameModel extends GenericModel {

    // probably preset during resource determination
    protected transient String resourceType;

    private transient Resource typeResource;
    private transient PagesConstants.ComponentType componentType;
    private transient Component component;

    private transient DisplayMode.Value displayMode;

    // OSGi services

    private transient SiteManager siteManager;
    private transient PageManager pageManager;
    private transient ResourceManager resourceManager;

    @Override
    public PagesConstants.ComponentType getComponentType() {
        if (componentType == null) {
            BeanContext context = delegate.getContext();
            componentType = PagesConstants.ComponentType.typeOf(context.getResolver(), getResource(), getType());
        }
        return componentType;
    }

    /**
     * @return the resource of the frame element itself instead of the element to edit
     */
    public Resource getFrameResource() {
        return getContext().getResource();
    }

    /**
     * @return the CSS base of the frame element itself not of the delegate
     */
    @Nonnull
    @Override
    public String getCssBase() {
        Resource resource = getFrameResource();
        String type = CSS_BASE_TYPE_RESTRICTION.accept(resource) ? resource.getResourceType() : null;
        return StringUtils.isNotBlank(type) ? TagCssClasses.cssOfType(type) : "";
    }

    /**
     * @return the resource type retrieved from the URL 'type' parameter ot the resource type element to edit
     */
    @Nonnull
    @Override
    public String getType() {
        if (resourceType == null) {
            BeanContext context = delegate.getContext();
            SlingHttpServletRequest request = context.getRequest();
            resourceType = request.getParameter(PARAM_TYPE);
            if (StringUtils.isBlank(resourceType)) {
                resourceType = super.getType();
            }
        }
        return resourceType;
    }

    @Override
    public Component getComponent() {
        if (component == null) {
            Resource typeResource = getTypeResource();
            component = new Component(getContext(), typeResource != null ? typeResource : getResource());
        }
        return component;
    }

    /**
     * retrieves the element resource focused by the frame element for editing
     */
    @Override
    protected Resource determineDelegateResource(BeanContext context, Resource resource) {
        String path = getDelegatePath(context);
        return context.getResolver().resolve(path);
    }

    /**
     * retrieves the path of the element to handle by the frame element using the suffix of the request
     */
    public String getDelegatePath(BeanContext context) {
        SlingHttpServletRequest request = context.getRequest();
        String delegatePath = XSS.filter(request.getRequestPathInfo().getSuffix());
        if (StringUtils.isBlank(delegatePath)) {
            delegatePath = "/";
        }
        return delegatePath;
    }

    public Resource getTypeResource() {
        if (typeResource == null) {
            Resource resource = getResource();
            typeResource = ResolverUtil.getResourceType(resource, getType());
        }
        return typeResource;
    }

    public String getTypePath() {
        Resource type = getTypeResource();
        return type != null ? type.getPath() : "";
    }

    // view mode

    public boolean isEditMode() {
        DisplayMode.Value mode = getDisplayMode();
        return mode == DisplayMode.Value.EDIT || mode == DisplayMode.Value.DEVELOP;
    }

    public boolean isDevelopMode() {
        return getDisplayMode() == DisplayMode.Value.DEVELOP;
    }

    public DisplayMode.Value getDisplayMode() {
        if (displayMode == null) {
            displayMode = DisplayMode.requested(getContext());
        }
        return displayMode;
    }

    // Tile rendering

    public String getTileResourceType() {
        return ResourceTypeUtil.getSubtypePath(getContext().getResolver(), getResource(), getPath(), EDIT_TILE_PATH, null);
    }

    // Versionable

    public boolean isVersionable() {
        Model model = getDelegate();
        if (model instanceof ContentDriven) {
            ContentDriven<?> content = (ContentDriven<?>) model;
            return content.isVersionable();
        }
        return false;
    }

    public ContentVersion.StatusModel getReleaseStatus() {
        Model model = getDelegate();
        if (model instanceof ContentDriven) {
            ContentDriven<?> content = (ContentDriven<?>) model;
            return content.getReleaseStatus();
        }
        return null;
    }

    public boolean isCheckedOut() {
        Model model = getDelegate();
        if (model instanceof ContentDriven) {
            ContentDriven<?> content = (ContentDriven<?>) model;
            return content.isCheckedOut();
        }
        return false;
    }

    public boolean isToggleLockAvailable() {
        Model model = getDelegate();
        if (model instanceof ContentDriven) {
            ContentDriven<?> content = (ContentDriven<?>) model;
            return content.isLockable() && (!content.isLocked() || content.isHoldsLock());
        }
        return false;
    }

    public boolean isLockable() {
        Model model = getDelegate();
        if (model instanceof ContentDriven) {
            ContentDriven<?> content = (ContentDriven<?>) model;
            return content.isLockable();
        }
        return false;
    }

    public boolean isHoldsLock() {
        Model model = getDelegate();
        if (model instanceof ContentDriven) {
            ContentDriven<?> content = (ContentDriven<?>) model;
            return content.isHoldsLock();
        }
        return false;
    }

    public boolean isLocked() {
        Model model = getDelegate();
        if (model instanceof ContentDriven) {
            ContentDriven<?> content = (ContentDriven<?>) model;
            return content.isLocked();
        }
        return false;
    }

    @Nullable
    public String getLockOwner() {
        Model model = getDelegate();
        if (model instanceof ContentDriven) {
            ContentDriven<?> content = (ContentDriven<?>) model;
            return content.getLockOwner();
        }
        return null;
    }

    // Tenants

    public boolean isTenantSupport() {
        return getSiteManager().isTenantSupport();
    }

    // Assets

    public boolean isAssetsSupport() {
        return getSiteManager().isAssetsSupport();
    }

    // User context

    public String getUserId() {
        return getContext().getResolver().getUserID();
    }

    public boolean isDevelopModeAllowed() {
        // FIXME disabled for release 1.0 - unlock if useful
        //return false;
        PagesTenantSupport tenantSupport = getSiteManager().getTenantSupport();
        return tenantSupport == null || tenantSupport.isDevelopModeAllowed(getContext(), getResource());
    }

    public String getLogoutUrl() {
        CoreConfiguration config = getContext().getService(CoreConfiguration.class);
        String targetUri = PAGES_FRAME_PATH + ".html" + getResource().getPath();
        String logoutUrl = config.getLogoutUrl(config.getLoginUrl(targetUri));
        return StringUtils.defaultIfBlank(logoutUrl, "/system/sling/logout.html?logout=true&GLO=true");
    }

    // Services...

    public PageManager getPageManager() {
        if (pageManager == null) {
            pageManager = getContext().getService(PageManager.class);
        }
        return pageManager;
    }

    public SiteManager getSiteManager() {
        if (siteManager == null) {
            siteManager = getContext().getService(SiteManager.class);
        }
        return siteManager;
    }

    public ResourceManager getResourceManager() {
        if (resourceManager == null) {
            resourceManager = getContext().getService(ResourceManager.class);
        }
        return resourceManager;
    }
}
