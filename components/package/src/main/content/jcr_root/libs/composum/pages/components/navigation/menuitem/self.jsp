<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="menuitem" type="com.composum.pages.components.model.navigation.Menuitem"
             tagName="li" cssAdd="@{menuitemCSS}_self menu-item link@{menuitem.cssClasses}">
    <a class="${menuitemCSS}_link" href="${menuitem.url}" role="menuitem"
       aria-label="${cpn:text(menuitem.title)}">${cpn:text(menuitem.title)}</a>
</cpp:element>
