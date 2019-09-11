package com.composum.pages.commons.event;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.service.PageManager;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.SlingResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.ModificationType;
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
            if (ModificationType.COPY != modification.getType()) { // the source of a copy is not changed.
                registerPage(context, modifiedPages, modification.getSource(), modification.getType());
            }
            registerPage(context, modifiedPages, modification.getDestination(), modification.getType());
        }
        Calendar now = Calendar.getInstance();
        for (Page page : modifiedPages) {
            pageManager.touch(context, page, now);
        }
    }

    protected void registerPage(BeanContext context, Set<Page> pageSet, String path, ModificationType modificationType) {
        switch (modificationType) {
            case CHECKIN:
            case CHECKOUT:
                return; // that shouldn't update the page modification time
        }
        if (StringUtils.isNotBlank(path)) {
            Resource resource = context.getResolver().resolve(path); // probably deleted
            Page page = pageManager.getContainingPage(context, resource);
            if (page != null && SlingResourceUtil.isSameOrDescendant(page.getContent().getPath(), path)) {
                // changes outside the content node should set the page as modified.
                // That can happen e.g. after deleting a page.
                // The order of nodes is published by publishing a subpage, so this should also be ignored.
                pageSet.add(page);
            }
        }
    }
}
