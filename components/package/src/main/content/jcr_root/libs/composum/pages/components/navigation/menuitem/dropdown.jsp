<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="menuitem" type="com.composum.pages.components.model.navigation.Menuitem"
             tagName="li" cssAdd="menu-item @{menuitem.submenu ? 'dropdown' : 'link'}@{menuitem.cssClasses}">
    <c:choose>
        <c:when test="${menuitem.submenu}">
            <a href="${menuitem.menuOnly ? '#' : menuitem.url}" class="dropdown-toggle"
               data-toggle="dropdown">${cpn:text(menuitem.title)}<span
                    class="caret"></span></a>
            <cpp:include resourceType="composum/pages/components/navigation/submenu"/>
        </c:when>
        <c:otherwise>
            <a href="${menuitem.url}">${cpn:text(menuitem.title)}</a>
        </c:otherwise>
    </c:choose>
</cpp:element>
