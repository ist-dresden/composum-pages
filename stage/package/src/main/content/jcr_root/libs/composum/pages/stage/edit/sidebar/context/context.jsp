<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:element var="tools" type="com.composum.pages.stage.tools.ContextTools" mode="none"
             cssBase="tabbed-widget" cssAdd="composum-pages-stage-edit-sidebar-context"
             data-component-type="@{tools.componentTypeName}">
    <ul class="composum-pages-stage-edit-sidebar-context_tabs ${toolsCSS}_tabs icons-only">
        <cpn:div test="${tools.status != null}" class="${toolsCSS}_status">
            <sling:include path="${tools.status.path}"/>
        </cpn:div>
        <c:forEach items="${tools.componentList}" var="component">
            <li class="${toolsCSS}_handle" data-tab="${toolsCSS}_tab_${component.key}">
                <a class="${toolsCSS}_link" title="${component.hint}">
                    <i class="${toolsCSS}_icon fa fa-${component.iconClass}"></i>
                    <span class="${toolsCSS}_label">${cpn:text(component.label)}</span>
                </a>
            </li>
        </c:forEach>
    </ul>
    <div class="${toolsCSS}_content">
        <c:forEach items="${tools.componentList}" var="component">
            <div class="${toolsCSS}_tab_${component.key} ${toolsCSS}_panel"
                 data-tool-path="${component.path}">
                <sling:include path="${component.path}"/>
            </div>
        </c:forEach>
    </div>
    <sling:include replaceSelectors="tile"/>
</cpp:element>
