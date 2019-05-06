package com.composum.pages.stage.model.widget;

import com.composum.pages.commons.model.GenericModel;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.widget.MultiSelect;
import com.composum.pages.commons.widget.WidgetModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import java.util.List;

/**
 * the abstract ...References model
 */
public abstract class ReferencesWidget extends MultiSelect implements WidgetModel {

    public static final String ATTR_PAGE = "page";
    public static final String ATTR_SCOPE = "scope";

    public class Reference {

        protected final GenericModel model;

        public Reference(Resource resource) {
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
            Site site = getPage().getSite();
            if (site != null) {
                String siteRoot = site.getPath() + "/";
                if (path.startsWith(siteRoot)) {
                    path = path.substring(siteRoot.length());
                }
            }
            return path;
        }
    }

    private transient Page page;

    private transient List<Reference> references;

    private transient PageManager pageManager;

    public List<Reference> getReferences() {
        if (references == null) {
            references = retrieveReferences();
        }
        return references;
    }

    protected abstract List<Reference> retrieveReferences();

    public Page getPage() {
        return page != null ? page : getCurrentPage();
    }

    @Override
    public String filterWidgetAttribute(String attributeKey, Object attributeValue) {
        if (ATTR_PAGE.equals(attributeKey)) {
            page = attributeValue instanceof Page ? (Page) attributeValue
                    : attributeValue instanceof Resource ? getPageManager().createBean(getContext(), (Resource) attributeValue)
                    : attributeValue != null ? getPageManager().createBean(getContext(),
                    getContext().getResolver().getResource(attributeValue.toString())) : null;
            return null;
        }
        return attributeKey;
    }
}
