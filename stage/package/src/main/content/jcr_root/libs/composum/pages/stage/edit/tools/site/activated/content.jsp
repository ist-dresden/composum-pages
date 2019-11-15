<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:element var="model" type="com.composum.pages.stage.model.edit.site.SiteModel" mode="none"
             cssBase="composum-pages-stage-edit-site-page-activated" data-path="@{model.site.path}">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions">
            <label class="tools-title"
                   title="${cpn:i18n(slingRequest,'pages activated with changes since the previous release')}">
                <input type="checkbox" class="composum-pages-stage-edit-site-page-activated_page-select-all"/>
                <span class="title-text">${cpn:i18n(slingRequest,'Activated')}</span>
                <cpp:include resourceType="composum/pages/stage/edit/site/page/type" replaceSelectors="context"/>
            </label>
        </div>
        <div class="composum-pages-tools_right-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-pause revert composum-pages-tools_button btn btn-default"
                        title="${cpn:i18n(slingRequest,'Revert selected content')}..."></button>
            </div>
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <cpp:include resourceType="composum/pages/stage/edit/site/page/filter" replaceSelectors="dropdown"/>
                <button type="button"
                        class="fa fa-refresh reload composum-pages-tools_button btn btn-default"
                        title="${cpn:i18n(slingRequest,'Reload')}..."></button>
            </div>
        </div>
    </div>
    <div class="composum-pages-stage-edit-tools-site-activated_tools-panel">
        <ul class="${modelCSS}_list">
            <c:forEach items="${model.releaseChanges}" var="version">
                <li class="${modelCSS}_listentry release-status_${version.contentActivationState}">
                    <div class="_page-state"><input type="checkbox" class="${modelCSS}_page-select"
                                                    data-path="${version.path}"/></div>
                    <div class="${modelCSS}_page-entry" data-path="${version.path}" data-viewer="${version.viewerUrl}">
                        <div class="${modelCSS}_page-head">
                            <div class="${modelCSS}_page-title">${not empty version.title?cpn:text(version.title):'-- --'}</div>
                            <cpn:text class="${modelCSS}_page-time">${version.lastModifiedString}</cpn:text>
                        </div>
                        <cpn:text class="${modelCSS}_page-path" type="path">${version.siteRelativePath}</cpn:text>
                    </div>
                </li>
            </c:forEach>
        </ul>
    </div>
</cpp:element>
