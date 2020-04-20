package com.composum.pages.options.microsite.service.impl;

import com.composum.pages.options.microsite.MicrositeConstants;
import com.composum.pages.options.microsite.service.MicrositeImportService;
import com.composum.pages.options.microsite.service.MicrositeImportStatus;
import com.composum.pages.options.microsite.strategy.MicrositeSourceTransformer;
import com.composum.sling.core.BeanContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.mime.MimeTypeService;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * The service implementation to import a 'full' site as ZIP content into the content resource of a page.
 */
@Component(
        service = MicrositeImportService.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Options - Microsite Import Service"
        }
)
@Designate(
        ocd = MicrositeImportServiceImpl.Configuration.class
)
public class MicrositeImportServiceImpl implements MicrositeImportService, MicrositeConstants {

    private static final Logger LOG = LoggerFactory.getLogger(MicrositeImportServiceImpl.class);

    public static final String PAGE_EXTENSION = ".html";
    public static final String PAGE_CONTENT_URL_SEGMENT = "/_jcr_content";

    /**
     * the microsite import service configuration 'object'
     */
    @ObjectClassDefinition(
            name = "Composum Pages Microsite Import Configuration"
    )
    public @interface Configuration {

        @AttributeDefinition(
                description = "the general on/off switch for this service"
        )
        boolean enabled() default true;

        @AttributeDefinition(
                description = "the list of path and filename patterns for all allowed files (whitelist)"
        )
        String[] whitelist() default {
                "^(.*/)?[^/]+\\.(html|jsp|s?css|less|js|map)$",
                "^(.*/)?[^/]+\\.(png|jpg|jpeg|svg|gif|ico)$",
                "^(.*/)?[^/]+\\.(eot|otf|ttf|woff|woff2)$",
                "^(.*/)?[^/]+\\.(mp4|m4v|mp3|ogg|acc|wav)$",
                "^(.*/)?[^/]+\\.(pdf|txt)$"
        };

        @AttributeDefinition(
                description = "the list of path and filename patterns for all forbidden files (blacklist)"
        )
        String[] blacklist() default {};

        @AttributeDefinition(
                description = "the list of path and filename patterns for files to ignore (e.g. system files)"
        )
        String[] ignored() default {
                "^(__|\\.).*$",
                "^.*/(__|\\.).*$"
        };
    }

    protected Configuration config;

    /**
     * the list of allowed content file patterns (path and name)
     */
    protected List<Pattern> whitelist;

    /**
     * the list of forbidden content file patterns (path and name)
     */
    protected List<Pattern> blacklist;

    /**
     * the list of ignored content file patterns (path and name)
     */
    protected List<Pattern> ignored;

    /**
     * this can be replaced my a mock for testing purposes
     */
    protected MicrositeImportProvider importProvider = new RepositoryImportProvider();

    /**
     * the service to determine the mime types of the imported files
     */
    @Reference
    protected MimeTypeService mimeTypeService;

    /**
     * a file is allowed if it matches one entry of the whitelist and no entry of the blacklist
     *
     * @param pathAndName the ZIP entries name (path)
     * @return 'true' if file can be imported
     */
    protected boolean isAllowedEntry(MicrositeImportRequest importRequest, String pathAndName) {
        boolean allowed = true;
        for (int i = 0; allowed && i < ignored.size(); i++) {
            allowed = !ignored.get(i).matcher(pathAndName).matches();
        }
        if (!allowed) {
            importRequest.addMessage(new MicrositeImportStatus.Message(MicrositeImportStatus.MessageLevel.warn,
                    "File \"{0}\" has been ignored.", pathAndName));
        } else {
            allowed = false;
            for (int i = 0; !allowed && i < whitelist.size(); i++) {
                allowed = whitelist.get(i).matcher(pathAndName).matches();
            }
            if (allowed) {
                for (int i = 0; allowed && i < blacklist.size(); i++) {
                    allowed = !blacklist.get(i).matcher(pathAndName).matches();
                }
                if (!allowed) {
                    importRequest.addMessage(new MicrositeImportStatus.Message(MicrositeImportStatus.MessageLevel.error,
                            "File \"{0}\" is forbidden - blacklisted.", pathAndName));
                }
            } else {
                importRequest.addMessage(new MicrositeImportStatus.Message(MicrositeImportStatus.MessageLevel.error,
                        "File \"{0}\" is forbidden - not whitelisted.", pathAndName));
            }
        }
        return allowed;
    }

    /**
     * @param filename the name of the ZIP entry
     * @return the mime type of the file
     */
    protected String getMimeType(String filename) {
        return mimeTypeService.getMimeType(filename);
    }

    /**
     * THE import method - imports the input stream as ZIP stream (must be such one) into a pages content; no commit is made.
     */
    @Override
    public MicrositeImportRequest importSiteContent(BeanContext context, Resource pageContent, RequestParameter importFile) {
        MicrositeImportRequest importRequest = new MicrositeImportRequest(context, pageContent, importFile);
        if (config.enabled()) {
            try (ZipInputStream zipStream = new ZipInputStream(Objects.requireNonNull(importFile.getInputStream()))) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("importSiteContent({},{})...", pageContent.getPath(), importFile.getFileName());
                }
                String rootPageName = Objects.requireNonNull(pageContent.getParent()).getName();
                MicrositeSourceTransformer sourceTransformer = new MicrositeSourceTransformer(
                        rootPageName + PAGE_EXTENSION, rootPageName + PAGE_CONTENT_URL_SEGMENT);
                importRequest.startImport(sourceTransformer, zipStream);
                importProvider.clearContent(pageContent);
                ZipEntry zipEntry;
                while ((zipEntry = zipStream.getNextEntry()) != null) {
                    importZipEntry(importRequest, zipEntry);
                }
                Calendar timestamp = new GregorianCalendar();
                timestamp.setTime(new Date());
                String user = importProvider.getCurrentUser(pageContent);
                ModifiableValueMap valueMap = pageContent.adaptTo(ModifiableValueMap.class);
                String indexPath = null;
                if (valueMap != null) {
                    indexPath = valueMap.get(PN_INDEX_PATH, "");
                }
                if (StringUtils.isNotBlank(indexPath)) {
                    valueMap.put(PN_LAST_IMPORT_TIME, timestamp);
                    valueMap.put(PN_LAST_IMPORT_FILE, importFile.getFileName());
                    valueMap.put(PN_LAST_IMPORT_SIZE, importFile.getSize());
                    valueMap.put(JcrConstants.JCR_LASTMODIFIED, timestamp);
                    valueMap.put(JcrConstants.JCR_LAST_MODIFIED_BY, user);
                } else {
                    importRequest
                            .addMessage(new MicrositeImportStatus.Message(MicrositeImportStatus.MessageLevel.error,
                                    "No entry point (" + INDEX_FILE + ") found in site content!"));
                }
                if (LOG.isInfoEnabled()) {
                    LOG.info("importSiteContent({}): completed: {}", pageContent,
                            importRequest.isSuccessful() ? "success" : "failed");
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                importRequest.addMessage(
                        new MicrositeImportStatus.Message(MicrositeImportStatus.MessageLevel.error,
                                "Error on ZIP import: \"{0}\" ({1}).",
                                importFile.getFileName(), ex.getLocalizedMessage()));
            }
        }
        return importRequest;
    }

    protected void importZipEntry(MicrositeImportRequest importRequest, ZipEntry zipEntry) {
        Resource contentResource = importRequest.getPageContent();
        String entryName = zipEntry.getName();
        if (!zipEntry.isDirectory()) {
            if (isAllowedEntry(importRequest, entryName.toLowerCase())) {
                int lastSlash = entryName.lastIndexOf('/');
                String name = entryName.substring(lastSlash + 1);
                String path = lastSlash > 0 ? entryName.substring(0, lastSlash) : "";
                Resource parentResource = contentResource;
                if (StringUtils.isNotBlank(path)) {
                    try {
                        importProvider.createFolder(contentResource, path);
                        parentResource = contentResource.getChild(path);
                    } catch (Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                        importRequest.addMessage(
                                new MicrositeImportStatus.Message(MicrositeImportStatus.MessageLevel.error,
                                        "Error on creating folder: \"{0}\" ({1}).",
                                        path, ex.getLocalizedMessage()));
                    }
                }
                importFile(importRequest, parentResource, name);
            } else {
                LOG.warn("entry not allowed: {}", entryName);
            }
        }
    }

    protected void importFile(MicrositeImportRequest importRequest, Resource parentResource, String name) {
        try {
            String extension = StringUtils.substringAfterLast(name, ".");
            switch (extension.toLowerCase()) {
                case "html":
                case "jsp":
                    importHtmlFile(importRequest, parentResource, name);
                    break;
                case "css":
                case "scss":
                    importCssFile(importRequest, parentResource, name);
                    break;
                default:
                    importFileBinary(importRequest, parentResource, name);
                    break;
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            importRequest
                    .addMessage(
                            new MicrositeImportStatus.Message(MicrositeImportStatus.MessageLevel.error, "Error on importing file \"{0}\" ({1}).",
                                    importRequest.getRelativeBase(parentResource) + "/" + name, ex.getLocalizedMessage()));
        }
    }

    protected void importHtmlFile(MicrositeImportRequest importRequest, Resource parentResource, String name)
            throws IOException {
        Resource contentResource = importRequest.getPageContent();
        String currentIndex = importProvider.getProperty(contentResource, PN_INDEX_PATH, "");
        boolean isIndex = INDEX_FILE.equals(name);
        String relativeBase = importRequest.getRelativeBase(parentResource);
        if (isIndex) {
            String parentPath = parentResource.getPath();
            String contentPath = contentResource.getPath();
            String indexPath;
            if (StringUtils.equals(parentPath, contentPath)) {
                indexPath = name;
            } else {
                indexPath = parentPath.substring(contentPath.length() + 1) + "/" + name;
            }
            if (StringUtils.isNotBlank(currentIndex)) {
                importRequest.addMessage(new MicrositeImportStatus.Message(MicrositeImportStatus.MessageLevel.error,
                        "More than one entry point ('" + INDEX_FILE + "') found ({0}, {1}) - must be unique!", currentIndex, indexPath));
            } else {
                importProvider.setProperty(importRequest.getPageContent(), PN_INDEX_PATH, indexPath);
                currentIndex = indexPath;
                // perform the source transformation of all delayed sources...
                MicrositeSourceTransformer transformer = importRequest.getSourceTransformer();
                for (MicrositeImportRequest.DelayedTransformation delayed : importRequest.getDelayedTransformations()) {
                    importProvider.storeSourceFile(delayed.parent, delayed.name, transformer
                            .transformHtml(importRequest.getRelativeBase(delayed.parent), delayed.content, false, currentIndex));
                }
            }
        }
        ZipInputStream zipStream = importRequest.getZipStream();
        String htmlSource = IOUtils.toString(zipStream, SOURCE_ENCODING);
        if (StringUtils.isNotBlank(currentIndex)) {
            htmlSource = importRequest.getSourceTransformer().transformHtml(relativeBase, htmlSource, isIndex, currentIndex);
            importProvider.storeSourceFile(parentResource, name, htmlSource);
        } else {
            // delay the source transformation and do it later with the found index path
            importRequest.addDelayedTransformation(parentResource, name, htmlSource);
        }
    }

    protected void importCssFile(MicrositeImportRequest importRequest, Resource parentResource, String name)
            throws IOException {
        ZipInputStream zipStream = importRequest.getZipStream();
        String cssSource = IOUtils.toString(zipStream, SOURCE_ENCODING);
        importProvider.storeSourceFile(parentResource, name, cssSource);
    }

    protected void importFileBinary(MicrositeImportRequest importRequest, Resource parentResource, String name)
            throws IOException {
        importProvider.storeBinaryFile(parentResource, name, importRequest.getZipStream());
    }

    // the repository import provider

    /**
     * CRUD resource creation properties
     */
    public static final Map<String, Object> CRUD_FOLDER_PROPS;
    public static final Map<String, Object> CRUD_FILE_PROPS;
    public static final Map<String, Object> CRUD_FILE_CONTENT_PROPS;

    static {
        CRUD_FOLDER_PROPS = new HashMap<>();
        CRUD_FOLDER_PROPS.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FOLDER);
        CRUD_FILE_PROPS = new HashMap<>();
        CRUD_FILE_PROPS.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FILE);
        CRUD_FILE_CONTENT_PROPS = new HashMap<>();
        CRUD_FILE_CONTENT_PROPS.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_RESOURCE);
    }

    /**
     * the import provider repository implementation for the ZIP file based site import into one page
     */
    public class RepositoryImportProvider implements MicrositeImportProvider {

        @Override
        public <T> T getProperty(Resource resource, String name, T defaultValue) {
            return resource.getValueMap().get(name, defaultValue);
        }

        @Override
        public void setProperty(Resource resource, String name, Object value) {
            ModifiableValueMap valueMap = resource.adaptTo(ModifiableValueMap.class);
            if (valueMap != null) {
                if (value != null) {
                    valueMap.put(name, value);
                } else {
                    valueMap.remove(name);
                }
            }
        }

        @Override
        public void clearContent(Resource contentRoot) throws PersistenceException {
            ResourceResolver resolver = contentRoot.getResourceResolver();
            for (Resource child : contentRoot.getChildren()) {
                if (child.isResourceType(JcrConstants.NT_FOLDER) || child.isResourceType(JcrConstants.NT_FILE)) {
                    resolver.delete(child);
                }
            }
            Calendar timestamp = new GregorianCalendar();
            timestamp.setTime(new Date());
            String user = resolver.getUserID();
            ModifiableValueMap valueMap = contentRoot.adaptTo(ModifiableValueMap.class);
            if (valueMap != null) {
                valueMap.remove(PN_INDEX_PATH);
                valueMap.remove(PN_LAST_IMPORT_TIME);
                valueMap.remove(PN_LAST_IMPORT_FILE);
                valueMap.remove(PN_LAST_IMPORT_SIZE);
                valueMap.put(JcrConstants.JCR_LASTMODIFIED, timestamp);
                valueMap.put(JcrConstants.JCR_LAST_MODIFIED_BY, user);
            }
        }

        @Override
        public void createFolder(Resource contentRoot, String path) throws PersistenceException {
            buildPath(contentRoot, path);
        }

        /**
         * creates a folder and all necessary parents if not available
         */
        protected Resource buildPath(Resource contentRoot, String path) throws PersistenceException {
            Resource folder = StringUtils.isNotBlank(path) ? contentRoot.getChild(path) : contentRoot;
            if (folder == null) {
                int lastSlash = path.lastIndexOf('/');
                String name = path.substring(lastSlash + 1);
                String parentPath = lastSlash > 0 ? path.substring(0, lastSlash) : "";
                Resource parentResource = StringUtils.isNotBlank(parentPath) ? buildPath(contentRoot, parentPath) : contentRoot;
                ResourceResolver resolver = contentRoot.getResourceResolver();
                folder = resolver.create(parentResource, name, CRUD_FOLDER_PROPS);
            }
            return folder;
        }

        @Override
        public void storeSourceFile(Resource parentResource, String name, String sourceContent)
                throws PersistenceException {
            storeBinaryFile(parentResource, name, new ReaderInputStream(new StringReader(sourceContent), SOURCE_ENCODING));
        }

        @Override
        public void storeBinaryFile(Resource parentResource, String name, InputStream content)
                throws PersistenceException {
            ResourceResolver resolver = parentResource.getResourceResolver();
            Resource fileResource = resolver.create(parentResource, name, CRUD_FILE_PROPS);
            Map<String, Object> contentProperties = new HashMap<>(CRUD_FILE_CONTENT_PROPS);
            String value;
            contentProperties.put(JcrConstants.JCR_DATA,
                    content instanceof ZipInputStream ? new ClosePreventingStreamWrapper(content) : content);
            value = getMimeType(name);
            if (StringUtils.isNotBlank(value)) {
                contentProperties.put(JcrConstants.JCR_MIMETYPE, value);
            }
            resolver.create(fileResource, JcrConstants.JCR_CONTENT, contentProperties);
        }

        @Override
        public String getCurrentUser(Resource contentRoot) {
            return contentRoot.getResourceResolver().getUserID();
        }
    }

    /**
     * a helper class to avoid closing of the ZIP input stream by importing a file of the stream
     */
    public static class ClosePreventingStreamWrapper extends InputStream {

        protected final InputStream wrappedStream;

        public ClosePreventingStreamWrapper(InputStream wrappedStream) {
            this.wrappedStream = wrappedStream;
        }

        @Override
        public void close() {
            // do nothing an let the ZIP stream open
        }

        @Override
        public int read() throws IOException {
            return wrappedStream.read();
        }

        @Override
        public int read(@Nonnull byte b[]) throws IOException {
            return wrappedStream.read(b);
        }

        @Override
        public int read(@Nonnull byte b[], int off, int len) throws IOException {
            return wrappedStream.read(b, off, len);
        }

    }


    @Activate
    @Modified
    public void activate(final Configuration config) {
        this.config = config;
        this.whitelist = new ArrayList<>();
        for (String rule : config.whitelist()) {
            if (StringUtils.isNotBlank(rule)) {
                this.whitelist.add(Pattern.compile(rule));
            }
        }
        this.blacklist = new ArrayList<>();
        for (String rule : config.blacklist()) {
            if (StringUtils.isNotBlank(rule)) {
                this.blacklist.add(Pattern.compile(rule));
            }
        }
        this.ignored = new ArrayList<>();
        for (String rule : config.ignored()) {
            if (StringUtils.isNotBlank(rule)) {
                this.ignored.add(Pattern.compile(rule));
            }
        }
    }
}
