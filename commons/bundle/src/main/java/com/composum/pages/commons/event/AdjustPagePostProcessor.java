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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * adjust 'jcr:lastModified' of the containing page on element modification
 */
@Component
public class AdjustPagePostProcessor implements SlingPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AdjustPagePostProcessor.class);

    @Reference
    protected PageManager pageManager;

    @Override
    public void process(SlingHttpServletRequest request, List<Modification> changes) {
        LOG.info("Modified: {}", changes);
        ResourceResolver resolver = request.getResourceResolver();
        BeanContext context = new BeanContext.Service(resolver);
        Set<Page> modifiedPages = new HashSet<>();
        for (Modification modification : changes) {
            registerPage(context, modifiedPages, modification.getSource());
            registerPage(context, modifiedPages, modification.getDestination());
        }
        Calendar now = Calendar.getInstance();
        for (Page page : modifiedPages) {
            pageManager.touch(context, page, now);
        }
    }

    protected void registerPage(BeanContext context, Set<Page> pageSet, String path) {
        if (StringUtils.isNotBlank(path)) {
            Resource resource = context.getResolver().resolve(path); // probably deleted
            Page page = pageManager.getContainingPage(context, resource);
            if (page != null) {
                pageSet.add(page);
            }
        }
    }
}
