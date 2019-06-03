package com.composum.pages.stage.model.widget;

import com.composum.pages.commons.model.GenericModel;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.service.ResourceManager;
import com.composum.pages.commons.widget.MultiSelect;
import com.composum.pages.commons.widget.WidgetModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * the abstract ...References model
 */
public abstract class ReferencesWidget extends MultiSelect implements WidgetModel {

    public static final String ATTR_PAGE = "page";
    public static final String ATTR_PAGES = "pages";
    public static final String ATTR_SCOPE = "scope";

    public class Reference implements Comparable<Reference> {

        protected final Page referrer;
        protected final GenericModel model;

        public Reference(Page referrer, Resource resource) {
            this.referrer = referrer;
            this.model = new GenericModel(getContext(), resource);
        }

        public String getPath() {
            return model.getPath();
        }

        public String getName() {
            return model.getName();
        }

        public String getTitle() {
            String title = model.getTitle();
            return StringUtils.isNotBlank(title) ? title : getName();
        }

        public String getPathInSite() {
            String path = getPath();
            Site site = referrer.getSite();
            if (site != null) {
                String siteRoot = site.getPath() + "/";
                if (path.startsWith(siteRoot)) {
                    path = path.substring(siteRoot.length());
                }
            }
            return path;
        }

        @Override
        public int compareTo(Reference other) {
            return getPath().compareTo(other.getPath());
        }
    }

    private transient Set<Page> pages;

    private transient TreeSet<Reference> references;

    private transient PageManager pageManager;

    public Set<Reference> getReferences() {
        if (references == null) {
            references = new TreeSet<>();
            for (Page page : getPages()) {
                references.addAll(retrieveCandidates(page));
            }
            Set<String> pagePaths = getPages().stream().map(Page::getPath).collect(Collectors.toSet());
            references = references.stream().filter(r -> !pagePaths.contains(r.getPath())).collect(Collectors.toCollection(TreeSet::new));
        }
        return references;
    }

    protected abstract List<Reference> retrieveCandidates(@Nonnull Page page);

    public Set<Page> getPages() {
        if (pages == null) {
            SlingHttpServletRequest request = getContext().getRequest();
            if (request != null) {
                pages = getPages(request.getAttribute(ATTR_PAGES));
            }
            if (pages == null) {
                pages = Collections.singleton(getCurrentPage());
            }
        }
        return pages;
    }

    @Override
    public String filterWidgetAttribute(String attributeKey, Object attributeValue) {
        if (ATTR_PAGE.equals(attributeKey)) {
            Page page = getPageAttribute(attributeValue);
            pages = page != null ? Collections.singleton(page) : null;
            return null;
        } else if (ATTR_PAGES.equals(attributeKey)) {
            // a set of pages (multi page selection)...
            pages = getPages(attributeValue);
            return null;
        }
        return attributeKey;
    }

    protected Set<Page> getPages(Object attributeValue) {
        Set<Page> result = null;
        Collection<?> objects = attributeValue instanceof ResourceManager.ReferenceList
                ? (ResourceManager.ReferenceList) attributeValue
                : attributeValue instanceof Object[] ? Arrays.asList((Object[]) attributeValue)
                : attributeValue instanceof Collection ? ((Collection) attributeValue) : null;
        if (objects != null) {
            result = new TreeSet<>();
            for (Object element : objects) {
                Page page = getPageAttribute(element);
                if (page != null) {
                    result.add(page);
                }
            }
        }
        return result;
    }

    protected Page getPageAttribute(Object attributeValue) {
        if (attributeValue instanceof Page) return (Page) attributeValue;
        else if (attributeValue instanceof ResourceManager.ResourceReference)
            return getPageManager().createBean(getContext(), ((ResourceManager.ResourceReference) attributeValue).getResource());
        else if (attributeValue instanceof Resource)
            return getPageManager().createBean(getContext(), (Resource) attributeValue);
        else if (attributeValue != null) return getPageManager().createBean(getContext(),
                getContext().getResolver().getResource(attributeValue.toString()));
        else return null;
    }
}
