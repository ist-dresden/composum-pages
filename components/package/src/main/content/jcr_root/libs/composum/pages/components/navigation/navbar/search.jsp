<%@page session="false" pageEncoding="utf-8"%><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0"%><%--
--%><cpp:defineObjects />
<cpp:element var="menu" type="com.composum.pages.components.model.navigation.Menu" mode="none"
             tagName="none">
    <div class="${menuCssBase}_search navbar-form navbar-right">
        <cpp:include resourceType="composum/pages/components/search/field" path="jcr:content/search"
                     mode="request" />
    </div>
</cpp:element>
