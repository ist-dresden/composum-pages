package com.composum.pages.stage.model.edit;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.PageContent;
import com.composum.pages.commons.service.ResourceManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FrameComponent extends FrameModel {

    private transient ResourceManager.Template template;
    private transient ResourceManager.Design design;

    public FrameComponent() {
    }

    @Nonnull
    public String getTitleOrName() {
        return getComponent().getTitleOrName();
    }

    @Nonnull
    public String getTemplatePath() {
        ResourceManager.Template template = getTemplate();
        return template != null ? template.getPath() : "--";
    }

    @Nonnull
    public String getDesignPath() {
        ResourceManager.Design design = getDesign();
        return design != null ? design.getPath() : "--";
    }

    @Nullable
    public ResourceManager.Design getDesign() {
        if (design == null) {
            ResourceManager.Template template = getTemplate();
            if (template != null) {
                Page page = getContainingPage();
                PageContent content;
                if (page != null && (content = page.getContent()) != null) {
                    String contentPath = content.getPath() + "/";
                    String relativePath = getResource().getPath();
                    if (relativePath.startsWith(contentPath)) {
                        relativePath = relativePath.substring(contentPath.length());
                    }
                    design = template.getDesign(content.getResource(), relativePath, null);
                }
            }
        }
        return design;
    }

    @Nullable
    public ResourceManager.Template getTemplate() {
        if (template == null) {
            template = getResourceManager().getTemplateOf(getResource());
        }
        return template;
    }
}
