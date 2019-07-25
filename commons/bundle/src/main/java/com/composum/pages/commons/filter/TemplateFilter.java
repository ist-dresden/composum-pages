package com.composum.pages.commons.filter;

import com.composum.sling.core.filter.ResourceFilter;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.util.ArrayList;
import java.util.List;

import static com.composum.pages.commons.PagesConstants.PROP_IS_TEMPLATE;
import static com.composum.pages.commons.PagesConstants.PROP_TEMPLATE;
import static com.composum.pages.commons.PagesConstants.PROP_TEMPLATE_REF;

/**
 * accepts page resources marked as a template
 */
public class TemplateFilter extends ResourceFilter.AbstractResourceFilter {

    public static final TemplateFilter INSTANCE = new TemplateFilter();

    @Override
    public boolean accept(Resource resource) {
        boolean isTemplate = true;
        List<String> trace = new ArrayList<>();
        Resource content;
        while (isTemplate && resource != null && !trace.contains(resource.getPath())
                && (content = resource.getChild(JcrConstants.JCR_CONTENT)) != null) {
            trace.add(resource.getPath());
            ValueMap values = content.getValueMap();
            Boolean isTemplateProp = values.get(PROP_IS_TEMPLATE, Boolean.class);
            if (isTemplateProp == null) {
                String templatePath = values.get(PROP_TEMPLATE, String.class);
                if (templatePath != null) {
                    ResourceResolver resolver = resource.getResourceResolver();
                    resource = resolver.getResource(templatePath);
                    continue;
                }
                templatePath = values.get(PROP_TEMPLATE_REF, String.class);
                if (templatePath != null) {
                    ResourceResolver resolver = resource.getResourceResolver();
                    resource = resolver.getResource(templatePath);
                }
            } else {
                isTemplate = isTemplateProp;
            }
        }
        return isTemplate;
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
