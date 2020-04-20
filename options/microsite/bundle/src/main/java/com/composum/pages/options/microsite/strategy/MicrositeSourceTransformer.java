package com.composum.pages.options.microsite.strategy;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * this transformer is rewriting relative URLs in the source files of an imported ZIP site
 */
public class MicrositeSourceTransformer {

    /**
     * url transformation patterns - (1: tag start (2: 'tag'|'tag') (3: keep it))(4: URL)(5: ... tag end))
     */
    public static final Pattern PTN_HTML_HREF = Pattern
            .compile("(<\\s*(a|link)\\s+([^>]+\\s+)?href\\s*=\\s*[\"'])([^\"'#]+)([\"'][^>]*>)");
    public static final Pattern PTN_HTML_SRC = Pattern
            .compile("(<\\s*(img|audio|video|source|script|frame)\\s+([^>]+\\s+)?src\\s*=\\s*[\"'])([^\"']+)([\"'][^>]*>)");
    public static final Pattern PTN_HTML_DATA = Pattern
            .compile("(<\\s*(div)\\s+([^>]+\\s+)?data-file\\s*=\\s*[\"'])([^\"']+)([\"'][^>]*>)");
    public static final Pattern PTN_CSS_URL = Pattern
            .compile("((background|background-image)\\s*:\\s*([^;}]+\\s+)?url\\s*\\(\\s*[\"']?)([^'\")]+)([\"']?\\s*\\))");

    protected static final int TAG_START_GROUP = 1;
    protected static final int EXTRACT_URL_GROUP = 4;
    protected static final int TAG_END_GROUP = 5;

    /**
     * decide between external or absolute and relative URLs (1: scheme (3: host, 4: port))(5: absolute path)
     */
    public static final Pattern PTN_EXT_ABS_URL = Pattern.compile("^(https?:(//([^:/]+)(:[\\d]+)?)?)?(/.*)$");

    /**
     * the base URL for all relative URLs in the source
     */
    protected final String htmlRoot; // the HTML root should reference the landing page as proxy
    protected final String fileRoot; // all resource files should be referenced directly (via '/_jcr_content')

    /**
     * constructor with the URL (path) of the landing page as general base for all sources
     *
     * @param htmlRoot the base URL for all relative URLs to HTML targets in the source
     * @param fileRoot the base URL for all relative URLs to non HTML targets in the source
     */
    public MicrositeSourceTransformer(String htmlRoot, String fileRoot) {
        this.htmlRoot = htmlRoot;
        this.fileRoot = fileRoot;
    }

    /**
     * build the URL relative to the microsite root relative to url base of the transformed source
     *
     * @param relBase the base path of the source relative to the source root
     * @param url     the URL from the original source to transform
     * @param isIndex 'true' if transforming the 'index.html' file
     * @return the transformed URL in relation to the root base
     */
    public String transformUrl(String relBase, String url, boolean isIndex, String indexPath) {
        StringBuilder transformedUrl = null;
        Matcher external = PTN_EXT_ABS_URL.matcher(url);
        if (!external.matches()) { // ignore external URLs
            String targetExt = StringUtils.substringAfterLast(url, ".");
            boolean isHtmlTarget = "html".equalsIgnoreCase(targetExt);
            String rootUrl = isHtmlTarget ? htmlRoot : fileRoot; // use different paths for HTML links and resource files
            transformedUrl = new StringBuilder(rootUrl + relBase + "/" + url);
            transformedUrl = new StringBuilder(transformedUrl.toString().replaceAll("/\\./", "/")); // remove '/.' path segments
            transformedUrl = new StringBuilder(transformedUrl.toString().replaceAll("/[^/]+/\\.\\./", "/"));
            if (!rootUrl.startsWith("/")) {
                if (transformedUrl.toString().equals(htmlRoot + (indexPath.startsWith("/") ? indexPath : "/" + indexPath))) {
                    transformedUrl = new StringBuilder(htmlRoot);
                }
                if (!isIndex) {
                    // let's start with the folder of the landing page if a content file - not the index - references something
                    transformedUrl.insert(0, StringUtils.repeat("../", StringUtils.countMatches(htmlRoot + relBase, "/") + 1));
                }
            }
        }
        return transformedUrl != null ? transformedUrl.toString() : url;
    }

    /**
     * transforms an HTML source file and rewrites all URLs to use the landing page as base URL
     *
     * @param relBase the base path of the transformed file relative to the root URL
     * @param source  the source code to transform
     * @param isIndex 'true' if transforming the 'index.html' file
     * @return the transformed source code
     */
    public String transformHtml(String relBase, String source, boolean isIndex, String indexPath) {
        String result = source;
        result = transform(relBase, result, isIndex, indexPath, PTN_HTML_HREF);
        result = transform(relBase, result, isIndex, indexPath, PTN_HTML_SRC);
        result = transform(relBase, result, isIndex, indexPath, PTN_HTML_DATA);
        return result;
    }

    /**
     * transforms the source string applying one pattern
     *
     * @param relBase the base path of the transformed file relative to the root URL
     * @param source  the source code to transform
     * @param isIndex 'true' if transforming the 'index.html' file
     * @param pattern the pattern to apply
     * @return the transformed source code
     */
    protected String transform(String relBase, String source, boolean isIndex, String indexPath, Pattern pattern) {
        StringBuilder transformed = new StringBuilder();
        int len = source.length();
        int pos = 0;
        Matcher matcher = pattern.matcher(source);
        while (matcher.find(pos)) {
            String url = matcher.group(EXTRACT_URL_GROUP); // extract URL from pattern
            url = transformUrl(relBase, url, isIndex, indexPath); // and transform the URL
            transformed.append(source.substring(pos, matcher.start())); // copy up to the start of the pattern
            transformed.append(matcher.group(TAG_START_GROUP)); // copy unmodified start sequence of the pattern
            transformed.append(url); // add transformed URL part of the pattern
            transformed.append(matcher.group(TAG_END_GROUP)); // copy unmodified end sequence of the pattern
            pos = matcher.end(); // continue at position at the end of the pattern
        }
        if (pos >= 0 && pos < len) {
            transformed.append(source.substring(pos)); // copy the rest of the source
        }
        return transformed.toString();
    }
}
