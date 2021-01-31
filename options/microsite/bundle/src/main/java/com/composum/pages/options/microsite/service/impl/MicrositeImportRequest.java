package com.composum.pages.options.microsite.service.impl;

import com.composum.pages.options.microsite.strategy.MicrositeSourceTransformer;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.logging.Message;
import com.composum.sling.core.servlet.Status;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * the stateful import request object of the import service
 */
public class MicrositeImportRequest {

    enum ContentType {html, style, source}

    /**
     * Up to the entry point (index.html) found in the ZIP file all source transformations are delayed
     * to ensure that all URLs in all source files are transformed relative to the index file path
     */
    protected class DelayedTransformation {

        public final Resource parent;
        public final String name;
        public final ContentType type;
        public final String content;

        public DelayedTransformation(Resource parent, String name, ContentType type, String content) {
            this.parent = parent;
            this.name = name;
            this.type = type;
            this.content = content;
        }
    }

    protected final BeanContext context;

    /**
     * the status object to use during import
     */
    protected final Status status;

    /**
     * the content resource of the target page for the import
     */
    protected final Resource pageContent;

    /**
     * the ZIP file parameter object
     */
    protected final RequestParameter importFile;

    /**
     * the ZIP file content stream to import
     */
    protected ZipInputStream zipStream;

    /**
     * the source transformer strategy instance
     */
    protected MicrositeSourceTransformer sourceTransformer;

    /**
     * the buf to collect all delayed source transformations
     */
    protected final List<DelayedTransformation> delayedTransformations = new ArrayList<>();

    public void addMessage(Message message) {
        status.addMessage(message);
    }

    public MicrositeImportRequest(@Nonnull BeanContext context, @Nonnull Status status,
                                  @Nonnull Resource pageContent, @Nonnull RequestParameter importFile) {
        this.context = context;
        this.status = status;
        this.pageContent = pageContent;
        this.importFile = importFile;
    }

    public void startImport(MicrositeSourceTransformer sourceTransformer, ZipInputStream zipStream) {
        this.sourceTransformer = sourceTransformer;
        this.zipStream = zipStream;
    }

    public BeanContext getContext() {
        return context;
    }

    public Resource getPageContent() {
        return pageContent;
    }

    public RequestParameter getImportFile() {
        return importFile;
    }

    public ZipInputStream getZipStream() {
        return zipStream;
    }

    public MicrositeSourceTransformer getSourceTransformer() {
        return sourceTransformer;
    }

    public String getRelativeBase(Resource baseResource) {
        return baseResource.getPath().substring(pageContent.getPath().length());
    }

    public void addDelayedTransformation(Resource parent, String name, ContentType type, String content) {
        getDelayedTransformations().add(new DelayedTransformation(parent, name, type, content));
    }

    public List<DelayedTransformation> getDelayedTransformations() {
        return delayedTransformations;
    }
}
