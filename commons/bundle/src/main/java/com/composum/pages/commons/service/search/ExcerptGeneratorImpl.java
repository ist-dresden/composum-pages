package com.composum.pages.commons.service.search;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.composum.sling.core.util.ResourceUtil.*;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Collections.singleton;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A heuristic to generate an excerpt for a resource.
 */
public class ExcerptGeneratorImpl implements ExcerptGenerator {

    private static final Logger LOG = getLogger(ExcerptGeneratorImpl.class);

    /** Number of additional characters surrounding searched words. */
    protected int contextLength = 40;

    /** Ignore fields of less than this size. */
    protected int minContextLength = 10;

    @Override
    public String excerpt(Resource resource, String searchExpression) throws SearchTermParseException {
        return excerpt(Arrays.asList(resource), searchExpression);
    }

    @Override
    public String excerpt(List<Resource> resourceList, String searchExpression) throws SearchTermParseException {
        Set<String> words = extractSearchterms(searchExpression);
        if (words.isEmpty()) return "";
        Pattern wordregex = wordregex(words);
        Set<String> contexts = new LinkedHashSet<>();
        for (Resource resource : resourceList) {
            for (Map.Entry<String, Object> entry : resource.getValueMap().entrySet()) {
                if (ignoredKey(entry.getKey())) continue;
                try {
                    String value;
                    if (entry.getValue() instanceof String) value = (String) entry.getValue();
                    else if (entry.getValue() instanceof InputStream) // jcr:data gives a LazyInputStream.
                        value = IOUtils.toString((InputStream) entry.getValue(), "UTF-8");
                    else continue;
                    collectContexts(contexts, normalize(value), wordregex);
                } catch (IOException e) {
                    LOG.error("Could not read key {} of {}", entry.getKey(), resource.getPath());
                }
            }
        }
        Set<String> excerpts = new LinkedHashSet<>();
        collectExcerptParts(excerpts, contexts, words);
        String result = excerpts.isEmpty() ? "": "... " + StringUtils.join(excerpts, " ... ") + " ...";
        return markWords(result, wordregex);
    }

    public boolean ignoredKey(String key) {
        if (key.startsWith("jcr:") || key.startsWith("nt:") || key.startsWith("rep:")) {
            if (PROP_TITLE.equals(key) || PROP_DESCRIPTION.equals(key) || PROP_DATA.equals(key)) return false;
            return true;
        }
        return false;
    }

    protected void collectContexts(Set<String> contexts, String text, Pattern wordregex) {
        int begin = Integer.MIN_VALUE;
        int end = Integer.MIN_VALUE;
        for (Matcher m = wordregex.matcher(text); m.find(); ) {
            if (m.start() - contextLength > end) {
                if (end > 0) contexts.add(text.substring(begin, end));
                begin = max(0, m.start() - contextLength);
                end = min(text.length(), m.end() + contextLength);
            } else if (!text.substring(begin, end).contains(m.group())) {
                end = min(text.length(), m.end() + contextLength);
            }
            while (begin > 0 && Character.isAlphabetic(text.charAt(begin))
                    && Character.isAlphabetic(text.charAt(begin - 1))) --begin;
            while (end < text.length() - 1 && Character.isAlphabetic(text.charAt(end - 1))
                    && Character.isAlphabetic(text.charAt(end))) ++end;
        }
        if (end > 0) {
            contexts.add(text.substring(begin, end));
        }
    }

    protected void collectExcerptParts(Set<String> excerpts, Set<String> contexts, Set<String> words) {
        if (words.isEmpty() || contexts.isEmpty()) return;
        String bestExcerpt = null;
        int bestWordcount = -1;
        for (String context : contexts) {
            if (context.length() < minContextLength) continue;
            int wordcount = countWords(context, words);
            if (wordcount > bestWordcount || (wordcount == bestWordcount && context.length() < bestExcerpt.length())) {
                bestExcerpt = context;
                bestWordcount = wordcount;
            }
        }
        if (null != bestExcerpt) excerpts.add(bestExcerpt.trim());
        Set<String> restWords = new LinkedHashSet<>();
        String bufContent = StringUtils.join(excerpts, " ");
        for (String word : words) if (!bufContent.contains(word)) restWords.add(word);
        if (!restWords.isEmpty() && restWords.size() != words.size()) collectExcerptParts(excerpts, contexts, restWords);
    }

    private int countWords(String context, Set<String> words) {
        int count = 0;
        for (String word : words) if (wordregex(singleton(word)).matcher(context).find()) count++;
        return count;
    }

    protected Pattern wordregex(Set<String> words) {
        StringBuilder buf = new StringBuilder("(");
        for (String word : words) {
            if (buf.length() > 1) buf.append("|");
            if (word.contains("*") || word.contains("~") || word.contains("?") || word.contains(" "))
                buf.append(metacharRegex(word));
            else buf.append(Pattern.quote(word));
        }
        buf.append(")");
        return Pattern.compile(buf.toString(), Pattern.CASE_INSENSITIVE);
    }

    private String metacharRegex(String word) {
        word = word.replaceAll("^\\*", "\\\\b\\\\S*");
        word = word.replaceAll("\\*$", "\\\\S*\\\\b");
        if (word.contains(" ")) word = word.replaceAll("\\*", ".*?");
        else word = word.replaceAll("\\*", "\\\\S*");
        word = word.replaceAll("\\?", "\\\\S");
        word = word.replaceAll(" ", "\\\\s+");
        return word;
    }

    protected String markWords(String excerpt, Pattern wordregex) {
        return wordregex.matcher(excerpt).replaceAll("<b>$1</b>");
    }

    protected Set<String> extractSearchterms(String searchExpression) throws SearchTermParseException {
        Set<String> res = new LinkedHashSet<>();
        for(String term : new SearchtermParser(searchExpression).getPositiveSearchterms()) {
            term = term.replaceAll("[^\\p{javaAlphabetic}\\d'*?\\s]+", "").replaceAll("\\s+", " ");
            if (isNotBlank(term)) res.add(term);
        }
        return res;
    }

    protected String normalize(String text) {
        return (" " + text + " ").replaceAll("[^\\p{javaAlphabetic}\\d\\s,.!?':-]+", " ");
    }

}
