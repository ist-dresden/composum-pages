<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="navbar" type="com.composum.pages.components.model.page.SinglePageMenu" mode="none"
             tagName="none">
    <cpn:link class="${navbarCSS}_brand navbar-brand"
              href="${navbar.singlePage.logoLinkUrl}"><cpn:image
            class="${navbarCSS}_logo" src="${navbar.singlePage.logoImageUrl}"/><span
            class="${navbarCSS}_title">${cpn:text(navbar.singlePage.title)}</span></cpn:link>
</cpp:element>
