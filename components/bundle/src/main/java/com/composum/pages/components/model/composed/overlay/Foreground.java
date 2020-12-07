package com.composum.pages.components.model.composed.overlay;

import com.composum.pages.commons.model.Container;
import com.composum.pages.commons.model.Element;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

import static com.composum.pages.components.model.composed.overlay.OverlayItem.PN_HIDE_CONTENT;

public class Foreground extends Container {

    private transient Boolean empty;
    private transient Boolean hideContent;

    public Foreground() {
    }

    public Foreground(BeanContext context, String path, String resourceType) {
        super(context, path, resourceType);
    }

    public Foreground(BeanContext context, Resource resource) {
        super(context, resource);
    }

    @Override
    protected ResourceFilter createRenderFilter() {
        return new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                super.createRenderFilter(), new ResourceFilter() {

            @Override
            public boolean accept(@Nullable Resource resource) {
                return resource != null && (isEditMode() || OverlayItem.isEnabled(resource));
            }

            @Override
            public boolean isRestriction() {
                return true;
            }

            @Override
            public void toString(@Nonnull StringBuilder builder) {
            }
        });
    }

    public boolean isNotEmpty() {
        return getElements().size() > 0;
    }

    public boolean isHideContent() {
        if (hideContent == null) {
            hideContent = false;
            for (Element element : getElements()) {
                if (element.getProperty(PN_HIDE_CONTENT, Boolean.FALSE)) {
                    hideContent = true;
                    break;
                }
            }
        }
        return hideContent;
    }
}
