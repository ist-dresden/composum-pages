package com.composum.pages.options.wiki.model;

import com.composum.pages.commons.model.Element;
import com.composum.pages.options.wiki.WikiHtmlBuilder;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class Markup extends Element {

    private static final Logger LOG = LoggerFactory.getLogger(Markup.class);

    public static final String PN_WIKI_CODE = "wikiCode";
    public static final String PN_WIKI_REF = "wikiRef";
    public static final String[] WIKI_FILE_NAMES = new String[]{
            "wikifile.txt", "wikifile.md", "wikifile"
    };

    public static final String PN_WIKI_TYPE = "type";
    public static final String WIKI_TYPE_DEFAULT = WikiHtmlBuilder.CONFLUENCE;

    public static final List<String> WIKI_TYPES = Arrays.asList(
            WikiHtmlBuilder.CONFLUENCE,
            WikiHtmlBuilder.MARKDOWN,
            WikiHtmlBuilder.MEDIAWIKI,
            WikiHtmlBuilder.TEXTILE,
            WikiHtmlBuilder.TRACWIKI,
            WikiHtmlBuilder.TWIKI
    );

    protected transient String markup;

    public String getMarkup() {
        if (markup == null) {
            String wikiType = getWikiType();
            String wikiCode = getWikiCode();
            WikiHtmlBuilder builder = context.getService(WikiHtmlBuilder.class);
            markup = builder.buildMarkup(context.getRequest(), wikiCode, wikiType);
        }
        return markup;
    }

    public List<String> getWikiTypes() {
        return WIKI_TYPES;
    }

    public String getWikiType() {
        return getProperty(PN_WIKI_TYPE, null, WIKI_TYPE_DEFAULT);
    }

    public String getWikiCode() {
        String wikiCode = getProperty(PN_WIKI_CODE, "");
        if (StringUtils.isBlank(wikiCode)) {
            Resource content = null;
            String wikiRef = getProperty(PN_WIKI_REF, "");
            if (StringUtils.isNotBlank(wikiRef)) {
                content = resolver.getResource(resource, wikiRef);
            }
            if (content == null) {
                content = determineFile(resource, "");
            }
            if (content == null) {
                content = determineFile(resource, ResourceUtil.CONTENT_NODE + "/");
            }
            if (content == null) {
                content = resource;
            }
            Binary binary = ResourceUtil.getBinaryData(content);
            if (binary != null) {
                try {
                    wikiCode = IOUtils.toString(binary.getStream(), StandardCharsets.UTF_8);
                } catch (RepositoryException | IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
        return wikiCode;
    }

    protected Resource determineFile(Resource resource, String path) {
        for (String name : WIKI_FILE_NAMES) {
            Resource file = resource.getChild(path + name);
            if (file != null) {
                return file;
            }
        }
        return null;
    }
}
