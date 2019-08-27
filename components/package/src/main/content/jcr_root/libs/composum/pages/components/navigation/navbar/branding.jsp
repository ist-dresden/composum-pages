<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="menu" type="com.composum.pages.components.model.navigation.Menu">
    <cpn:link class="${menuCSS}_brand navbar-brand"
              href="${menu.currentPage.homepage.url}"><img
            class="${menuCSS}_logo" src="${menu.currentPage.logoUrl}"
            alt="${cpn:i18n(slingRequest,'Site Logo')}"/></cpn:link>
</cpp:model>
