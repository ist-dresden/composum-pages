<%@page session="false" pageEncoding="utf-8"%><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0"%><%--
--%><cpp:defineObjects />
<div class="${menuCssBase}_search navbar-form navbar-right">
    <cpp:include resourceType="composum/pages/components/element/search/field" path="jcr:content/search"
                 mode="request" />
</div>
