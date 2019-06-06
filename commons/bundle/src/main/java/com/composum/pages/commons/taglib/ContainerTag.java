package com.composum.pages.commons.taglib;

import com.composum.pages.commons.model.Container;
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
                out.append("<div class=\"composum-pages-container_start\">");
                out.append("&nbsp;>> ");
                out.append("<span class=\"composum-pages-container_path-hint\">");
                out.append(container.getPathHint());
                out.append("</span>");
                out.append("<span class=\"composum-pages-container_name-hint\">");
                out.append(container.getName());
                out.append("</span>");
                out.append(" <span class=\"composum-pages-container_type-hint\">(");
                out.append(container.getTypeHint());
                out.append(")</span></div>\n");
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
                out.append("<div class=\"composum-pages-container_end\">");
                out.append("&nbsp;<< ");
                out.append("<span class=\"composum-pages-container_path-hint\">");
                out.append(container.getPathHint());
                out.append("</span>");
                out.append("<span class=\"composum-pages-container_name-hint\">");
                out.append(container.getName());
                out.append("</span>");
                out.append(" <span class=\"composum-pages-container_type-hint\">(");
                out.append(container.getTypeHint());
                out.append(")</span></div>\n");
            } catch (IOException ioex) {
                LOG.error(ioex.getMessage(), ioex);
            }
        }
        super.doEndTag();
        return EVAL_PAGE;
    }
}
