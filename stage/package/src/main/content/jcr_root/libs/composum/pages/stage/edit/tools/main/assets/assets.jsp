<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:element var="assets" type="com.composum.pages.stage.model.edit.AssetsModel" mode="none"
             cssAdd="composum-pages-tools">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions">
        </div>
        <div class="composum-pages-tools_right-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa ${assetsCSS}_toggle-view composum-pages-tools_button btn btn-default"
                        title="${cpn:i18n(slingRequest,'Search an asset object')}"><cpn:text
                        tagName="span" class="composum-pages-tools_button-label"
                        i18n="true">Search</cpn:text></button>
            </div>
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button" title="Filter" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"
                        class="fa fa-filter composum-pages-tools_button btn btn-default dropdown dropdown-toggle"><span
                        class="composum-pages-tools_button-label">Filter</span></button>
                <ul class="${assetsCSS}_filter composum-pages-tools_menu dropdown-menu" role="menu">
                    <c:forEach items="${assets.assetFilterSet}" var="filter">
                        <li class="${assetsCSS}_filter-value" data-value="${filter.key}"><a
                                href="#"
                                title="${cpn:i18n(slingRequest,filter.hint)}">${cpn:i18n(slingRequest,filter.label)}</a>
                        </li>
                    </c:forEach>
                </ul>
                <button type="button"
                        class="fa fa-refresh ${assetsCSS}_refresh composum-pages-tools_button btn btn-default"
                        title="${cpn:i18n(slingRequest,'Refresh current node')}"><cpn:text
                        tagName="span" class="composum-pages-tools_button-label"
                        i18n="true">Refresh</cpn:text></button>
            </div>
        </div>
    </div>
    <div class="composum-pages-tools_tree-panel tree-panel">
        <div class="composum-pages-tools_tree">
        </div>
    </div>
    <div class="composum-pages-tools_tree_asset-tile tree-panel-preview">
    </div>
    <cpp:include resourceType="composum/pages/stage/edit/tools/search" replaceSelectors="asset"/>
</cpp:element>
