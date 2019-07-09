<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:editToolbar var="frame" type="com.composum.pages.stage.model.edit.FramePage"
                 cssAdd="composum-pages-stage-edit-toolbar">
    <c:choose><c:when test="${frame.hasLanguageVariations}">
        <a class="${toolbarCssBase}_language ${toolbarCssBase}_button dropdown-toggle"
           data-toggle="dropdown" href="#"><span class="${toolbarCssBase}_language-label">xx</span><span
                class="caret"></span></a>
        <sling:include path="${frame.pagePath}" resourceType="composum/pages/stage/edit/tools/language/menu"/>
    </c:when><c:otherwise>
        <div class="${toolbarCssBase}_language ${toolbarCssBase}_button"><span class="${toolbarCssBase}_language-label">xx</span>
        </div>
        <div class="composum-pages-language-menu dropdown-menu dropdown-menu-right"></div>
    </c:otherwise>
    </c:choose>
    <a class="fa fa-refresh ${toolbarCssBase}_reload-page ${toolbarCssBase}_button" href="#"
       title="${cpn:i18n(slingRequest,'Reload Page')}"><span
            class="${toolbarCssBase}_label">${cpn:i18n(slingRequest,'Reload Page')}</span></a>
    <a class="fa fa-bullseye ${toolbarCssBase}_open-page ${toolbarCssBase}_button" href="#"
       title="${cpn:i18n(slingRequest,'Open Page')}"><span
            class="${toolbarCssBase}_label">${cpn:i18n(slingRequest,'Open Page')}</span></a>
    <a class="fa fa-eye ${toolbarCssBase}_preview ${toolbarCssBase}_button" href="?pages.mode=preview"
       title="${cpn:i18n(slingRequest,'Preview Mode')}"><span
            class="${toolbarCssBase}_label">${cpn:i18n(slingRequest,'Preview Mode')}</span></a>
    <a class="fa fa-edit ${toolbarCssBase}_edit ${toolbarCssBase}_button" href="?pages.mode=edit"
       title="${cpn:i18n(slingRequest,'Edit Mode')}"><span
            class="${toolbarCssBase}_label">${cpn:i18n(slingRequest,'Edit Mode')}</span></a>
    <a class="fa fa-external-link ${toolbarCssBase}_open-separate ${toolbarCssBase}_button" href="#" target="_blank"
       title="${cpn:i18n(slingRequest,'Open Page in separate View')}"><span
            class="${toolbarCssBase}_label">${cpn:i18n(slingRequest,'Open Page in separate View')}</span></a>
    <a class="fa fa-arrows-h ${toolbarCssBase}_handle ${toolbarCssBase}_button" href="#"
       title="${cpn:i18n(slingRequest,'Toolbar Handle')}"><span
            class="${toolbarCssBase}_label">${cpn:i18n(slingRequest,'Toolbar Handle')}</span></a>
</cpp:editToolbar>
