<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><cpp:defineObjects/>
<cpp:element var="content" type="com.composum.pages.commons.model.PageContent" scope="request">
    <div class="${contentCssBase}_top-row row">
        <div class="${contentCssBase}_top col-xs-12 col-sm-12 col-md-12 col-lg-12">
            <cpp:include path="top" resourceType="composum/pages/components/container/parsys"/>
        </div>
    </div>
    <div class="${contentCssBase}_main">
        <cpp:include path="main" resourceType="composum/pages/components/container/parsys"/>
    </div>
</cpp:element>
