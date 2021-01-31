package com.composum.pages.stage.model.edit;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.util.LinkUtil;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.XSS;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.composum.pages.commons.PagesConstants.PAGES_EDITOR_PATH;
import static com.composum.pages.commons.PagesConstants.PAGES_FRAME_PATH;

public class FramePage extends Page {

    public static final String HEADER_REFERER = "Referer";
    public static final Pattern EDITOR_URL = Pattern.compile(
            "^((https?:)?//[^/]+)?((/.*)?(?<path>/bin/(pages|edit)\\.html))((?<suffix>/[^?]*)?([?].*)?)?$",
            Pattern.CASE_INSENSITIVE);
    public static final Pattern GENERAL_URL = Pattern.compile(
            "^((https?:)?//[^/]+)?(?<path>[^.]+\\.html)((?<suffix>/[^?]*)?([?].*)?)?$",
            Pattern.CASE_INSENSITIVE);
    public static final String DEFAULT_EDITOR_URI = PAGES_FRAME_PATH + ".html";

    private transient Page page;
    private transient Resource pageResource;
    private transient String pagePath;

    private transient String editorUri;
    private transient String editorSuffix;

    private transient DisplayMode.Value displayMode;

    public FramePage() {
        super();
    }

    /**
     * @return the resource of the edited page (this is not the initializers resource)
     */
    @Nonnull
    @Override
    public Resource getResource() {
        return getPageResource();
    }

    /**
     * @return the initializers resource (the edit frame component resource)
     */
    public Resource getFrameResource() {
        return super.getResource();
    }

    public Page getPage() {
        if (page == null) {
            page = getPageManager().createBean(context, getPageResource());
        }
        return page;
    }

    /**
     * @return the resource of the edited page
     */
    public Resource getPageResource() {
        if (pageResource == null) {
            String path = getPagePath();
            if (pageResource == null) {
                pageResource = resolver.resolve(path);
            }
        }
        return pageResource;
    }

    /**
     * @return the path of the edited page
     */
    public String getPagePath() {
        if (pagePath == null) {
            pagePath = XSS.filter(context.getRequest().getRequestPathInfo().getSuffix());
            if (StringUtils.isBlank(pagePath)) {
                pagePath = "/";
            } else {
                pageResource = resolver.resolve(pagePath);
                if (!ResourceUtil.isNonExistingResource(pageResource)) {
                    pagePath = pageResource.getPath();
                } else {
                    if (pagePath.endsWith(".html")) {
                        pagePath = pagePath.substring(0, pagePath.length() - 5);
                    }
                }
            }
        }
        return pagePath;
    }

    /**
     * @return the URL of the edited page
     */
    public String getPageUrl() {
        return LinkUtil.getUrl(context.getRequest(), getPage().getUrl());
    }

    /**
     * send a redirect to the edited page as response of the current context
     */
    public void redirectToPage() throws IOException {
        context.getResponse().sendRedirect(getPageUrl());
    }

    public boolean isHasLanguageVariations() {
        return getPage().getPageLanguages().getLanguages().size() > 1;
    }

    // frame type

    public boolean isNavigator() {
        return getEditorUri().equals(DEFAULT_EDITOR_URI);
    }

    public String getNavigatorUri() {
        SlingHttpServletRequest request = getContext().getRequest();
        getEditorUri();
        return request.getContextPath() + DEFAULT_EDITOR_URI + editorSuffix;
    }

    public boolean isStandalone() {
        return getEditorUri().equals(PAGES_EDITOR_PATH + ".html");
    }

    public String getStandaloneUri() {
        SlingHttpServletRequest request = getContext().getRequest();
        getEditorUri();
        return request.getContextPath() + PAGES_EDITOR_PATH + ".html" + editorSuffix;
    }

    protected String getEditorUri() {
        if (editorUri == null) {
            editorUri = DEFAULT_EDITOR_URI;
            SlingHttpServletRequest request = getContext().getRequest();
            if (request != null) {
                String requestUri = request.getRequestURI();
                Matcher editor = EDITOR_URL.matcher(requestUri);
                if (editor.matches()) {
                    editorSuffix = editor.group("suffix");
                    editorUri = editor.group("path");
                } else {
                    String referer = request.getHeader(HEADER_REFERER);
                    if (StringUtils.isNotBlank(referer)) {
                        editor = EDITOR_URL.matcher(referer);
                        if (editor.matches()) {
                            editorUri = editor.group("path");
                            Matcher general = GENERAL_URL.matcher(requestUri);
                            if (general.matches()) {
                                editorSuffix = general.group("suffix");
                            } else {
                                editorSuffix = editor.group("suffix");
                            }
                        }
                    }
                }
            }
        }
        return editorUri;
    }

    // view mode

    public boolean isEditMode() {
        DisplayMode.Value mode = getDisplayMode();
        return mode == DisplayMode.Value.EDIT || mode == DisplayMode.Value.DEVELOP;
    }

    public boolean isDevelopMode() {
        return getDisplayMode() == DisplayMode.Value.DEVELOP;
    }

    /**
     * @return the requested display mode (the current could be overloaded during 'include')
     */
    public DisplayMode.Value getDisplayMode() {
        if (displayMode == null) {
            displayMode = DisplayMode.requested(getContext());
        }
        return displayMode;
    }

    /**
     * @return a readable string of the current display mode
     */
    public String getDisplayModeHint() {
        DisplayMode.Value mode = getDisplayMode();
        switch (mode) {
            case DEVELOP:
                mode = DisplayMode.Value.EDIT;
            default:
                break;
        }
        return mode.name();
    }

    /**
     * @return the Sling selector key for the current display mode
     */
    public String getDisplayModeSelector() {
        return getDisplayModeHint().toLowerCase();
    }
}
