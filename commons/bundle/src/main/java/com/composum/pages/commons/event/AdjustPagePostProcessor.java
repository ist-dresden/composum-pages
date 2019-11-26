package com.composum.pages.commons.event;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.PageManager;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.SlingResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.ModificationType;
import org.apache.sling.servlets.post.SlingPostProcessor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Adjust 'jcr:lastModified' of the containing page or site configuration on element modification.
 * This is done as SlingPostProcessor since this has several advantages over ResourceChangeListener:
 * <ul>
 *     <li>It allows to distinguish between a checkin / checkpoint for a resource and an actual modification.</li>
 *     <li>It isn't triggered from a package installation, which should not change the last modification time.</li>
 *     <li>It is not triggered during migration scripts.</li>
 * </ul>
 * A part of these can be caught by looking at the changing user, but the checkins hardly, so a
 * ResourceChangeListener seems too brittle.
 */
@Component
public class AdjustPagePostProcessor implements SlingPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AdjustPagePostProcessor.class);

    @Reference
    protected PageManager pageManager;

    @Override
    public void process(SlingHttpServletRequest request, @Nonnull List<Modification> changes) {
        LOG.info("Modified: {}", changes);
        Calendar now = Calendar.getInstance();
        ResourceResolver resolver = request.getResourceResolver();
        BeanContext context = new BeanContext.Service(resolver);
        Set<Page> modifiedPages = new HashSet<>();
        Map<String, Resource> modifiedResources = new HashMap<>(); // path to page
        for (Modification modification : changes) {
            if (ModificationType.COPY != modification.getType()) { // the source of a copy is not changed.
                registerResource(context, modifiedPages, modifiedResources, modification.getSource(),
                        modification.getType());
            }
            registerResource(context, modifiedPages, modifiedResources, modification.getDestination(),
                    modification.getType());
        }
        LOG.info("Adjusting lastmodified of {}, {}", modifiedPages, modifiedResources.keySet());
        for (Page page : modifiedPages) {
            pageManager.touch(context, page, now);
        }
        for (Map.Entry<String, Resource> entry : modifiedResources.entrySet()) {
            Resource resource = entry.getValue();
            ModifiableValueMap values = resource.adaptTo(ModifiableValueMap.class);
            if (values != null) {
                values.put(ResourceUtil.PROP_LAST_MODIFIED, now);
                values.put(ResourceUtil.JCR_LASTMODIFIED_BY, resource.getResourceResolver().getUserID());
            } else {
                LOG.error("Resource not modifiable but changed? {} from {}", resource, changes);
            }
        }
    }

    protected void registerResource(BeanContext context, Set<Page> pageSet, Map<String, Resource> resourceSet, String path, ModificationType modificationType) {
        //noinspection EnumSwitchStatementWhichMissesCases
        switch (modificationType) {
            case CHECKIN:
            case CHECKOUT:
                return; // that shouldn't update the page modification time
        }
        if (StringUtils.isNotBlank(path)) {
            // we use resolver.resolve since we want a nonexisting resource for deleted resources to find the page it
            // was deleted from.
            Resource resource = context.getResolver().resolve(path);

            Page page = pageManager.getContainingPage(context, resource);
            if (page != null && SlingResourceUtil.isSameOrDescendant(page.getContent().getPath(), path)) {
                // changes outside the content node should set the page as modified.
                // That can happen e.g. after deleting a page.
                // The order of nodes is published by publishing a subpage, so this should also be ignored.
                pageSet.add(page);
            }

            Resource siteCfgResource = resource;
            while (siteCfgResource != null) {
                if (Site.isSiteConfiguration(siteCfgResource)) {
                    resourceSet.put(siteCfgResource.getPath(), siteCfgResource);
                    break;
                }
                siteCfgResource = siteCfgResource.getParent();
            }
        }
    }
}
