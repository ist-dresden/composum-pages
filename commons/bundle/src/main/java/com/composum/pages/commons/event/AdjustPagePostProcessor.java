package com.composum.pages.commons.event;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.service.PageManager;
import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.SlingPostProcessor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * adjust type of column containers created on demand (include of synthetic column component)
 */
@Component
public class AdjustPagePostProcessor implements SlingPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AdjustPagePostProcessor.class);

    public static final String RESOURCE_TYPE_ROW = "composum/pages/components/container/row";
    public static final String RESOURCE_TYPE_COLUMN = "composum/pages/components/container/row/column";

    public static final String COLUMN_BASE_NAME = "column-";

    @Reference
    protected PageManager pageManager;

    @Override
    public void process(SlingHttpServletRequest request, List<Modification> changes) {
        ResourceResolver resolver = request.getResourceResolver();
        BeanContext context = new BeanContext.Service(resolver);
        Set<Page> modifiedPages = new HashSet<>();
        for (Modification modification : changes) {
            registerPage(context, modifiedPages, modification.getSource());
            registerPage(context, modifiedPages, modification.getDestination());
        }
        Calendar now = new GregorianCalendar();
        now.setTimeInMillis(System.currentTimeMillis());
        for (Page page : modifiedPages) {
            pageManager.touch(context, page, now, false);
        }
    }

    protected void registerPage (BeanContext context, Set<Page> pageSet , String path) {
        if (StringUtils.isNotBlank(path)) {
            Resource resource = context.getResolver().getResource(path);
            if (resource != null) {
                Page page = pageManager.getContainingPage(context, resource);
                if (page != null) {
                    pageSet.add(page);
                }
            }
        }
    }
}
