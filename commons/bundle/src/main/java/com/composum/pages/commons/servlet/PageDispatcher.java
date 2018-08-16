package com.composum.pages.commons.servlet;

import com.composum.pages.commons.model.Page;

import java.io.IOException;

/**
 * the service interface for page dispatching strategy implementations registered by the PageNodeServlet
 */
public interface PageDispatcher {

    /**
     * @param page the page to check for a redirect
     * @return 'true' if a redirect is send back via the pages contexts response
     * @throws IOException if an error is occurring during redirect
     */
    boolean redirect(Page page) throws IOException;

    /**
     * @param page the page to determine the forward target
     * @return the target page for the content forward performed by the PageNodeServlet
     */
    Page getForwardPage(Page page);
}
