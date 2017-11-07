<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineObjects/>
<cpp:element var="page_content" type="com.composum.pages.commons.model.PageContent" scope="request"
             cssAdd="row">
    <div class="${page_contentCssBase}_top col-xs-12 col-sm-12 col-md-12 col-lg-12">
        <cpp:include path="top" resourceType="composum/pages/components/container/parsys"/>
    </div>
    <div class="${page_contentCssBase}_main col-xs-12 col-sm-12 col-md-12 col-lg-12">
        <cpp:include path="main" resourceType="composum/pages/components/container/parsys"/>
    </div>
</cpp:element>
