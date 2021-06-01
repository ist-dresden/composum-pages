package com.composum.pages.options.wiki.impl;

import com.composum.pages.options.wiki.WikiHtmlBuilder;
import com.composum.pages.options.wiki.WikiHtmlRenderer;
import org.apache.sling.api.SlingHttpServletRequest;
import org.eclipse.mylyn.wikitext.confluence.ConfluenceLanguage;
import org.eclipse.mylyn.wikitext.markdown.MarkdownLanguage;
import org.eclipse.mylyn.wikitext.mediawiki.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.textile.TextileLanguage;
import org.eclipse.mylyn.wikitext.tracwiki.TracWikiLanguage;
import org.eclipse.mylyn.wikitext.twiki.TWikiLanguage;
import org.osgi.service.component.annotations.Component;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides the transformation of Wiki text content to HTML markup text.
 */
@Component(
        name = "Composum Wiki Markup Builder",
        immediate = true,
        service = WikiHtmlBuilder.class
)
public class DefaultHtmlBuilder implements WikiHtmlBuilder {

    public final Map<String, WikiHtmlRenderer> languageMap;

    public DefaultHtmlBuilder() {
        languageMap = new HashMap<>();
        languageMap.put(CONFLUENCE, new WikitextLanguageRenderer(new ConfluenceLanguage()));
        languageMap.put(MARKDOWN, new WikitextLanguageRenderer(new MarkdownLanguage()));
        languageMap.put(MEDIAWIKI, new WikitextLanguageRenderer(new MediaWikiLanguage()));
        languageMap.put(TEXTILE, new WikitextLanguageRenderer(new TextileLanguage()));
        languageMap.put(TRACWIKI, new WikitextLanguageRenderer(new TracWikiLanguage()));
        languageMap.put(TWIKI, new WikitextLanguageRenderer(new TWikiLanguage()));
    }

    @Override
    public String buildMarkup(SlingHttpServletRequest request, String text, String language) {
        StringWriter writer = new StringWriter();
        buildMarkup(request, text, language, writer);
        return writer.toString();
    }

    @Override
    public void buildMarkup(SlingHttpServletRequest request, String text, String language, Writer writer) {
        WikiHtmlRenderer renderer = languageMap.get(language.toLowerCase());
        renderer.renderMarkup(request, text, writer);
    }
}
