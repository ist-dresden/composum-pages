package com.composum.pages.stage.model.widget;

import com.composum.pages.commons.PagesConstants.ReferenceType;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * the PageReferrers model...
 */
public class PageReferences extends ReferencesWidget {

    public static final String ATTR_UNRESOLVED = "unresolved";

    private transient ReferenceType scope;
    private transient Boolean unresolved;

    protected List<Reference> retrieveReferences() {
        List<Reference> references = new ArrayList<>();
        Collection<Resource> resources = getPageManager().getReferences(getPage(), getScope(), isUnresolved());
        for (Resource resource : resources) {
            references.add(new Reference(resource));
        }
        return references;
    }

    public ReferenceType getScope() {
        return scope;
    }

    public boolean isUnresolved() {
        return unresolved != null ? unresolved : false;
    }

    @Override
    public String filterWidgetAttribute(String attributeKey, Object attributeValue) {
        if (ATTR_SCOPE.equals(attributeKey)) {
            scope = attributeValue instanceof ReferenceType ? (ReferenceType) attributeValue
                    : attributeValue != null ? ReferenceType.valueOf(attributeValue.toString()) : null;
            return null;
        } else if (ATTR_UNRESOLVED.equals(attributeKey)) {
            unresolved = attributeValue instanceof Boolean ? (Boolean) attributeValue
                    : attributeValue != null ? Boolean.valueOf(attributeValue.toString()) : null;
            return null;
        }
        return super.filterWidgetAttribute(attributeKey, attributeValue);
    }
}
