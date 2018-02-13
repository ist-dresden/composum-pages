package com.composum.pages.options.microsite.service.impl;

import com.composum.pages.options.microsite.service.MicrositeImportStatus;
import com.composum.pages.options.microsite.strategy.MicrositeSourceTransformer;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * the stateful import request object of the import service
 */
public class MicrositeImportRequest implements MicrositeImportStatus {

    protected final BeanContext context;

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
     * Up to the entry point (index.html) found in the ZIP file all source transformations are delayed
     * to ensure that all URLs in all source files are transformed relative to the index file path
     */
    protected class DelayedTransformation {

        public final Resource parent;
        public final String name;
        public final String content;

        public DelayedTransformation(Resource parent, String name, String content) {
            this.parent = parent;
            this.name = name;
            this.content = content;
        }
    }

    /**
     * the buffer to collect all delayed source transformations
     */
    protected final List<DelayedTransformation> delayedTransformations = new ArrayList<>();

    /**
     * the collection of general messages collected during import
     */
    protected final List<Message> messages = new ArrayList<>();

    /**
     * the final status of the import
     */
    protected MessageLevel importStatus = MessageLevel.info;

    @Override
    public boolean isSuccessful() {
        return importStatus != MessageLevel.error;
    }

    public void addMessage(Message message) {
        messages.add(message);
        switch (message.getLevel()) {
            case warn:
                if (importStatus == MessageLevel.info) {
                    importStatus = MessageLevel.warn;
                }
                break;
            case error:
                if (importStatus != MessageLevel.error) {
                    importStatus = MessageLevel.error;
                }
                break;
        }
    }

    @Override
    public List<Message> getMessages() {
        return messages;
    }

    public MicrositeImportRequest(BeanContext context, Resource pageContent, RequestParameter importFile) {
        this.context = context;
        this.pageContent = pageContent;
        this.importFile = importFile;
    }

    public void startImport (MicrositeSourceTransformer sourceTransformer, ZipInputStream zipStream) {
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

    public void addDelayedTransformation(Resource parent, String name, String content) {
        getDelayedTransformations().add(new DelayedTransformation(parent, name, content));
    }

    public List<DelayedTransformation> getDelayedTransformations() {
        return delayedTransformations;
    }
}
