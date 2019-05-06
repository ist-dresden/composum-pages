package com.composum.pages.stage.model.widget;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import org.apache.sling.api.resource.Resource;

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

    public Resource getScope() {
        return scope != null ? scope : getPage().getSite().getResource();
    }

    protected List<Reference> retrieveReferences() {
        List<Reference> references = new ArrayList<>();
        Collection<Resource> resources = getPageManager().getReferrers(getPage(), getScope(), isResolved());
        for (Resource resource : resources) {
            references.add(new Reference(resource));
        }
        return references;
    }

    public boolean isResolved() {
        return resolved != null ? resolved : false;
    }

    @Override
    public String filterWidgetAttribute(String attributeKey, Object attributeValue) {
        if (ATTR_PAGE.equals(attributeKey)) {
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
