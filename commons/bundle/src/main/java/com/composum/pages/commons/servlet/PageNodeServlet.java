package com.composum.pages.commons.servlet;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.PageContent;
import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.request.LocaleRequestWrapper;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.service.Theme;
import com.composum.pages.commons.util.ThemeUtil;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Page Node Servlet",
                ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=cpp:Page",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET
        })
public class PageNodeServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(PageNodeServlet.class);

    @Reference
    protected PageManager pageManager;

    protected BundleContext bundleContext;

    protected List<PageDispatcher> pageDispatchers = Collections.synchronizedList(new ArrayList<>());

    @Activate
    private void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    protected void doGet(@Nonnull SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response)
            throws ServletException, IOException {

        final BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
        final Resource resource = request.getResource();
        Page page = pageManager.createBean(context, resource);

        if (page.isValid()) {
            // ensure that the current page is declared before any property access
            request.setAttribute(PagesConstants.RA_CURRENT_PAGE, page);
            Theme theme = page.getTheme();
            if (theme != null) {
                request.setAttribute(PagesConstants.RA_CURRENT_THEME, theme);
            }

            // if not in edit mode check for a HTTP redirect triggered by one of the dispatchers
            DisplayMode.Value displayMode = DisplayMode.current(context);
            if (displayMode != DisplayMode.Value.EDIT && displayMode != DisplayMode.Value.DEVELOP) {
                for (PageDispatcher dispatcher : pageDispatchers) {
                    if (dispatcher.redirect(page)) {
                        return;
                    }
                }
            }

            // adjust the requests locale to the locale of the requested page if necessary
            // to synchroinze the systems I18N support with the pages locale
            Locale pageLocale = page.getLocale();
            if (!pageLocale.equals(request.getLocale())) {
                request = new LocaleRequestWrapper(request, pageLocale);
            }

            // determine the page content resource to use for the request forward
            for (PageDispatcher dispatcher : pageDispatchers) {
                page = dispatcher.getForwardPage(page);
            }
            PageContent pageContent = page.getContent();
            Resource forwardContent = pageContent.getResource();

            RequestDispatcherOptions options = new RequestDispatcherOptions();
            ThemeUtil.applyTheme(theme, forwardContent, options);
            RequestDispatcher dispatcher = request.getRequestDispatcher(forwardContent, options);
            if (dispatcher != null) {
                dispatcher.forward(request, response);
            }

        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // page dispatcher registry

    @Reference(
            service = PageDispatcher.class,
            policy = ReferencePolicy.DYNAMIC,
            cardinality = ReferenceCardinality.MULTIPLE
    )
    protected void addPageDispatcher(@Nonnull final PageDispatcher dispatcher) {
        LOG.info("addPageDispatcher: {}", dispatcher.getClass().getSimpleName());
        pageDispatchers.add(dispatcher);
    }

    protected void removePageDispatcher(@Nonnull final PageDispatcher dispatcher) {
        LOG.info("removePageDispatcher: {}", dispatcher.getClass().getSimpleName());
        pageDispatchers.remove(dispatcher);
    }
}
