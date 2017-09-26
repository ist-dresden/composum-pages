<%@page session="false" pageEncoding="utf-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2"%><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0"%><%--
--%><cpp:defineObjects />
<cpp:element var="menuitem" type="com.composum.pages.components.model.navigation.Menuitem"
               tagName="li" cssAdd="menu-item link@{menuitem.cssClasses}">
    <a class="${menuitemCssBase}_link" href="${cpn:url(slingRequest,menuitem.path)}">
            ${cpn:text(menuitem.title)}
    </a>
</cpp:element>
