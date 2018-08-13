package com.composum.pages.commons.event;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.SlingPostProcessor;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
public class PageEditPostProcessor implements SlingPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(PageEditPostProcessor.class);

    @Override
    public void process(SlingHttpServletRequest request, List<Modification> changes) throws Exception {
        ResourceResolver resolver = request.getResourceResolver();
        Resource resource = request.getResource();
        LOG.info("process({},{})", resource, changes);
    }
}
