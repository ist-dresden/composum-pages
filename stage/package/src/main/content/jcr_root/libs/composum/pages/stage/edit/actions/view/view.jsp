<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editToolbar var="frame" type="com.composum.pages.stage.model.edit.FramePage"
                 cssClasses="composum-pages-stage-edit-toolbar">
    <a class="${toolbarCssBase}_language ${toolbarCssBase}_button dropdown-toggle"
       data-toggle="dropdown" href="#"><span class="${toolbarCssBase}_language-label">${frame.languageHint}</span><span class="caret"></span></a>
    <sling:include path="${frame.pagePath}" resourceType="composum/pages/stage/edit/tools/language/menu"/>
    <a class="fa fa-bullseye ${toolbarCssBase}_open-page ${toolbarCssBase}_button" href="#"
       title="${cpn:i18n(slingRequest,'Open Page')}"><span class="${toolbarCssBase}_label">${cpn:i18n(slingRequest,'Open Page')}</span></a>
    <a class="fa fa-eye ${toolbarCssBase}_preview ${toolbarCssBase}_button" href="?pages.mode.switch=preview"
       title="${cpn:i18n(slingRequest,'Preview Mode')}"><span class="${toolbarCssBase}_label">${cpn:i18n(slingRequest,'Preview Mode')}</span></a>
    <a class="fa fa-edit ${toolbarCssBase}_edit ${toolbarCssBase}_button" href="?pages.mode.switch=edit"
       title="${cpn:i18n(slingRequest,'Edit Mode')}"><span class="${toolbarCssBase}_label">${cpn:i18n(slingRequest,'Edit Mode')}</span></a>
    <a class="fa fa-arrows-h ${toolbarCssBase}_handle ${toolbarCssBase}_button" href="#"
       title="${cpn:i18n(slingRequest,'Toolbar Handle')}"><span class="${toolbarCssBase}_label">${cpn:i18n(slingRequest,'Toolbar Handle')}</span></a>
</cpp:editToolbar>
