<%@page session="false" pageEncoding="UTF-8"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<cpp:defineFrameObjects/>
<cpp:element var="site" type="com.composum.pages.stage.model.edit.site.SiteModel" mode="none"
             cssBase="composum-pages-stage-edit-site-releases" data-path="@{site.site.path}">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions">
            <cpn:text tagName="label" value="Site Releases" i18n="true"/>
        </div>
        <div class="composum-pages-tools_right-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-globe composum-pages-tools_button btn btn-default release-public"
                        title="${cpn:i18n(slingRequest,'Public Release')}..."></button>
                <button type="button"
                        class="fa fa-eye composum-pages-tools_button btn btn-default release-preview"
                        title="${cpn:i18n(slingRequest,'Preview Release')}..."></button>
                <button type="button"
                        class="fa fa-trash composum-pages-tools_button btn btn-default release-delete"
                        title="${cpn:i18n(slingRequest,'Delete Release')}..."></button>
            </div>
        </div>
    </div>
    <div class="composum-pages-stage-edit-tools-site-releases_tools-panel">
        <ul class="${siteCssBase}_list">
            <c:forEach items="${site.site.releases}" var="release">
                <sling:include resource="${release.resource}"
                               resourceType="composum/pages/stage/edit/tools/site/releases/release"/>
            </c:forEach>
        </ul>
    </div>
</cpp:element>
