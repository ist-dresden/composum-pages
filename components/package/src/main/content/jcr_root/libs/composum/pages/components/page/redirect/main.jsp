<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="pageModel" type="com.composum.pages.commons.model.Page" scope="request">
    <div class="composum-pages-components-page_body container-fluid">
        <div class="composum-pages-components-page_row row">
            <div class="composum-pages-components-page_main col-xs-12">
                <div class="composum-pages-components-page-redirect_redirect-hint alert alert-info">This page
                    redirects to: '<a href="${pageModel.slingTargetUrl}">${pageModel.slingTarget}</a>'
                </div>
            </div>
        </div>
    </div>
</cpp:model>