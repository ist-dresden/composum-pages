<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="menu" type="com.composum.pages.components.model.navigation.Menu" mode="none"
             tagName="none">
    <cpn:link class="${menuCSS}_brand navbar-brand"
              href="${menu.currentPage.homepage.url}"><img
            class="${menuCSS}_logo" src="${menu.currentPage.homepage.logoUrl}"/><span
            class="${menuCSS}_title">${menu.currentPage.homepage.title}</span></cpn:link>
</cpp:element>
