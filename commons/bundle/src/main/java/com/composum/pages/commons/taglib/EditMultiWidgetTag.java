package com.composum.pages.commons.taglib;

import com.composum.pages.commons.model.properties.GenericNode;
import com.composum.pages.commons.model.properties.PropertyNode;
import com.composum.pages.commons.model.properties.PropertyNodeSet;
import com.composum.pages.commons.util.TagCssClasses;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.core.LoopTag;
import javax.servlet.jsp.jstl.core.LoopTagStatus;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * the EditMultiWidget is rendering the form content for a structured mutli value node set
 */
public class EditMultiWidgetTag extends AbstractWidgetTag implements LoopTag {

    public static final String MULTIWIDGET_VAR = "multiwidget";
    public static final String MULTIWIDGET_CSS_VAR = MULTIWIDGET_VAR + "CSS";
    public static final String MULTIWIDGET_CSSBASE_VAR = MULTIWIDGET_VAR + "CssBase";
    public static final String MULTIWIDGET_TYPE = "multiwidget";

    public static final String DEFAULT_CSS_BASE = "composum-pages-edit-" + MULTIWIDGET_TYPE;
    public static final List<String> FORM_CSS_CLASES =
            Arrays.asList("widget multi-form-widget form-group".split(" +"));

    protected String status;

    private transient PropertyNodeSetStatus loopStatus;
    private transient String itemName;

    @Override
    protected void clear() {
        itemName = null;
        loopStatus = null;
        status = null;
        super.clear();
    }

    public String getName() {
        return getProperty();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String var) {
        status = var;
    }

    /**
     * returns the property resource to edit
     */
    @Override
    public Resource getModelResource(BeanContext context) {
        ResourceResolver resolver = context.getResolver();
        Resource resource = super.getModelResource(context);
        String propertyName = getPropertyName();
        Resource propertyResource = resolver.resolve(resource.getPath() + "/" + propertyName);
        if (ResourceUtil.isNonExistingResource(propertyResource)) {
            Resource contentResource = resource.getChild(JcrConstants.JCR_CONTENT);
            if (contentResource != null) {
                propertyResource = resolver.resolve(contentResource.getPath() + "/" + propertyName);
            }
        }
        return propertyResource;
    }

    /**
     * the component type is defined as the 'modelClass' declared in the tag
     */
    @Override
    public String getType() {
        return StringUtils.isNotBlank(modelClass) ? modelClass : PropertyNodeSet.class.getName();
    }

    protected void collectCssClasses(TagCssClasses.CssSet collection) {
        super.collectCssClasses(collection);
        collection.addAll(FORM_CSS_CLASES);
    }

    protected void collectAttributes(Map<String, Object> attributeSet) {
        attributeSet.put("data-name", getPropertyName());
        super.collectAttributes(attributeSet);
    }

    @Override
    public FormAction getDefaultAction() {
        return null;
    }

    @Override
    public int doStartTag() throws JspException {
        if (StringUtils.isBlank(cssBase)) {
            cssBase = DEFAULT_CSS_BASE;
        }
        return super.doStartTag();
    }

    @Override
    @SuppressWarnings("Duplicates")
    protected void getTagDebug(Writer writer) throws IOException {
        super.getTagDebug(writer);
        writer.append("\n    name: '").append(getName()).append("'; property: '").append(getProperty())
                .append("'; propertyName: '").append(getPropertyName())
                .append("'; relativePath: '").append(getRelativePath()).append("'");
    }

    protected String getSnippetResourceType() {
        return getWidgetResourceType(MULTIWIDGET_TYPE);
    }

    @Override
    protected void prepareTagStart() {
        @SuppressWarnings("unchecked")
        PropertyNodeSet<PropertyNode> set = (PropertyNodeSet<PropertyNode>) getModel();
        loopStatus = new PropertyNodeSetStatus(set);
        if (status != null) {
            pageContext.setAttribute(status, loopStatus, PageContext.PAGE_SCOPE);
        }
        itemName = set.getItemName();
        if (StringUtils.isBlank(itemName)) {
            itemName = getVar();
        }
        loopStatus.next();
        loopStatus.exposeVariables();
        setAttribute(MULTIWIDGET_VAR, this, PageContext.REQUEST_SCOPE);
        if (StringUtils.isNotBlank(cssBase)) {
            setAttribute(MULTIWIDGET_CSS_VAR, cssBase, PageContext.REQUEST_SCOPE);
            setAttribute(MULTIWIDGET_CSSBASE_VAR, cssBase, PageContext.REQUEST_SCOPE);
        }
    }

    @Override
    protected void renderTagStart() throws JspException, IOException {
        includeSnippet(getSnippetResourceType(), "multiwidget-start");
    }

    /**
     * Continues the iteration when appropriate -- that is, if we (a) have
     * more items and (b) don't run over our 'end' (given our 'step').
     */
    @Override
    public int doAfterBody() throws JspException {
        if (loopStatus.hasNext()) {
            try {
                includeSnippet(getSnippetResourceType(), "multiwidget-next");
            } catch (IOException ioex) {
                throw new JspException(ioex);
            }
            loopStatus.next();
            loopStatus.exposeVariables();
            return EVAL_BODY_AGAIN;
        } else {
            loopStatus.exposeVariables();
            return SKIP_BODY;
        }
    }

    @Override
    protected void renderTagEnd() throws JspException, IOException {
        includeSnippet(getSnippetResourceType(), "multiwidget-end");
    }

    @Override
    protected void finishTagEnd() {
        loopStatus.exposeCurrent(null);
        if (status != null) {
            pageContext.removeAttribute(status, PageContext.PAGE_SCOPE);
        }
    }

    // loop tag

    @Override
    public Object getCurrent() {
        return loopStatus.getCurrent();
    }

    @Override
    public LoopTagStatus getLoopStatus() {
        return loopStatus;
    }

    public class PropertyNodeSetStatus extends EditMultiLoopStatus<PropertyNode> {

        public PropertyNodeSetStatus(PropertyNodeSet<PropertyNode> set) {
            super(set);
        }

        protected PropertyNode createEmptyPlaceholder() {
            return new GenericNode(context,
                    new NonExistingResource(context.getResolver(),
                            getResource().getPath() + "/" + itemName));
        }

        protected void exposeCurrent(PropertyNode current) {
            if (current == null) {
                pageContext.removeAttribute(getVar(), PageContext.PAGE_SCOPE);
                pageContext.removeAttribute(EditWidgetTag.PROPERTY_PATH_ATTR, PageContext.PAGE_SCOPE);
                pageContext.removeAttribute(EditWidgetTag.PROPERTY_RESOURCE_ATTR, PageContext.PAGE_SCOPE);
            } else {
                String propertyPath = getPropertyName() + "/" + current.getName();
                pageContext.setAttribute(getVar(), current, PageContext.PAGE_SCOPE);
                pageContext.setAttribute(EditWidgetTag.PROPERTY_PATH_ATTR, propertyPath, PageContext.PAGE_SCOPE);
                pageContext.setAttribute(EditWidgetTag.PROPERTY_RESOURCE_ATTR,
                        current.getResource(), PageContext.PAGE_SCOPE);
            }
        }
    }
}
