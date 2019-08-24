<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="page_content" type="com.composum.pages.commons.model.PageContent" scope="request">
    <div class="composum-pages-components-page_content_stage">
        <cpp:include path="stage" resourceType="composum/pages/components/composed/carousel"/>
    </div>
    <div class="composum-pages-components-page_content_main">
        <cpp:include path="main" resourceType="composum/pages/components/container/parsys"/>
    </div>
</cpp:element>
