package com.composum.pages.commons.taglib;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.properties.Language;
import com.composum.pages.commons.service.TrackingService;
import com.composum.pages.commons.util.LinkUtil;
import com.composum.sling.cpnl.CpnlTagSupport;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;

/**
 * the 'token' tag inserts an image to track the request to load a page; the image URL is built
 * by the mapped pth of the page resource with the 'token' selector and the 'png' image extension;
 * if available the referer of the page request is added as a bae64 encoded suffix; the 'token'
 * selector is served by the 'TokenServlet'; this servlet is implementing the tracking functionality
 */
public class TokenTag extends CpnlTagSupport {

    private static final Logger LOG = LoggerFactory.getLogger(TokenTag.class);

    protected Object test;
    private transient Boolean testResult;

    protected void clear() {
        testResult = null;
        test = null;
    }

    /**
     * the 'test' expression for conditional tags
     */
    public void setTest(Object value) {
        test = value;
    }

    /**
     * evaluates the test expression if present and returns the evaluation result; default: 'true'
     */
    protected boolean getTestResult() {
        if (testResult == null) {
            testResult = getExpressionUtil().eval(test, test instanceof Boolean ? (Boolean) test : Boolean.TRUE);
        }
        return testResult;
    }

    @Override
    public int doEndTag() throws JspException {
        if (getTestResult()) {
            try {
                Page page = (Page) request.getAttribute(PagesConstants.RA_CURRENT_PAGE);
                String url = LinkUtil.getUrl(
                        request,
                        page.getPath(),
                        "token",
                        TrackingService.EXT_PNG);
                String referer = request.getHeader("Referer");
                if (StringUtils.isNotBlank(referer)) {
                    url += "/" + Base64.encodeBase64URLSafeString(referer.getBytes(TrackingService.CHARSET));
                }
                Language language = page.getLanguage();
                if (!language.equals(page.getPageLanguages().getDefaultLanguage())) {
                    url += "?pages.locale=" + language.getKey();
                }
                JspWriter writer = pageContext.getOut();
                writer.write("<img class=\"composum-pages-token\" src=\"");
                writer.write(url);
                writer.write("\"/>");
            } catch (IOException ioex) {
                LOG.error(ioex.getMessage(), ioex);
            }
        }
        return super.doEndTag();
    }
}
