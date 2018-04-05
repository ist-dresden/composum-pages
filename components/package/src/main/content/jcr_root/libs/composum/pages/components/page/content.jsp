<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineObjects/>
<cpp:element var="page_content" type="com.composum.pages.commons.model.PageContent" scope="request"
             cssAdd="composum-pages-components-page_content row">
    <div class="composum-pages-components-page_content_top col-xs-12">
        <cpp:include path="top" resourceType="composum/pages/components/container/parsys"/>
    </div>
    <div class="composum-pages-components-page_content_main col-xs-12">
        <cpp:include path="main" resourceType="composum/pages/components/container/parsys"/>
    </div>
</cpp:element>
