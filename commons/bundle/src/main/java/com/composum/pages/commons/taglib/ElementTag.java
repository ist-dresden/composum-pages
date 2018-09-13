package com.composum.pages.commons.taglib;

import com.composum.pages.commons.model.Container;
import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.util.TagCssClasses;
import com.composum.sling.core.request.DomIdentifiers;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.io.IOException;
import java.util.Map;

/**
 * the tag to render a Pages Sling component
 * such a component is rendering an HTML tag with the components content within
 * the tag around contains all additional information for the edit tools if edit mode is set
 */
public class ElementTag extends AbstractWrappingTag {

    public static final String NONE_TAG = "none";
    public static final String DEFAULT_TAG = "div";

    public static final String COMPONENT_EDIT_BODY_CLASSES = "composum-pages-component";
    public static final String ELEMENT_EDIT_CSS_CLASS = "composum-pages-element";

    public static final String STYLE_PROPERTY = "style";

    protected String id;
    protected String tagId;
    protected String tagName;
    protected DisplayMode.Value displayMode;

    @Override
    protected void clear() {
        displayMode = null;
        tagName = null;
        tagId = null;
        id = null;
        super.clear();
    }

    /**
     * the optional DOM tree id for the HTML tag
     */
    public String getTagId() {
        return tagId;
    }

    public void setTagId(String id) {
        tagId = id;
    }

    public String getId() {
        if (id == null) {
            id = getTagId();
            if (StringUtils.isNotBlank(id)) {
                id = eval(id, id);
            }
        }
        return id;
    }

    /**
     * the tag name to render the wrapping tag (default: 'div')
     */
    public String getTagName() {
        return tagName;
    }

    public void setTagName(String name) {
        tagName = name;
    }

    public boolean isWithTag() {
        return !NONE_TAG.equalsIgnoreCase(getTagName());
    }

    /**
     * the edit mode for this render step and all included components
     */
    @Override
    public void setMode(String mode) {
        super.displayMode = displayMode = DisplayMode.Value.valueOf(mode.toUpperCase());
    }

    /**
     * the edit CSS class for this component (normally 'element'; extension hook used by the container tag)
     */
    protected String getElementCssClass() {
        return ELEMENT_EDIT_CSS_CLASS;
    }

    /**
     * builds the list of CSS classes for the wrapping tag
     */
    protected void collectCssClasses(TagCssClasses.CssSet collection) {
        ValueMap values = resource.adaptTo(ValueMap.class);
        if (values != null) {
            collection.add(values.get(STYLE_PROPERTY, ""));
        }
        super.collectCssClasses(collection);
        if (isEditMode()) {
            collection.add(COMPONENT_EDIT_BODY_CLASSES);
            collection.add(getElementCssClass());
        }
    }

    /**
     * builds the list of tag attributes for the wrapping tag
     */
    protected void collectAttributes(Map<String, String> attributeSet) {
        String value;
        if (StringUtils.isNotBlank(value = getId())) {
            attributeSet.put(TAG_ID, value);
        }
        super.collectAttributes(attributeSet);
        if (isEditMode()) {
            attributeSet.put(PAGES_EDIT_DATA_PATH, resource.getPath());
            addEditAttributes(attributeSet, resource, resource.getResourceType());
            if (isDraggable()) {
                attributeSet.put("draggable", "true");
            }
        }
    }

    // hierarchy

    protected boolean isDraggable() {
        return getContainer() != null;
    }

    protected Resource getContainer() {
        Resource parent = resource.getParent();
        while (parent != null) {
            if (Container.isContainer(resourceResolver, parent, null)) {
                // if parent is a container this parent is that we are searching for
                // if we itself are a dynamic element of the container
                Element element = new Element(context, resource);
                Container container = new Container(context, parent);
                return container.isAllowedElement(element) && element.isAllowedContainer(container)
                        ? parent  // embedded dynamically
                        : null;   // placed inside by a static include
            }
            if (Element.isElement(resourceResolver, parent, null) || Page.isPageContent(parent) || Page.isPage(parent)) {
                // if parent is an element or page is reached the resource itself is not a container element
                return null;
            }
            parent = parent.getParent();
        }
        return null;
    }

    // rendering

    /**
     * if this returns 'false' nothing is rendered, no wrapping tag and no content within
     * this is used if the option 'test' attribute is set; if the test fails this returns 'false'...
     */
    @Override
    protected boolean renderTag() {
        return getTestResult();
    }

    /**
     * setup before the rendering starts - sets the display mode if specified
     */
    @Override
    protected void prepareTagStart() {
        String var = getVar();
        if (displayMode != null) {
            DisplayMode.get(context).push(displayMode);
        }
        if (StringUtils.isBlank(tagName)) {
            tagName = DEFAULT_TAG;
        }
        Model model = (Model) getModel();
        if (model != null) {
            setAttribute(var + "Id",
                    DomIdentifiers.getInstance(context).getElementId(model),
                    getVarScope());
        }
    }

    /**
     * renders the tag start HTML element if not 'none' is set for the tag name (tagName='none')
     * if 'none' is set the content is rendered only not the wrapping tag (with no edit capability)
     */
    @Override
    protected void renderTagStart() throws IOException {
        if (isWithTag()) {
            out.append("<").append(tagName).append(" ").append(getAttributes()).append(">\n");
        }
    }

    /**
     * renders the tag end HTML element if not 'none' is set for the tag name
     */
    @Override
    protected void renderTagEnd() throws IOException {
        if (isWithTag()) {
            out.append("</").append(tagName).append(">\n");
        }
    }

    /**
     * cleanup after rendering ends - resets the display mode if changed
     */
    @Override
    protected void finishTagEnd() {
        if (displayMode != null) {
            DisplayMode.get(context).pop();
        }
    }
}
