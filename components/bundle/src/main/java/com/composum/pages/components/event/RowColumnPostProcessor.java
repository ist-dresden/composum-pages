package com.composum.pages.components.event;

import com.composum.pages.commons.PagesConstants;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.ModificationType;
import org.apache.sling.servlets.post.SlingPostProcessor;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * adjust modification time of containing page on resource modification
 */
@Component
public class RowColumnPostProcessor implements SlingPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(RowColumnPostProcessor.class);

    public static final String RESOURCE_TYPE_ROW = "composum/pages/components/container/row";
    public static final String RESOURCE_TYPE_COLUMN = "composum/pages/components/container/row/column";

    public static final String COLUMN_BASE_NAME = "column-";

    @Override
    public void process(SlingHttpServletRequest request, List<Modification> changes) {
        ResourceResolver resolver = request.getResourceResolver();
        for (Modification modification : changes) {
            if (modification.getType() == ModificationType.CREATE) {
                String sourcePath = modification.getSource();
                Resource source = resolver.getResource(sourcePath);
                Resource parent;
                if (source != null &&
                        (parent = source.getParent()) != null &&
                        parent.isResourceType(RESOURCE_TYPE_ROW) &&
                        source.getName().startsWith(COLUMN_BASE_NAME)) {
                    ModifiableValueMap values = source.adaptTo(ModifiableValueMap.class);
                    values.put(JcrConstants.JCR_PRIMARYTYPE, PagesConstants.NODE_TYPE_CONTAINER);
                    values.put(ResourceUtil.PROP_RESOURCE_TYPE, RESOURCE_TYPE_COLUMN);
                }
            }
        }
    }
}
