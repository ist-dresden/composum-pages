package com.composum.pages.commons.filter;

import com.composum.sling.core.filter.ResourceFilter;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import static com.composum.pages.commons.PagesConstants.PROP_IS_TEMPLATE;
import static com.composum.pages.commons.PagesConstants.PROP_TEMPLATE;

public class TemplateFilter implements ResourceFilter {

    public static final TemplateFilter INSTANCE = new TemplateFilter();

    @Override
    public boolean accept(Resource resource) {
        Resource content = resource.getChild(JcrConstants.JCR_CONTENT);
        if (content != null) {
            ValueMap values = content.getValueMap();
            Boolean isTemplate = values.get(PROP_IS_TEMPLATE, Boolean.class);
            if (isTemplate == null) {
                isTemplate = values.get(PROP_TEMPLATE, String.class) == null;
            }
            return isTemplate;
        }
        return false;
    }

    @Override
    public boolean isRestriction() {
        return true;
    }

    @Override
    public void toString(StringBuilder builder) {
        builder.append("PageTemplateFilter");
    }
}
