package com.composum.pages.options.microsite.service;

import com.composum.sling.core.BeanContext;
import com.composum.sling.core.servlet.Status;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * The service to import a 'full' site as ZIP content into the content resource of a page.
 */
public interface MicrositeImportService {

    /**
     * imports the input stream of a POST request parameter as ZIP stream (must be such one)
     * into a pages content resource using CRUD operations of the resolver; no commit is made.
     */
    void importSiteContent(@Nonnull BeanContext context, @Nonnull Status status,
                           @Nonnull Resource pageContent, @Nonnull RequestParameter fileParam)
            throws IOException;
}
