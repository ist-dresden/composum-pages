package com.composum.pages.stage.model.tools;

import com.composum.pages.commons.model.GenericModel;
import com.composum.pages.commons.model.properties.Language;
import com.composum.pages.commons.service.VersionsService;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import javax.jcr.version.VersionManager;
import java.util.Locale;

/**
 * a model implemetation for a comparator root node which can be used in templates
 */
public class PropertiesComparatorRoot extends PropertiesComparatorNode {

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesComparatorRoot.class);

    public static class ComparatorRef extends GenericModel {

        protected Locale locale;
        protected String version;

        public ComparatorRef(@Nonnull final BeanContext context, @Nonnull final Resource resource,
                             @Nullable final Locale locale, @Nonnull final String version) {
            super(context, resource);
            this.locale = locale;
            this.version = version;
        }

        @Override
        @Nonnull
        public Language getLanguage() {
            Language language = null;
            Locale locale = getLocale();
            if (locale != null) {
                language = getLanguages().getLanguage(locale.getLanguage());
            }
            return language != null ? language : super.getLanguage();
        }

        @Nullable
        public Locale getLocale() {
            return locale;
        }

        @Nonnull
        public String getVersion() {
            return version;
        }
    }

    public static ComparatorRef rootNode(@Nonnull BeanContext context,
                                         @Nullable final String path,
                                         @Nullable final Locale locale,
                                         @Nullable final String versionUuid) {
        ComparatorRef result = null;
        if (StringUtils.isNotBlank(path)) {
            String version = "";
            ResourceResolver resolver = context.getResolver();
            Resource resource = null;
            if (StringUtils.isNotBlank(versionUuid)) {
                try {
                    VersionsService versionsService = context.getService(VersionsService.class);
                    resource = versionsService.historicalVersion(resolver, path, versionUuid);
                    if (resource != null) {
                        Resource versionResource = ResourceUtil.getByUuid(resolver, versionUuid);
                        if (versionResource != null) {
                            version = versionResource.getName();
                        }
                        context = new BeanContext.Wrapper(context, resource.getResourceResolver());
                    }
                } catch (RepositoryException ex) {
                    LOG.error(ex.toString());
                }
            } else {
                resource = resolver.getResource(path);
            }
            if (resource != null) {
                result = new ComparatorRef(context, resource, locale, version);
            }
        }
        return result;
    }

    private transient VersionManager versionManager;

    public PropertiesComparatorRoot(@Nonnull final BeanContext context,
                                    @Nullable final String leftPath,
                                    @Nullable final Locale leftLocale,
                                    @Nullable final String leftVersionUuid,
                                    @Nullable final String rightPath,
                                    @Nullable final Locale rightLocale,
                                    @Nullable final String rightVersionUuid) {
        super(rootNode(context, leftPath, leftLocale, leftVersionUuid),
                rootNode(context, rightPath, rightLocale, rightVersionUuid));
    }

    @Override
    @Nullable
    public ComparatorRef getLeft() {
        return (ComparatorRef) super.getLeft();
    }

    @Override
    @Nullable
    public ComparatorRef getRight() {
        return (ComparatorRef) super.getRight();
    }

    public boolean isEqualPath() {
        ComparatorRef left = getLeft();
        ComparatorRef right = getRight();
        return left != null && right != null && left.getPath().equals(right.getPath());
    }

    public boolean isEqualLanguage() {
        ComparatorRef left = getLeft();
        ComparatorRef right = getRight();
        return left != null && right != null && left.getLanguage().equals(right.getLanguage());
    }

    public boolean isEqualVersion() {
        ComparatorRef left = getLeft();
        ComparatorRef right = getRight();
        return left != null && right != null && left.getVersion().equals(right.getVersion());
    }
}
