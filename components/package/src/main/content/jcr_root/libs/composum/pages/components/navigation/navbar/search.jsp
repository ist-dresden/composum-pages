<%@page session="false" pageEncoding="utf-8"%><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0"%><%--
--%><cpp:defineObjects />
<cpp:element var="menu" type="com.composum.pages.components.model.navigation.Menu" mode="none"
             tagName="li" cssBase="composum-pages-components-navigation-navbar_search" cssAdd="navbar-form">
    <cpp:include resourceType="composum/pages/components/search/field" path="jcr:content/search"
                 mode="request"/>
</cpp:element>
