/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.components.model.codeblock;

import com.composum.platform.commons.content.service.ContentRefService;
import com.composum.pages.commons.model.Element;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CodeBlock extends Element {

    private static final Logger LOG = LoggerFactory.getLogger(CodeBlock.class);

    public static final String PN_CODE = "code";
    public static final String PN_CODE_REF = "codeRef";
    public static final String CODE_ENCODING = "UTF-8";

    public static final String PN_BORDERED = "bordered";
    public static final String PN_COLLAPSIBLE = "collapsible";
    public static final String PN_COLLAPSED = "collapsed";
    public static final String PN_WRAP_LINES = "wrapLines";

    public static final String PN_COPYRIGHT = "copyright";

    public static final String PN_LANGUAGE = "language";
    public static final String PN_SHOW_LANGUAGE = "showLanguage";

    public static final String PN_SERVICE_URI = "serviceUri";

    public static final String TYPE_PANEL = "panel";
    public static final String TYPE_SIMPLE = "simple";

    private transient String title;
    private transient String code;
    private transient String copyright;
    private transient String codeLanguage;
    private transient String serviceUri;
    private transient Boolean showLanguage;

    private transient Boolean wrapLines;
    private transient Boolean bordered;
    private transient Boolean collapsible;
    private transient String collapsed;

    public boolean isValid() {
        return StringUtils.isNotBlank(getCode());
    }

    public boolean isBordered() {
        if (bordered == null) {
            bordered = getProperty(PN_BORDERED, Boolean.FALSE);
        }
        return bordered;
    }

    public boolean isCollapsible() {
        if (collapsible == null) {
            collapsible = getProperty(PN_COLLAPSIBLE, Boolean.FALSE);
        }
        return collapsible;
    }

    public boolean isCollapsed() {
        if (collapsed == null) {
            collapsed = getProperty(PN_COLLAPSED, "");
        }
        return StringUtils.isNotBlank(collapsed);
    }

    public boolean isWrapLines() {
        if (wrapLines == null) {
            wrapLines = getProperty(PN_WRAP_LINES, Boolean.FALSE);
        }
        return wrapLines;
    }

    public boolean isShowHeading() {
        return isCollapsible()
                || StringUtils.isNotBlank(getTitle())
                || isShowLanguage();
    }

    public boolean isUsePanel() {
        return isBordered() || isShowHeading();
    }

    public String getRenderType() {
        return isUsePanel() ? TYPE_PANEL : TYPE_SIMPLE;
    }

    public String getClasses() {
        List<String> classes = new ArrayList<>();
        if (isUsePanel()) {
            classes.add("code-panel");
        } else {
            classes.add("code-simple");
        }
        if (isWrapLines()) {
            classes.add("wrap-lines");
        }
        if (isBordered()) {
            classes.add("bordered");
        }
        if (isCollapsible()) {
            classes.add("collapsible");
        }
        if (isCollapsed()) {
            classes.add("collapsed");
            classes.add("collapsed-" + collapsed);
        }
        return StringUtils.join(classes, " ");
    }

    public boolean isHasCopyright() {
        return StringUtils.isNotBlank(getCopyright());
    }

    public String getCopyright() {
        if (copyright == null) {
            copyright = getProperty(PN_COPYRIGHT, "");
        }
        return copyright;
    }

    public boolean isShowLanguage() {
        if (showLanguage == null) {
            showLanguage = getProperty(PN_SHOW_LANGUAGE, Boolean.FALSE)
                    && StringUtils.isNotBlank(getCodeLanguage());
        }
        return showLanguage;
    }

    public String getCodeLanguage() {
        if (codeLanguage == null) {
            codeLanguage = getProperty(PN_LANGUAGE, "");
        }
        return codeLanguage;
    }

    public String getServiceUri() {
        if (serviceUri == null) {
            serviceUri = getProperty(PN_SERVICE_URI, "");
        }
        return serviceUri;
    }

    public String getCode() {
        if (code == null) {
            code = getProperty(PN_CODE, "");
            if (StringUtils.isBlank(code)) {
                ContentRefService service = context.getService(ContentRefService.class);
                String codeRef = getProperty(PN_CODE_REF, "");
                String servicveUri = getServiceUri();
                if (StringUtils.isNotBlank(servicveUri)) {
                    codeRef = servicveUri + codeRef;
                }
                code = service.getReferencedContent(resolver, codeRef);
                if (StringUtils.isBlank(code)) {
                    code = service.getRenderedContent(context.getRequest(), codeRef, false);
                }
            }
        }
        return code;
    }
}
