<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineObjects/>
<cpp:element var="page_content" type="com.composum.pages.commons.model.PageContent" scope="request"
             cssAdd="row">
    <div class="${page_contentCssBase}_stage col-xs-12 col-sm-9 col-md-9 col-lg-10">
        <cpp:include path="stage" resourceType="composum/pages/components/composed/carousel"/>
    </div>
    <div class="${page_contentCssBase}_main-par col-xs-12 col-sm-9 col-md-9 col-lg-10">
        <cpp:include path="main" resourceType="composum/pages/components/container/parsys"/>
    </div>
</cpp:element>
