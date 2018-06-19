package com.composum.pages.commons.service;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.model.Template;
import com.composum.platform.cache.service.CacheConfiguration;
import com.composum.platform.cache.service.CacheManager;
import com.composum.platform.cache.service.impl.CacheServiceImpl;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

@Component(service = TemplateService.class)
@Designate(ocd = PagesTemplateService.Config.class)
public class PagesTemplateService extends CacheServiceImpl<Template> implements TemplateService {

    @ObjectClassDefinition(
            name = "Pages Template Service Configuration"
    )
    public @interface Config {

        @AttributeDefinition(
                description = "the count maximum of templates stored in the cache"
        )
        int maxElementsInMemory() default 1000;

        @AttributeDefinition(
                description = "the validity period maximum for the cache entries in seconds"
        )
        int timeToLiveSeconds() default 600;

        @AttributeDefinition(
                description = "the validity period after last access of a cache entry in seconds"
        )
        int timeToIdleSeconds() default 300;

        @AttributeDefinition()
        String webconsole_configurationFactory_nameHint() default
                "Templates (heap: {maxElementsInMemory}, time: {timeToIdleSeconds}-{timeToLiveSeconds})";
    }

    @Reference
    protected CacheManager cacheManager;

    protected Config config;

    @Nullable
    public Template getTemplateOf(@Nullable Resource resource) {
        Template template = null;
        if (resource != null && !ResourceUtil.isNonExistingResource(resource)) {
            String path = resource.getPath();
            template = get(path);
            if (template == null) {
                template = findTemplateOf(resource);
                put(path, template != null ? template : Template.EMPTY);
            }
        }
        return template != Template.EMPTY ? template : null;
    }

    @Nullable
    protected Template findTemplateOf(@Nonnull Resource resource) {
        Template template = null;
        if (Site.isSite(resource)) {
            return getTemplateOf(resource.getChild(JcrConstants.JCR_CONTENT));
        } else if (Page.isPage(resource)) {
            return getTemplateOf(resource.getChild(JcrConstants.JCR_CONTENT));
        } else {
            String templatePath = resource.getValueMap().get(PagesConstants.PROP_TEMPLATE, "");
            if (StringUtils.isNotBlank(templatePath)) {
                Resource templateResource = resource.getResourceResolver().getResource(templatePath);
                if (templateResource != null && !ResourceUtil.isNonExistingResource(templateResource)) {
                    template = new Template(templateResource);
                }
            } else {
                if (!JcrConstants.JCR_CONTENT.equals(resource.getName())) {
                    Template parentTemplate = getTemplateOf(resource.getParent());
                    if (parentTemplate != null) {
                        Resource templateChild = parentTemplate.getTemplateResource().getChild(resource.getName());
                        template = templateChild != null ? new Template(templateChild) : parentTemplate;
                    }
                }
            }
        }
        return template;
    }

    protected class CacheConfig implements CacheConfiguration {

        @Override
        public boolean enabled() {
            return true;
        }

        @Override
        public String name() {
            return "Templates";
        }

        @Override
        public String contentType() {
            return Template.class.getName();
        }

        @Override
        public int maxElementsInMemory() {
            return config.maxElementsInMemory();
        }

        @Override
        public int timeToLiveSeconds() {
            return config.timeToLiveSeconds();
        }

        @Override
        public int timeToIdleSeconds() {
            return config.timeToIdleSeconds();
        }

        @Override
        public String webconsole_configurationFactory_nameHint() {
            return config.webconsole_configurationFactory_nameHint();
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return config.annotationType();
        }
    }

    @Activate
    @Modified
    public void activate(final Config config) {
        this.config = config;
        super.activate(cacheManager, new CacheConfig());
    }

    @Deactivate
    public void deactivate() {
        super.deactivate();
        config = null;
    }
}
