package com.composum.pages.options.wiki.impl;

import com.composum.sling.core.util.LinkUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;

import java.io.Writer;

/**
 *
 */
public class WikitextDocumentBuilder extends HtmlDocumentBuilder {

    protected SlingHttpServletRequest request;

    public WikitextDocumentBuilder(SlingHttpServletRequest request, Writer writer) {
        super(writer);
        this.request = request;
    }

    @Override
    protected String makeUrlAbsolute(String url) {
        return LinkUtil.getUrl(request, url);
    }
}
