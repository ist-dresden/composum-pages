<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:element var="site" type="com.composum.pages.stage.model.edit.site.SiteModel" mode="none"
             cssBase="composum-pages-stage-edit-site-page-activated" data-path="@{site.site.path}">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions">
            <label title="${cpn:i18n(slingRequest,'pages activated with changes since the previous release')}">
                <input type="checkbox" class="composum-pages-stage-edit-site-page-activated_page-select-all"/>
                <span>${cpn:i18n(slingRequest,'Activated Pages')}</span>
            </label>
        </div>
        <div class="composum-pages-tools_right-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-pause revert composum-pages-tools_button btn btn-default"
                        title="${cpn:i18n(slingRequest,'Revert selected pages')}..."></button>
            </div>
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-refresh reload composum-pages-tools_button btn btn-default"
                        title="${cpn:i18n(slingRequest,'Reload')}..."></button>
            </div>
        </div>
    </div>
    <div class="composum-pages-stage-edit-tools-site-activated_tools-panel">
        <ul class="${siteCssBase}_list">
            <c:forEach items="${site.site.releaseChanges}" var="page">
                <li class="${siteCssBase}_listentry">
                    <input type="checkbox" class="${siteCssBase}_page-select" data-path="${page.path}"/>
                    <div class="${siteCssBase}_page-entry" data-path="${page.path}">
                        <div class="${siteCssBase}_page-head">
                            <div class="${siteCssBase}_page-title">${not empty page.title?cpn:text(page.title):'-- --'}</div>
                            <cpn:text class="${siteCssBase}_page-time">${page.lastModifiedString}</cpn:text>
                        </div>
                        <cpn:text class="${siteCssBase}_page-path" type="path">${page.siteRelativePath}</cpn:text>
                    </div>
                </li>
            </c:forEach>
        </ul>
    </div>
</cpp:element>
