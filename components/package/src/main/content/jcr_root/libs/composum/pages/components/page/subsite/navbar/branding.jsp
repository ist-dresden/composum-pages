<%@page session="false" pageEncoding="utf-8"%><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0"%><%--
--%><cpp:defineObjects />
<cpp:element var="navbar" type="com.composum.pages.components.model.subsite.SubsiteMenu" mode="none"
             tagName="none">
    <cpn:link class="${navbarCssBase}_brand navbar-brand"
              href="${navbar.rootPage.path}"><cpn:image
            class="${navbarCssBase}_logo" src="${navbar.rootPage.logoUrl}"/><span
            class="${navbarCssBase}_title">${navbar.rootPage.title}</span></cpn:link>
</cpp:element>
