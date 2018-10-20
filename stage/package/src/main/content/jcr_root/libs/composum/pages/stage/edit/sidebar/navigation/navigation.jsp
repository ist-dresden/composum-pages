<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:element var="tools" type="com.composum.pages.stage.tools.NavigationTools" mode="none"
             cssBase="tabbed-widget" cssClasses="composum-pages-stage-edit-sidebar-navigation">
    <cpp:include resourceType="composum/pages/stage/edit/sidebar/logo"/>
    <ul class="composum-pages-stage-edit-sidebar-navigation_tabs ${toolsCssBase}_tabs icons-only right">
        <c:forEach items="${tools.componentList}" var="component">
            <li class="${toolsCssBase}_handle" data-tab="${toolsCssBase}_tab_${component.name}">
                <a class="${toolsCssBase}_link" title="${cpn:i18n(slingRequest,component.hint)}">
                    <i class="${toolsCssBase}_icon fa fa-${component.iconClass}"></i>
                    <span class="${toolsCssBase}_label">${cpn:i18n(slingRequest,component.label)}</span>
                </a>
            </li>
        </c:forEach>
    </ul>
    <div class="${toolsCssBase}_content">
        <c:forEach items="${tools.componentList}" var="component">
            <div class="${toolsCssBase}_tab_${component.name} ${toolsCssBase}_panel">
                <cpp:include path="${component.path}"/>
            </div>
        </c:forEach>
    </div>
    <cpp:include resourceType="composum/pages/stage/edit/sidebar/navigation/context"/>
</cpp:element>
