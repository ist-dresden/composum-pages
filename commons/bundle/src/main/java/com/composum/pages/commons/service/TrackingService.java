package com.composum.pages.commons.service;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.LoginException;

import javax.jcr.RepositoryException;
import java.nio.charset.Charset;

/**
 * a simple service interface to track page requests
 */
public interface TrackingService {

    String PAGE_TOKEN_COOKIE = "composum-pages-token-page";

    String PROP_TOTAL = "total";
    String PROP_UNIQUE = "unique";
    String PROP_URL = "url";

    String EXT_PNG = ".png";
    Charset CHARSET = Charset.forName("UTF-8");

    void trackToken(BeanContext context, String path, String referer)
            throws RepositoryException, LoginException;
}
