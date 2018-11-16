/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.taglib;

import com.composum.pages.commons.filter.DropZoneFilter;
import com.composum.pages.commons.model.properties.DropZone;
import com.composum.pages.commons.model.properties.Languages;
import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.request.PagesLocale;
import com.composum.pages.commons.service.ResourceManager;
import com.composum.pages.commons.util.ResourceTypeUtil;
import com.composum.pages.commons.util.TagCssClasses;
import com.composum.platform.models.annotations.InternationalizationStrategy.I18NFOLDER;
import com.composum.sling.core.util.ResourceUtil;
import com.google.gson.JsonObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * a tag to declare a drop zone to change a property by dropping an asset or component (edit mode only)
 */
public class DropZoneTag extends ModelTag {

    private static final Logger LOG = LoggerFactory.getLogger(DropZoneTag.class);

    public static final String DROP_ZONE_VAR = "dropZone";
    public static final String DROP_ZONE_CSS_VAR = DROP_ZONE_VAR + "CssBase";

    public static final String DEFAULT_TAGNAME = "div";
    public static final String DEFAULT_TYPE = "asset";
    public static final String DEFAULT_FILTER = DEFAULT_TYPE + ":all";

    public static final String DROP_ZONE_CSS_KEY = "_drop-zone";
    public static final String DROP_ZONE_CSS_CLASS = EDIT_CSS_BASE + DROP_ZONE_CSS_KEY;

    public static final String TAG_ID_PREFIX = "pages-edit-dz_";
    public static final String RA_STATUS = DropZoneTag.class.getSimpleName() + "#status";

    protected static class RequestStatus {

        public static RequestStatus getInstance(SlingHttpServletRequest request) {
            RequestStatus instance = (RequestStatus) request.getAttribute(RA_STATUS);
            if (instance == null) {
                request.setAttribute(RA_STATUS, instance = new RequestStatus());
            }
            return instance;
        }

        // using a timestamp to make ids different even if a drop zone is load lazy via Ajax
        protected final String tstamp = new SimpleDateFormat("yyyyMMddHHmmssSSS")
                .format(new Date(System.currentTimeMillis()));
        protected int count = 0;

        public String newId() {
            return TAG_ID_PREFIX + tstamp + "_" + (++count);
        }
    }

    protected String tagName;
    protected Resource propertyResource;
    protected String propertyPath;
    protected String resourcePath;
    protected String property;
    protected boolean i18n = false;
    protected String filter;
    protected String successEvent;

    @Override
    protected void clear() {
        filter = null;
        i18n = false;
        property = null;
        resourcePath = null;
        propertyPath = null;
        propertyResource = null;
        tagName = null;
        super.clear();
    }

    // tag attribute setters

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public void setResource(Resource resource) {
        this.propertyResource = resource;
    }

    public void setResourcePath(String path) {
        this.resourcePath = path;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setI18n(boolean i18n) {
        this.i18n = i18n;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setSuccessEvent(String event) {
        this.successEvent = event;
    }

    // tag attribute retrieval

    @Nonnull
    public String getTagName() {
        return StringUtils.isNotBlank(tagName) ? tagName : DEFAULT_TAGNAME;
    }

    @Nonnull
    protected Resource getPropertyResource() {
        if (propertyResource == null) {
            if (StringUtils.isNotBlank(resourcePath)) {
                propertyResource = context.getResolver().getResource(resourcePath);
            }
            if (propertyResource == null) {
                propertyResource = getModelResource(context);
            }
            if (propertyResource == null) {
                propertyResource = getResource();
            }
        }
        return propertyResource;
    }

    @Nonnull
    public String getResourcePath() {
        return getPropertyResource().getPath();
    }

    @Nonnull
    public String getPropertyPath() {
        if (propertyPath == null) {
            propertyPath = property;
            PagesLocale pagesLocale;
            if (i18n && (pagesLocale = context.getRequest().adaptTo(PagesLocale.class)) != null) {
                Languages languages = Languages.get(context);
                if (languages != null && languages.size() > 0) {
                    Locale locale = pagesLocale.getLocale();
                    if (!languages.getDefaultLanguage().getKey().equals(locale.getLanguage()))
                        propertyPath = I18NFOLDER.getI18nPath(locale, propertyPath);
                }
            }
        }
        return propertyPath;
    }

    @Override
    public String getVar() {
        return null; // create not a bean (model) for this tag
    }

    @Nonnull
    public String getType() {
        String type = StringUtils.substringBefore(getFilter(), ":");
        return StringUtils.isNotBlank(type) ? type : DEFAULT_TYPE;
    }

    @Nonnull
    public String getFilter() {
        return StringUtils.isNotBlank(filter) ? filter : DEFAULT_FILTER;
    }

    @Nonnull
    public String getSuccessEvent() {
        return StringUtils.isNotBlank(successEvent) ? successEvent : "";
    }

    // tag rendering

    protected String getTagId() {
        return RequestStatus.getInstance(request).newId();
    }

    protected boolean isSyntheticTarget(Resource resource) {
        return ResourceTypeUtil.isSyntheticResource(resource)
                || StringUtils.isBlank(resource.getValueMap().get(ResourceUtil.PROP_RESOURCE_TYPE, ""));
    }


    @Override
    public String buildCssBase() {
        return super.buildCssBase() + DROP_ZONE_CSS_KEY;
    }

    /**
     * collects the set of CSS classes (extension hook)
     * adds the 'cssBase' itself as CSS class and the general CSS class for a drop zone an for the filter set
     */
    @Override
    protected void collectCssClasses(TagCssClasses.CssSet collection) {
        if (StringUtils.isBlank(getTagCssClasses().getCssSet())) {
            String cssBase = getCssBase();
            collection.add(cssBase);
            collection.add(DROP_ZONE_CSS_CLASS);
            DropZoneFilter filter = new DropZoneFilter(context, getFilter());
            for (DropZoneFilter.Type type : filter.getTypes()) {
                for (String key : filter.getKeys(type)) {
                    collection.add(DROP_ZONE_CSS_CLASS + "_" + type + "-" + key);
                }
            }
        }
    }

    /**
     * builds the list of tag attributes for the wrapping tag
     */
    protected void addEditAttributes(@Nonnull Map<String, String> attributeSet) {
        Resource resource = getPropertyResource();
        attributeSet.put(TAG_ID, getTagId());
        attributeSet.put(PAGES_EDIT_DATA_ENCODED,
                Base64.encodeBase64String(getEditData().toString().getBytes(StandardCharsets.UTF_8)));
    }

    protected JsonObject getEditData() {
        JsonObject data = new JsonObject();
        data.addProperty(DropZone.PATH, getResourcePath());
        if (isSyntheticTarget(resource)) {
            // add hints for implicit target creation if the designated target is synthetic
            ResourceManager resourceManager = context.getService(ResourceManager.class);
            ResourceManager.ResourceReference reference = resourceManager.getReference(resource, getResourceType());
            if (reference != null) {
                data.addProperty(DropZone.TYPE, reference.getType());
                data.addProperty(DropZone.PRIM, reference.getPrimaryType());
            }
        }
        data.addProperty(DropZone.PROPERTY, getPropertyPath());
        data.addProperty(DropZone.FILTER, getFilter());
        String event = getSuccessEvent();
        if (StringUtils.isNotBlank(event)) {
            data.addProperty(DropZone.EVENT, event);
        }
        return data;
    }

    /**
     * builds the list of tag attributes for the wrapping tag
     */
    @Override
    protected void collectAttributes(Map<String, String> attributeSet) {
        String cssClasses = buildCssClasses();
        if (StringUtils.isNotBlank(cssClasses)) {
            attributeSet.put("class", cssClasses);
        }
        super.collectAttributes(attributeSet);
        addEditAttributes(attributeSet);
    }

    @Override
    public int doStartTag() throws JspException {
        super.doStartTag();
        if (DisplayMode.isEditMode(context)) {
            setAttribute(DROP_ZONE_VAR, this, PageContext.REQUEST_SCOPE);
            if (StringUtils.isNotBlank(cssBase)) {
                setAttribute(DROP_ZONE_CSS_VAR, cssBase, PageContext.REQUEST_SCOPE);
            }
            try {
                out.append("<").append(getTagName()).append(getAttributes()).append(">");
            } catch (IOException ioex) {
                LOG.error(ioex.getMessage(), ioex);
            }
        }
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() throws JspException {
        if (DisplayMode.isEditMode(context)) {
            try {
                out.append("</").append(getTagName()).append(">");
            } catch (IOException ioex) {
                LOG.error(ioex.getMessage(), ioex);
            }
        }
        return super.doEndTag();
    }
}
