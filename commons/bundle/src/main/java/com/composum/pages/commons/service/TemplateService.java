package com.composum.pages.commons.service;

import com.composum.pages.commons.model.Template;
import com.composum.platform.cache.service.CacheService;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nullable;

public interface TemplateService extends CacheService<Template> {

    @Nullable
    Template getTemplateOf(@Nullable Resource resource);
}
