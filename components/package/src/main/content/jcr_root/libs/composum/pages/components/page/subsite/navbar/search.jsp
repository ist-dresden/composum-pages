<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="navbar" type="com.composum.pages.components.model.subsite.SubsiteMenu" mode="none"
             tagName="li" cssBase="composum-pages-components-navigation-navbar_search" cssAdd="navbar-form">
    <cpp:include path="${navbar.rootPage.path}/jcr:content/search/field" mode="${navbar.root?'request':'none'}"
                 resourceType="composum/pages/components/search/field"/>
</cpp:element>
