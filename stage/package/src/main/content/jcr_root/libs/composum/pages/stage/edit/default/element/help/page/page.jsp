<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="page_content" type="com.composum.pages.commons.model.PageContent" scope="request"
             cssBase="composum-pages-components-help_content">
    <div class="${page_contentCssBase}_main">
        <cpp:include path="main" resourceType="composum/pages/components/container/parsys"/>
    </div>
</cpp:element>
