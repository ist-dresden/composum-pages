package com.composum.pages.stage.model.edit.page;

import com.composum.pages.commons.model.AbstractModel;
import com.composum.pages.commons.model.Component;
import com.composum.pages.commons.service.ComponentManager;
import com.composum.pages.commons.util.RequestUtil;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.composum.pages.commons.PagesConstants.CATEGORY_OTHER;
import static com.composum.pages.commons.servlet.EditServlet.PAGE_COMPONENT_TYPES;
import static com.composum.pages.commons.servlet.EditServlet.PAGE_COMPONENT_TYPES_SCOPE;
import static com.composum.sling.core.servlet.AbstractServiceServlet.PARAM_NAME;
import static javax.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static javax.servlet.http.HttpServletResponse.SC_OK;

public class Components extends PageModel {

    private static final Logger LOG = LoggerFactory.getLogger(Components.class);

    public static final String WIDGET_NAME = "elementType";

    private transient TreeMap<String, TreeSet<Component>> components;

    public static final Comparator<Component> COMPONENT_COMPARATOR = Comparator.comparing(AbstractModel::getTitle);

    public Collection<String> getAllCategories() {
        BeanContext context = getContext();
        return context.getService(ComponentManager.class).getComponentCategories(context.getResolver());
    }

    public Set<String> getCategories() {
        return getComponents().keySet();
    }

    public TreeMap<String, TreeSet<Component>> getComponents() {
        if (components == null) {
            BeanContext context = getDelegate().getContext();
            ResourceResolver resolver = context.getResolver();
            components = new TreeMap<>();
            @SuppressWarnings("unchecked")
            List<String> allowedElements = (List<String>) context.getRequest().getAttribute(PAGE_COMPONENT_TYPES);
            if (allowedElements != null) {
                for (String path : allowedElements) {
                    Resource typeResource = resolver.getResource(path);
                    if (typeResource != null) {
                        Component component = new Component(context, typeResource);
                        List<String> categories = component.getCategory();
                        String category = categories.size() > 0 ? categories.get(0) : CATEGORY_OTHER;
                        TreeSet<Component> set = components.computeIfAbsent(category,
                                k -> new TreeSet<>(COMPONENT_COMPARATOR));
                        set.add(component);
                    } else {
                        LOG.error("no resource found for type '{}'", path);
                    }
                }
            }
        }
        return components;
    }

    public String getWidgetName() {
        SlingHttpServletRequest request = getContext().getRequest();
        if (request != null) {
            // in a request context the hint for the requested scope is mapped to the response status
            Boolean scopeAccepted = (Boolean) request.getAttribute(PAGE_COMPONENT_TYPES_SCOPE);
            if (scopeAccepted != null) {
                getContext().getResponse().setStatus(scopeAccepted ? SC_ACCEPTED : SC_OK);
            }
            return RequestUtil.getParameter(request, PARAM_NAME, WIDGET_NAME);
        }
        return WIDGET_NAME;
    }
}
