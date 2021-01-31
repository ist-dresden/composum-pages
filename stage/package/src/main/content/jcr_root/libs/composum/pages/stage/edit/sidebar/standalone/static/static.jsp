<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:element var="tools" type="com.composum.pages.stage.tools.StandaloneTools" mode="none"
             cssBase="tabbed-widget" cssAdd="composum-pages-stage-edit-sidebar-standalone"
             data-component-type="@{tools.componentTypeName}">
    <cpp:include resourceType="composum/pages/stage/edit/sidebar/logo" replaceSelectors="standalone"/>
    <ul class="composum-pages-stage-edit-sidebar-standalone_tabs ${toolsCSS}_tabs icons-only right">
        <c:forEach items="${tools.staticList}" var="component">
            <li class="${toolsCSS}_handle static-tool" data-tab="${toolsCSS}_tab_${component.key}">
                <a class="${toolsCSS}_link" title="${cpn:i18n(slingRequest,component.hint)}">
                    <i class="${toolsCSS}_icon fa fa-${component.iconClass}"></i>
                    <span class="${toolsCSS}_label">${cpn:text(component.label)}</span>
                </a>
            </li>
        </c:forEach>
    </ul>
    <div class="${toolsCSS}_content">
        <c:forEach items="${tools.staticList}" var="component">
            <div class="${toolsCSS}_tab_${component.key} ${toolsCSS}_panel static-tool"
                 data-tool-path="${component.path}">
                <cpp:include path="${component.path}"/>
            </div>
        </c:forEach>
    </div>
    <div class="composum-pages-stage-edit-sidebar-standalone_current">
        <cpp:include replaceSelectors="tile"/>
    </div>
    <div class="composum-pages-stage-edit-sidebar-standalone_buffer">
        <cpp:include replaceSelectors="tile"/>
    </div>
</cpp:element>
