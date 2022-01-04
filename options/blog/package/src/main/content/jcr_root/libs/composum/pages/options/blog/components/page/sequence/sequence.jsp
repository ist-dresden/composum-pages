<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<cpp:defineObjects/>
<cpp:model var="pageModel" type="com.composum.pages.options.blog.model.BlogSequence">
    <%
        if (!pageModel.redirectRequest()) {
    %>
    <!DOCTYPE html>
    <html ${currentPage.htmlLangAttribute} ${currentPage.htmlDirAttribute} class="${currentPage.htmlClasses}"
                                                                           data-context-path="${slingRequest.contextPath}"
                                                                           data-locale="${currentPage.locale}">
    <cpp:head>
        <sling:call script="head.jsp"/>
    </cpp:head>
    <cpp:body cssAdd="composum-pages-components-page">
        <sling:call script="navbar.jsp"/>
        <div class="composum-pages-components-page_body container-fluid">
            <div class="composum-pages-components-page_content">
                <div class="composum-pages-components-page_content_top">
                    <cpp:include path="top" resourceType="composum/pages/components/container/iparsys"/>
                </div>
                <main class="composum-pages-components-page_content_main">
                    <div class="composum-pages-components-page_main-top">
                    </div>
                    <div class="composum-pages-components-page_main-body">
                        <div class="composum-pages-components-page-redirect_redirect-hint alert alert-info">This page
                            redirects to: '<a href="${pageModel.targetUrl}">${pageModel.targetPath}</a>'
                        </div>
                    </div>
                </main>
                <div class="composum-pages-components-page_content_bottom">
                    <cpp:include path="bottom" resourceType="composum/pages/components/container/iparsys"/>
                </div>
            </div>
        </div>
        <sling:call script="script.jsp"/>
    </cpp:body>
    </html>
    <%
        }
    %>
</cpp:model>
