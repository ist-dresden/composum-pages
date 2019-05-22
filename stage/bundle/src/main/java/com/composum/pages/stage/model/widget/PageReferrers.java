package com.composum.pages.stage.model.widget;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * the PageReferences model...
 */
public class PageReferrers extends ReferencesWidget {

    public static final String ATTR_RESOLVED = "resolved";

    private transient Resource scope;
    private transient Boolean resolved;

    protected List<Reference> retrieveReferences(@Nonnull final Page target) {
        List<Reference> references = new ArrayList<>();
        Collection<Resource> resources = getPageManager().getReferrers(target, getScope(target), isResolved());
        for (Resource resource : resources) {
            references.add(new Reference(target, resource));
        }
        return references;
    }

    protected Resource getScope(@Nonnull final Page target) {
        if (scope != null) {
            return scope;
        }
        Site site = target.getSite();
        if (site != null) {
            return site.getResource();
        }
        return null;
    }

    public boolean isResolved() {
        return resolved != null ? resolved : false;
    }

    @Override
    public String filterWidgetAttribute(String attributeKey, Object attributeValue) {
        if (ATTR_SCOPE.equals(attributeKey)) {
            scope = attributeValue instanceof Site ? ((Site) attributeValue).getResource()
                    : attributeValue instanceof Page ? ((Page) attributeValue).getResource()
                    : attributeValue instanceof Resource ? (Resource) attributeValue
                    : attributeValue != null ? getContext().getResolver().getResource(attributeValue.toString()) : null;
            return null;
        } else if (ATTR_RESOLVED.equals(attributeKey)) {
            resolved = attributeValue instanceof Boolean ? (Boolean) attributeValue
                    : attributeValue != null ? Boolean.valueOf(attributeValue.toString()) : null;
            return null;
        }
        return super.filterWidgetAttribute(attributeKey, attributeValue);
    }
}
