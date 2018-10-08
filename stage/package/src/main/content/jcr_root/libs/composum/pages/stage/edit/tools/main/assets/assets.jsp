<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:element var="assets" type="com.composum.pages.stage.model.edit.FrameModel" mode="none"
             cssAdd="composum-pages-tools">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions">
        </div>
        <div class="composum-pages-tools_right-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-filter composum-pages-tools_button btn btn-default dropdown dropdown-toggle"
                        data-toggle="dropdown" title="Filter"><span
                        class="composum-pages-tools_button-label">Filter</span></button>
                <ul class="composum-pages-tools_menu dropdown-menu" role="menu">
                    <li class="${treeCssBase}_filter-value"
                        data-value="all"><a href="#" title="Show all asset types">All</a></li>
                    <li class="${treeCssBase}_filter-value"
                        data-value="assets"><a href="#" title="Asset objects only">Assets</a></li>
                    <li class="${treeCssBase}_filter-value"
                        data-value="images"><a href="#" title="Show Image files only">Images</a></li>
                    <li class="${treeCssBase}_filter-value"
                        data-value="videos"><a href="#" title="Show Video files only">Videos</a></li>
                </ul>
            </div>
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-search ${assetsCssBase}_search composum-pages-tools_button btn btn-default"
                        title="Search a page"><span
                        class="composum-pages-tools_button-label">Search</span></button>
            </div>
        </div>
    </div>
    <div class="composum-pages-tools_tree-panel tree-panel">
        <div class="composum-pages-tools_tree">
        </div>
    </div>
    <cpp:include resourceType="composum/pages/stage/edit/tools/search"/>
</cpp:element>
