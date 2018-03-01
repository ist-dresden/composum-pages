package com.composum.pages.commons.model.properties;

import com.composum.pages.commons.model.ResourceReference;
import com.composum.pages.commons.util.ResolverUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * a multi value string property 'allowed...' with a set of resource type patterns
 */
public class AllowedTypes {

    protected List<Pattern> patternList = null;

    public AllowedTypes(@Nonnull ResourceReference reference, @Nonnull String propertyName) {
        this((String) reference.getProperty(propertyName, null));
    }

    public AllowedTypes(@Nonnull ResourceResolver resolver, @Nonnull String resourceType, @Nonnull String propertyName) {
        this((String) ResolverUtil.getTypeProperty(resolver, resourceType, propertyName, null));
    }

    public AllowedTypes(String... typeRules) {
        if (typeRules != null && (typeRules.length == 0 || typeRules[0] != null)) {
            buildPatterns(typeRules);
        }
    }

    protected void buildPatterns(@Nonnull String... typeRules) {
        patternList = new ArrayList<>();
        for (String rule : typeRules) {
            if (StringUtils.isNotBlank(rule)) {
                rule = rule.trim();
                if (rule.length() > 2 && // complete a regex if not always a regex
                        "^.[(".indexOf(rule.charAt(0)) < 0 &&
                        ".*+])?$".indexOf(rule.charAt(rule.length() - 1)) < 0) {
                    if (!rule.startsWith("/")) {
                        rule = ".*" + rule;
                    }
                    rule = "^" + rule + "$";
                }
                patternList.add(Pattern.compile(rule));
            }
        }
    }

    /** is valid if the property is present */
    public boolean isValid() {
        return patternList != null;
    }

    /** is empty if no property found or the value set contains no value */
    public boolean isEmpty() {
        return !isValid() || patternList.isEmpty();
    }

    /**
     * matches if property is valid and one of the rules is matching
     */
    public boolean matches(String resourceType) {
        if (!isEmpty()) {
            for (Pattern pattern : patternList) {
                if (pattern.matcher(resourceType).matches()) {
                    return true;
                }
            }
            return false;
        } else {
            return !isValid();
        }
    }
}
