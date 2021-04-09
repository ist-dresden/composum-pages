<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="menu" type="com.composum.pages.components.model.navigation.NavbarMenu" mode="none"
             cssBase="composum-pages-components-navigation-navbar" role="navigation"
             cssAdd="navbar navbar-default navbar-fixed-top navbar-width-@{menu.sizeCss}">
    <div class="${menuCSS}_container container-fluid">
        <div class="${menuCSS}_header navbar-header">
            <button type="button" class="${menuCSS}_navbar-toggle navbar-toggle" data-toggle="collapse"
                    data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <sling:call script="branding.jsp"/>
        </div>
        <nav class="${menuCSS}_navbar-left navbar-collapse collapse navbar-left">
            <%--ul class="${menuCSS}_navbar-nav nav navbar-nav">
                <cpp:include resourceType="composum/pages/components/navigation/breadcrumbs"
                             replaceSelectors="dropdown"/>
            </ul--%>
        </nav>
        <nav class="${menuCSS}_navbar-right navbar-collapse collapse navbar-right">
            <ul class="${menuCSS}_navbar-nav nav navbar-nav">
                <c:forEach items="${menu.menuItems}" var="item">
                    <cpp:include path="${item.content.path}"
                                 resourceType="composum/pages/components/navigation/menuitem"
                                 replaceSelectors="navbar"/>
                </c:forEach>
                <sling:call script="search.jsp"/>
                <sling:call script="language.jsp"/>
            </ul>
        </nav>
    </div>
</cpp:element>
