package com.composum.pages.commons.replication;

import com.composum.pages.commons.filter.SitePageFilter;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.SiteManager;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.SlingResourceUtil;
import com.composum.sling.platform.security.AccessMode;
import com.composum.sling.platform.staging.ReleaseChangeEventListener;
import com.composum.sling.platform.staging.StagingReleaseManager;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** Augments the {@link com.composum.pages.commons.servlet.ReleaseServlet}. */
public interface PagesReleaseService extends ReleaseChangeEventListener {


}
