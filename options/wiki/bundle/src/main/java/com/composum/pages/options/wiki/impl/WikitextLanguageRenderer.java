package com.composum.pages.options.wiki.impl;

import com.composum.pages.options.wiki.WikiHtmlRenderer;
import org.apache.sling.api.SlingHttpServletRequest;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;

import java.io.Writer;

public class WikitextLanguageRenderer implements WikiHtmlRenderer {

    MarkupLanguage language;

    public WikitextLanguageRenderer(MarkupLanguage language) {
        this.language = language;
    }

    @Override
    public void renderMarkup(SlingHttpServletRequest request, String text, Writer writer) {
        HtmlDocumentBuilder builder = createHtmlDocumentBuilder(request, writer);
        MarkupParser markupParser = new MarkupParser(language);
        markupParser.setBuilder(builder);
        markupParser.parse(text);
    }

    protected HtmlDocumentBuilder createHtmlDocumentBuilder(SlingHttpServletRequest request, Writer writer) {
        HtmlDocumentBuilder builder = new WikitextDocumentBuilder(request, writer);
        // avoid the <html> and <body> tags
        builder.setEmitAsDocument(false);
        return builder;
    }
}
