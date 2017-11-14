<%@page session="false" pageEncoding="utf-8"%><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0"%><%--
--%><cpp:defineObjects />
<cpp:element var="menu" type="com.composum.pages.components.model.navigation.Menu" mode="none"
             tagName="none">
    <cpn:link class="${menuCssBase}_brand navbar-brand"
              href="${menu.currentPage.homepage.path}"><img
            class="${menuCssBase}_logo" src="${menu.currentPage.homepage.logoUrl}"/><span
            class="${menuCssBase}_title">${menu.currentPage.title}</span></cpn:link>
</cpp:element>
