<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="pageModel" type="com.composum.pages.options.blog.model.BlogSequence">
    <%
        if (!pageModel.redirectRequest()) {
    %>
    <div class="composum-pages-components-page_body container-fluid">
        <div class="composum-pages-components-page_row row">
            <div class="composum-pages-components-page_main col-xs-12">
                <div class="composum-pages-components-page-redirect_redirect-hint alert alert-info">This page
                    redirects to: '<a href="${pageModel.targetUrl}">${pageModel.targetPath}</a>'
                </div>
            </div>
        </div>
    </div>
    <%
        }
    %>
</cpp:model>
