<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="content" type="com.composum.pages.commons.model.PageContent" scope="request"
           cssBase="composum-pages-components-page_content">
    <div class="${contentCssBase}_main row">
        <div class="composum-pages-components-element-title_text">
            <h1 class="composum-pages-components-element-title_title">Test Page</h1>
            <h2 class="composum-pages-components-element-title_subtitle">Properties Edit Test</h2>
        </div>
        <cpp:include path="properties" resourceType="composum/pages/components/test/properties"/>
    </div>
</cpp:model>
