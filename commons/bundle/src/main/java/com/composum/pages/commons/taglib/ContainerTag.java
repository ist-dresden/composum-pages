package com.composum.pages.commons.taglib;

import com.composum.pages.commons.model.Component;
import com.composum.pages.commons.model.Container;
import com.composum.pages.commons.request.DisplayMode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import java.io.IOException;

/**
 * the tag to render a Pages Sling component
 */
public class ContainerTag extends ElementTag {

    private static final Logger LOG = LoggerFactory.getLogger(ContainerTag.class);

    public static final String CONTAINER_EDIT_CSS_CLASS = "composum-pages-container";

    protected boolean decoration = true;

    public void setDecoration(boolean decoration) {
        this.decoration = decoration;
    }

    @Override
    protected void clear() {
        decoration = true;
        super.clear();
    }

    @Override
    protected String getElementCssClass() {
        return CONTAINER_EDIT_CSS_CLASS;
    }

    @Override
    public int doStartTag() throws JspException {
        int result = super.doStartTag();
        if (decoration && isEditMode() && isWithTag() && component instanceof Container) {
            Container container = (Container) component;
            try {
                out.append("<div class=\"composum-pages-container_wrapper\"");
                if (DisplayMode.isDevelopMode(context)) {
                    out.append(" title=\"type: ").append(container.getType()).append("\"");
                }
                out.append(">");
                writeContainerDecoration(container);
            } catch (IOException ioex) {
                LOG.error(ioex.getMessage(), ioex);
            }
            result = EVAL_BODY_INCLUDE;
        }
        return result;
    }

    @Override
    public int doEndTag() throws JspException {
        if (decoration && isEditMode() && isWithTag() && component instanceof Container) {
            Container container = (Container) component;
            try {
                out.append("</div>");
            } catch (IOException ioex) {
                LOG.error(ioex.getMessage(), ioex);
            }
        }
        super.doEndTag();
        return EVAL_PAGE;
    }

    protected void writeContainerDecoration(Container container) throws IOException {
        String typeLabel = null;
        String typeHint = null;
        Component component = container.getComponent();
        if (component != null) {
            typeLabel = component.getTitle();
            typeHint = container.getTypeHint();
        }
        if (StringUtils.isBlank(typeLabel)) {
            typeLabel = container.getTypeHint();
        }
        out.append("<div class=\"composum-pages-container_wrapper-hints\"");
        if (DisplayMode.isDevelopMode(context)) {
            out.append(" title=\"type: ").append(container.getType()).append("\"");
        }
        out.append(">");
        out.append("<span class=\"composum-pages-container_type-label\">").append(typeLabel).append("</span> (");
        if (DisplayMode.isDevelopMode(context)) {
            if (StringUtils.isNotBlank(typeHint)) {
                out.append("<span class=\"composum-pages-container_type-hint\">").append(typeHint).append("</span>");
            }
            out.append("<span class=\"composum-pages-container_path-hint\">").append(container.getPathHint()).append("</span>");
        }
        out.append("<span class=\"composum-pages-container_name-hint\">").append(container.getName()).append("</span>");
        out.append(")</div>\n");
    }
}
