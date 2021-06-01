<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="menu" type="com.composum.pages.components.model.navigation.SidebarMenu" mode="none"
             cssBase="composum-pages-components-navigation-sidebar">
    <ul class="${menuCSS}_list nav" role="menu">
        <cpp:include path="${menu.path}" resourceType="composum/pages/components/navigation/menuitem"
                     replaceSelectors="sidebarroot"/>
    </ul>
</cpp:element>
