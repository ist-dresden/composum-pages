<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:element var="model" type="com.composum.pages.stage.model.edit.site.SiteModel" mode="none"
             cssBase="composum-pages-stage-edit-site-page-modified" data-path="@{model.site.path}">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions">
            <label class="tools-title" title="${cpn:i18n(slingRequest,'modified but last changes not activated yet')}">
                <input type="checkbox" class="composum-pages-stage-edit-site-page-modified_page-select-all"/>
                <span class="title-text">${cpn:i18n(slingRequest,'Modified Pages')}</span>
            </label>
        </div>
        <div class="composum-pages-tools_right-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-play activate composum-pages-tools_button btn btn-default"
                        title="${cpn:i18n(slingRequest,'Activate selected pages')}..."></button>
            </div>
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <cpp:include resourceType="composum/pages/stage/edit/site/page/filter" replaceSelectors="dropdown"/>
                <button type="button"
                        class="fa fa-refresh reload composum-pages-tools_button btn btn-default"
                        title="${cpn:i18n(slingRequest,'Reload')}..."></button>
            </div>
        </div>
    </div>
    <div class="composum-pages-stage-edit-tools-site-modified_tools-panel">
        <ul class="${modelCSS}_list">
            <c:forEach items="${model.modifiedPages}" var="page">
                <li class="${modelCSS}_listentry release-status_${page.releaseStatus.activationState}">
                    <div class="_page-state"><input type="checkbox" class="${modelCSS}_page-select"
                                                    data-path="${page.path}"/></div>
                    <div class="${modelCSS}_page-entry" data-path="${page.path}">
                        <div class="${modelCSS}_page-head">
                            <div class="${modelCSS}_page-title">${not empty page.title?cpn:text(page.title):'-- --'}</div>
                            <cpn:text class="${modelCSS}_page-time">${page.lastModifiedString}</cpn:text>
                        </div>
                        <cpn:text class="${modelCSS}_page-path" type="path">${page.siteRelativePath}</cpn:text>
                    </div>
                </li>
            </c:forEach>
        </ul>
    </div>
</cpp:element>
